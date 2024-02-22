package little.goose.note

import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text2.input.TextFieldState
import androidx.compose.foundation.text2.input.placeCursorAtEnd
import androidx.compose.foundation.text2.input.textAsFlow
import androidx.compose.ui.focus.FocusRequester
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.IO
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
import little.goose.note.logic.Formatter
import little.goose.note.ui.note.NoteBlockState
import little.goose.note.ui.note.NoteBottomBarState
import little.goose.note.ui.note.NoteContentState
import little.goose.note.ui.note.NoteScreenIntent
import little.goose.note.ui.note.NoteScreenMode
import little.goose.note.ui.note.NoteScreenState
import little.goose.note.util.FormatType
import little.goose.shared.common.getCurrentTimeMillis
import kotlin.time.Duration.Companion.seconds

class NoteScreenStateHolder(
    noteId: Long,
    private val coroutineScope: CoroutineScope,
    private val insertOrReplaceNoteContentBlocks: InsertOrReplaceNoteContentBlocksUseCase,
    private val getNoteWithContentFlowWithNoteId: GetNoteWithContentFlowWithNoteIdUseCase,
    private val insertOrReplaceNoteContentBlock: InsertOrReplaceNoteContentBlockUseCase,
    private val insertOrReplaceNote: InsertOrReplaceNoteUseCase,
    private val deleteNoteAndItsBlocks: DeleteNoteAndItsBlocksUseCase,
    private val deleteNoteContentBlockUseCase: DeleteBlockUseCase
) {
    private val noteIdFlow = MutableStateFlow(noteId)
    private val noteWithContent = MutableStateFlow<NoteWithContent?>(null)
    private val focusingBlockId = MutableStateFlow<Long?>(null)
    private val isPreviewStateFlow = MutableStateFlow(false)
    private val titleState = TextFieldState().apply {
        textAsFlow().onEach(::updateTitle).launchIn(coroutineScope)
    }
    private val contentBlockTextFieldState = mutableMapOf<Long, TextFieldState>()
    private val collectFocusJobMap = mutableMapOf<Long, Job>()
    private val collectUpdateJobMap = mutableMapOf<Long, Job>()
    private val focusRequesterMap = mutableMapOf<Long, FocusRequester>()
    private val mutableInteractionSourceMap = mutableMapOf<Long, MutableInteractionSource>()

    private val _event = MutableSharedFlow<NoteScreenEvent>()
    val event = _event.asSharedFlow()

    val format: (FormatType) -> Unit = Formatter(
        getBlocks = { noteWithContent.value?.content },
        getFocusingId = focusingBlockId::value,
        getContentBlockTextFieldState = contentBlockTextFieldState::get
    )

    init {
        noteIdFlow.flatMapLatest { nId ->
            if (nId == -1L) {
                // If not pass id from outside, we need to create a empty Note for default.
                flowOf(NoteWithContent(Note(), emptyList()))
            } else {
                // If pass from outside or insert note to database.
                getNoteWithContentFlowWithNoteId(nId)
            }
        }.onEach { noteWithContent.value = it }.launchIn(coroutineScope)
    }

    private val noteContentState = combine(
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
                }
            )
        }
    }.stateIn(
        coroutineScope,
        SharingStarted.WhileSubscribed(5000L),
        NoteContentState.Loading
    )

    private val noteBottomBarState = combine(
        noteWithContent.filterNotNull(),
        isPreviewStateFlow
    ) { _, isPreview ->
        if (isPreview) {
            NoteBottomBarState.Preview
        } else {
            NoteBottomBarState.Editing
        }
    }.stateIn(coroutineScope, SharingStarted.WhileSubscribed(5000L), NoteBottomBarState.Loading)

    val noteScreenState = combine(
        noteContentState, noteBottomBarState
    ) { noteContentState, noteBottomBarState ->
        NoteScreenState(noteContentState, noteBottomBarState)
    }.stateIn(
        coroutineScope,
        SharingStarted.WhileSubscribed(5000L),
        NoteScreenState(noteContentState.value, noteBottomBarState.value)
    )

    val action = fun(intent: NoteScreenIntent) {
        when (intent) {
            is NoteScreenIntent.Format -> format(intent.formatType)
            NoteScreenIntent.AddBlockToBottom -> addBlockToBottom()
            is NoteScreenIntent.DeleteBlock -> deleteNoteContentBlock(intent.id)
            is NoteScreenIntent.ChangeNoteScreenMode -> {
                isPreviewStateFlow.value = when (intent.mode) {
                    NoteScreenMode.Preview -> true
                    NoteScreenMode.Edit -> false
                }
            }
        }
    }

    private fun deleteNoteContentBlock(blockId: Long) {
        coroutineScope.launch {
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
        val newNwc = nwc.copy(content = newBlocks)
        noteWithContent.value = newNwc
        val index = newNwc.content.indexOfLast { it.id == noteContentBlockId }
        val focusRequester = focusRequesterMap.getOrPut(noteContentBlockId, ::FocusRequester)
        _event.emit(NoteScreenEvent.AddNoteBlock(index, focusRequester))
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
        coroutineScope.launch {
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

    private suspend fun createCollectFocusJob(blockId: Long, interactionSource: InteractionSource) {
        collectFocusJobMap[blockId]?.cancel()
        collectFocusJobMap[blockId] = coroutineScope.launch {
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
        collectUpdateJobMap[blockId] = coroutineScope.launch(NonCancellable) {
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

    fun clear() {
        // Update note last modified time
        val nwc = noteWithContent.value ?: return
        val note = nwc.note.takeIf { it.id != null } ?: return
        coroutineScope.launch(NonCancellable + Dispatchers.IO) {
            if (nwc.content.isEmpty()) {
                deleteNoteAndItsBlocks(note.id!!)
            } else {
                insertOrReplaceNote(note.copy(time = getCurrentTimeMillis()))
            }
        }
    }

}