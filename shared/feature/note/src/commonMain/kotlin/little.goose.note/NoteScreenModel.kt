package little.goose.note

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text2.input.TextFieldState
import androidx.compose.foundation.text2.input.delete
import androidx.compose.foundation.text2.input.insert
import androidx.compose.foundation.text2.input.placeCursorAtEnd
import androidx.compose.foundation.text2.input.textAsFlow
import androidx.compose.ui.focus.FocusRequester
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import little.goose.data.note.bean.Note
import little.goose.data.note.bean.NoteContentBlock
import little.goose.data.note.bean.NoteWithContent
import little.goose.data.note.domain.DeleteBlockUseCase
import little.goose.data.note.domain.DeleteNoteAndItsBlocksUseCase
import little.goose.data.note.domain.GetNoteWithContentFlowWithNoteIdUseCase
import little.goose.data.note.domain.InsertOrReplaceNoteContentBlockUseCase
import little.goose.data.note.domain.InsertOrReplaceNoteContentBlocksUseCase
import little.goose.data.note.domain.InsertOrReplaceNoteUseCase
import little.goose.note.event.NoteScreenEvent
import little.goose.note.ui.note.NoteBlockState
import little.goose.note.ui.note.NoteBottomBarState
import little.goose.note.ui.note.NoteContentState
import little.goose.note.util.FormatType
import little.goose.note.util.orderListNum
import little.goose.shared.common.getCurrentTimeMillis
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalFoundationApi::class, ExperimentalCoroutinesApi::class)
class NoteScreenModel(
    noteId: Long,
    private val insertOrReplaceNoteContentBlocks: InsertOrReplaceNoteContentBlocksUseCase,
    private val getNoteWithContentFlowWithNoteId: GetNoteWithContentFlowWithNoteIdUseCase,
    private val insertOrReplaceNoteContentBlock: InsertOrReplaceNoteContentBlockUseCase,
    private val insertOrReplaceNote: InsertOrReplaceNoteUseCase,
    private val deleteNoteAndItsBlocks: DeleteNoteAndItsBlocksUseCase,
    private val deleteNoteContentBlockUseCase: DeleteBlockUseCase
) : ScreenModel {

    private val noteIdFlow = MutableStateFlow(noteId)
    private val noteWithContent = MutableStateFlow<NoteWithContent?>(null)
    private val focusingBlockId = MutableStateFlow<Long?>(null)
    private val isPreviewStateFlow = MutableStateFlow(false)
    private val titleState = TextFieldState().apply {
        textAsFlow().onEach(::updateTitle).launchIn(screenModelScope)
    }
    private val contentBlockTextFieldState = mutableMapOf<Long, TextFieldState>()
    private val collectFocusJobMap = mutableMapOf<Long, Job>()
    private val collectUpdateJobMap = mutableMapOf<Long, Job>()
    private val focusRequesterMap = mutableMapOf<Long, FocusRequester>()
    private val mutableInteractionSourceMap = mutableMapOf<Long, MutableInteractionSource>()

    private val _event = MutableSharedFlow<NoteScreenEvent>()
    val event = _event.asSharedFlow()

    init {
        noteIdFlow.flatMapLatest { nId ->
            if (nId == -1L) {
                // If not pass id from outside, we need to create a empty Note for default.
                flowOf(NoteWithContent(Note(), emptyList()))
            } else {
                // If pass from outside or insert note to database.
                getNoteWithContentFlowWithNoteId(nId)
            }
        }.onEach { noteWithContent.value = it }.launchIn(screenModelScope)
    }

    val noteContentState = combine(
        noteWithContent.filterNotNull(),
        isPreviewStateFlow
    ) { nwc, isPreview ->
        if (isPreview) {
            NoteContentState.Preview(
                buildString {
                    if (nwc.note.title.isNotBlank()) {
                        append("# ${nwc.note.title}\n\n")
                    }
                    append(nwc.content.joinToString("\n\n") { it.content })
                }
            )
        } else {
            NoteContentState.Edit(
                titleState = titleState.apply {
                    if (!text.contentEquals(nwc.note.title)) {
                        edit {
                            replace(0, length, nwc.note.title)
                            placeCursorAtEnd()
                        }
                    }
                },
                contentStateList = nwc.content.map { block ->
                    val blockId = block.id!!
                    NoteBlockState(
                        id = blockId,
                        contentState = contentBlockTextFieldState.getOrPut(block.id!!) {
                            TextFieldState(block.content).also { tfs ->
                                createCollectUpdateJob(blockId, tfs)
                            }
                        },
                        interaction = mutableInteractionSourceMap.getOrPut(block.id!!) {
                            MutableInteractionSource().also { mis ->
                                createCollectFocusJob(blockId, mis)
                            }
                        },
                        focusRequester = focusRequesterMap.getOrPut(block.id!!, ::FocusRequester)
                    )
                },
                onBlockDelete = ::deleteNoteContentBlock,
                onBlockAdd = ::addBlockToBottom
            )
        }
    }.stateIn(
        screenModelScope,
        SharingStarted.WhileSubscribed(5000L),
        NoteContentState.Loading
    )

    val noteBottomBarState = combine(
        noteWithContent.filterNotNull(),
        isPreviewStateFlow
    ) { _, isPreview ->
        if (isPreview) {
            NoteBottomBarState.Preview(
                onChangeToEditMode = { isPreviewStateFlow.value = false },
            )
        } else {
            NoteBottomBarState.Editing(
                onChangeToPreviewMode = { isPreviewStateFlow.value = true },
                onFormat = ::format,
                onBlockAdd = ::addBlockToBottom
            )
        }
    }.stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000L), NoteBottomBarState.Loading)

    private fun deleteNoteContentBlock(blockId: Long) {
        screenModelScope.launch {
            val nwc = noteWithContent.value ?: return@launch
            val deletingBlock = nwc.content.find { it.id == blockId } ?: return@launch
            val newBlocks = buildList {
                val movingBlocks = mutableListOf<NoteContentBlock>()
                nwc.content.forEachIndexed { index, block ->
                    if (block.id == blockId) {
                        deleteNoteContentBlockUseCase(blockId)
                        collectFocusJobMap.remove(blockId)?.cancel()
                        collectUpdateJobMap.remove(blockId)?.cancel()
                        focusRequesterMap.remove(blockId)
                        mutableInteractionSourceMap.remove(blockId)
                        contentBlockTextFieldState.remove(blockId)
                    } else if (index < deletingBlock.sectionIndex) {
                        add(block)
                    } else {
                        block.copy(sectionIndex = index.toLong() - 1).also {
                            add(it)
                            movingBlocks.add(it)
                        }
                    }
                }
                insertOrReplaceNoteContentBlocks(movingBlocks)
            }
            noteWithContent.value = NoteWithContent(nwc.note, newBlocks)
        }
    }

    private suspend fun addContentBlock(block: NoteContentBlock): Long? {
        var nwc = noteWithContent.value ?: return null
        val insertingBlock = if (block.noteId == null) {
            // If this note doesn't exit, insert the note first.
            val noteId = insertOrReplaceNote(nwc.note)
            nwc = nwc.copy(nwc.note.copy(id = noteId))
            noteWithContent.value = nwc
            noteIdFlow.value = noteId
            block.copy(noteId = noteId)
        } else block

        // Insert the content block
        val noteContentBlockId = insertOrReplaceNoteContentBlock(insertingBlock)
        val insertedBlock = insertingBlock.copy(id = noteContentBlockId)

        val newBlocks = if (nwc.content.size.toLong() == insertedBlock.sectionIndex) {
            // Add block to end
            nwc.content + insertedBlock
        } else {
            buildList {
                val movingBlocks = mutableListOf<NoteContentBlock>()
                nwc.content.forEachIndexed { index, noteContentBlock ->
                    if (index < insertedBlock.sectionIndex) {
                        add(noteContentBlock)
                    } else {
                        if (index.toLong() == insertedBlock.sectionIndex) {
                            add(insertedBlock)
                        }
                        noteContentBlock.copy(sectionIndex = index + 1L).also {
                            add(it)
                            movingBlocks.add(it)
                        }
                    }
                }
                insertOrReplaceNoteContentBlocks(movingBlocks)
            }
        }
        noteWithContent.value = nwc.copy(content = newBlocks)
        _event.emit(NoteScreenEvent.AddNoteBlock(noteContentBlockId))
        return noteContentBlockId
    }

    private suspend fun updateTitle(title: CharSequence) {
        noteWithContent.value?.note?.copy(title = title.toString())?.let { note ->
            if (noteIdFlow.value != -1L) {
                insertOrReplaceNote(note)
            } else {
                val noteId = insertOrReplaceNote(note = note)
                noteIdFlow.value = noteId
            }
        }
    }

    private fun addBlockToBottom() {
        screenModelScope.launch {
            val index = noteWithContent.value?.content?.size ?: 0
            val noteId = noteWithContent.value?.note?.id
            addContentBlock(
                NoteContentBlock(
                    id = null,
                    noteId = noteId,
                    sectionIndex = index.toLong(),
                    content = ""
                )
            )
        }
    }

    private fun format(formatType: FormatType) {
        val realType = if (formatType is FormatType.List.Ordered) {
            // if formatting ordered list, we need to find if pre block is ordered list and get its number.
            val blocks = noteWithContent.value?.content ?: return
            val focusingId = focusingBlockId.value ?: return
            val focusingContentBlock = blocks.findLast { it.id == focusingId } ?: return
            if (focusingContentBlock.sectionIndex > 0L) {
                blocks.findLast {
                    it.sectionIndex == focusingContentBlock.sectionIndex - 1
                }?.content?.orderListNum?.let { preNum ->
                    FormatType.List.Ordered(preNum + 1)
                } ?: formatType
            } else formatType
        } else formatType

        focusingBlockId.value?.let(contentBlockTextFieldState::get)?.let { tfs ->
            tfs.edit {
                if (asCharSequence().startsWith(realType.value)) {
                    delete(0, realType.value.length)
                } else {
                    insert(0, realType.value)
                }
            }
        }
    }

    private suspend fun createCollectFocusJob(blockId: Long, interactionSource: InteractionSource) {
        collectFocusJobMap[blockId]?.cancel()
        collectFocusJobMap[blockId] = screenModelScope.launch {
            interactionSource.interactions.collect { interaction ->
                when (interaction) {
                    is FocusInteraction.Focus -> {
                        focusingBlockId.value = blockId
                    }

                    is FocusInteraction.Unfocus -> {
                        if (blockId == focusingBlockId.value) {
                            focusingBlockId.value = null
                        }
                    }
                }
            }
        }
    }

    @OptIn(FlowPreview::class)
    private suspend fun createCollectUpdateJob(blockId: Long, textFieldState: TextFieldState) {
        collectUpdateJobMap[blockId]?.cancel()
        collectUpdateJobMap[blockId] = screenModelScope.launch(NonCancellable) {
            textFieldState.textAsFlow().map { charSequence ->
                val nwc = noteWithContent.value ?: return@map charSequence
                val blockIndex = nwc.content.indexOfLast { it.id == blockId }.takeIf { it != -1 }
                    ?: return@map charSequence
                val block = nwc.content[blockIndex]
                val enterIndex = charSequence.lastIndexOf('\n')
                if (enterIndex >= 0) {
                    val block1Content = charSequence.subSequence(0, enterIndex).toString()
                    val block2Content =
                        charSequence.subSequence(enterIndex + 1, charSequence.length).toString()
                    textFieldState.edit {
                        replace(0, length, block1Content)
                    }
                    val block2 = NoteContentBlock(
                        id = null,
                        content = block2Content,
                        sectionIndex = block.sectionIndex + 1,
                        noteId = nwc.note.id
                    )
                    val block2Id = addContentBlock(block2)
                    noteWithContent.value = nwc.copy(
                        content = nwc.content.toMutableList().apply {
                            set(blockIndex, block.copy(content = block1Content))
                            add(blockIndex + 1, block2.copy(id = block2Id))
                        }
                    )
                    block1Content
                } else charSequence
            }.debounce(0.8.seconds).collect { textFieldCharSequence ->
                noteWithContent.value?.content?.find { it.id == blockId }?.let { block ->
                    insertOrReplaceNoteContentBlock(
                        block.copy(content = textFieldCharSequence.toString())
                    )
                }
            }
        }
    }

    override fun onDispose() {
        super.onDispose()
        // Update note last modified time
        val nwc = noteWithContent.value ?: return
        val note = nwc.note.takeIf { it.id != null } ?: return
        screenModelScope.launch(NonCancellable) {
            if (nwc.content.isEmpty()) {
                deleteNoteAndItsBlocks(note.id!!)
            } else {
                insertOrReplaceNote(note.copy(time = getCurrentTimeMillis()))
            }
        }
    }

}