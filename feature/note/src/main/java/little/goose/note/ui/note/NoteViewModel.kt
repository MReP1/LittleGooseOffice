package little.goose.note.ui.note

import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text2.input.TextFieldState
import androidx.compose.foundation.text2.input.delete
import androidx.compose.foundation.text2.input.insert
import androidx.compose.foundation.text2.input.placeCursorAtEnd
import androidx.compose.foundation.text2.input.textAsFlow
import androidx.compose.ui.focus.FocusRequester
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import little.goose.note.data.constants.KEY_NOTE_ID
import little.goose.note.data.entities.Note
import little.goose.note.data.entities.NoteContentBlock
import little.goose.note.logic.DeleteNoteContentBlockUseCase
import little.goose.note.logic.DeleteNotesAndItsBlocksUseCase
import little.goose.note.logic.FormatType
import little.goose.note.logic.GetNoteFlowUseCase
import little.goose.note.logic.GetNoteWithContentMapFlowUseCase
import little.goose.note.logic.InsertNoteContentBlockUseCase
import little.goose.note.logic.InsertNoteUseCase
import little.goose.note.logic.UpdateNoteContentBlockUseCase
import little.goose.note.logic.UpdateNoteContentBlocksUseCase
import little.goose.note.logic.UpdateNoteUseCase
import little.goose.note.logic.content
import little.goose.note.logic.note
import little.goose.note.logic.orderListNum
import java.util.Date
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getNoteWithContentMapFlow: GetNoteWithContentMapFlowUseCase,
    private val getNoteFlow: GetNoteFlowUseCase,
    private val deleteNoteContentBlock: DeleteNoteContentBlockUseCase,
    private val updateNoteContentBlock: UpdateNoteContentBlockUseCase,
    private val updateNoteContentBlocks: UpdateNoteContentBlocksUseCase,
    private val insertNoteContentBlock: InsertNoteContentBlockUseCase,
    private val deleteNotesAndItsBlocks: DeleteNotesAndItsBlocksUseCase,
    private val insertNote: InsertNoteUseCase,
    private val updateNote: UpdateNoteUseCase
) : ViewModel() {

    private val noteIdFlow = savedStateHandle.getStateFlow<Long>(KEY_NOTE_ID, -1)
    private val noteWithContent = MutableStateFlow<Map<Note, List<NoteContentBlock>>?>(null)
    private val focusingBlockId = MutableStateFlow<Long?>(null)
    private val isPreviewStateFlow = MutableStateFlow(false)
    private val titleState = TextFieldState().apply {
        textAsFlow().onEach(::updateTitle).launchIn(viewModelScope)
    }
    private val contentBlockTextFieldState = mutableMapOf<Long, TextFieldState>()
    private val collectFocusJobMap = mutableMapOf<Long, Job>()
    private val collectUpdateJobMap = mutableMapOf<Long, Job>()
    private val focusRequesterMap = mutableMapOf<Long, FocusRequester>()
    private val mutableInteractionSourceMap = mutableMapOf<Long, MutableInteractionSource>()

    private val _event = MutableSharedFlow<NoteScreenEvent>()
    val event = _event.asSharedFlow()

    init {
        noteIdFlow.flatMapLatest { noteId ->
            if (noteId == -1L) {
                // If not pass id from outside, we need to create a empty Note for default.
                flowOf(mapOf(Note() to emptyList()))
            } else {
                // If pass from outside or insert note to database.
                getNoteWithContentMapFlow(noteId)
            }
        }.flatMapLatest { nwc ->
            if (nwc.isEmpty()) {
                combine(
                    getNoteFlow(noteIdFlow.value),
                    flowOf(emptyList<NoteContentBlock>())
                ) { note, contentBlocks ->
                    mapOf(note to contentBlocks)
                }
            } else {
                flow<Map<Note, List<NoteContentBlock>>> { emit(nwc) }
            }
        }.onEach { noteWithContent.value = it }.launchIn(viewModelScope)
    }

    val noteContentState = combine(
        noteWithContent.filterNotNull().filterNot(Map<*, *>::isEmpty),
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
                    block.id!!
                    NoteBlockState(
                        id = block.id,
                        contentState = contentBlockTextFieldState.getOrPut(block.id) {
                            TextFieldState(block.content).also { tfs ->
                                createCollectUpdateJob(block.id, tfs)
                            }
                        },
                        interaction = mutableInteractionSourceMap.getOrPut(block.id) {
                            MutableInteractionSource().also { mis ->
                                createCollectFocusJob(block.id, mis)
                            }
                        },
                        focusRequester = focusRequesterMap.getOrPut(block.id, ::FocusRequester)
                    )
                },
                onBlockDelete = ::deleteNoteContentBlock,
                onBlockAdd = ::addBlockToBottom
            )
        }
    }.stateIn(viewModelScope, WhileSubscribed(5000L), NoteContentState.Loading)

    val noteBottomBarState = combine(
        noteWithContent.filterNotNull().filterNot(Map<*, *>::isEmpty),
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
    }.stateIn(viewModelScope, WhileSubscribed(5000L), NoteBottomBarState.Loading)

    private fun deleteNoteContentBlock(blockId: Long) {
        viewModelScope.launch {
            val nwc = noteWithContent.value ?: return@launch
            val deletingBlock = nwc.content.find { it.id == blockId } ?: return@launch
            val newBlocks = buildList {
                val movingBlocks = mutableListOf<NoteContentBlock>()
                nwc.content.forEachIndexed { index, block ->
                    if (block.id == blockId) {
                        deleteNoteContentBlock(deletingBlock)
                        collectFocusJobMap.remove(blockId)?.cancel()
                        collectUpdateJobMap.remove(blockId)?.cancel()
                        focusRequesterMap.remove(blockId)
                        mutableInteractionSourceMap.remove(blockId)
                        contentBlockTextFieldState.remove(blockId)
                    } else if (index < deletingBlock.index) {
                        add(block)
                    } else {
                        block.copy(index = index - 1).also {
                            add(it)
                            movingBlocks.add(it)
                        }
                    }
                }
                updateNoteContentBlocks(movingBlocks)
            }
            noteWithContent.value = buildMap { put(nwc.note, newBlocks) }
        }
    }

    private suspend fun addContentBlock(block: NoteContentBlock): Long? {
        var nwc = noteWithContent.value ?: return null
        val insertingBlock = if (block.noteId == null) {
            // If this note doesn't exit, insert the note first.
            val noteId = insertNote(nwc.note)
            nwc = buildMap { put(nwc.note.copy(id = noteId), nwc.content) }
            noteWithContent.value = nwc
            savedStateHandle[KEY_NOTE_ID] = noteId
            block.copy(noteId = noteId)
        } else block

        // Insert the content block
        val noteContentBlockId = insertNoteContentBlock(insertingBlock)
        val insertedBlock = insertingBlock.copy(id = noteContentBlockId)

        val newBlocks = if (nwc.content.size == insertedBlock.index) {
            // Add block to end
            nwc.content + insertedBlock
        } else {
            buildList {
                val movingBlocks = mutableListOf<NoteContentBlock>()
                nwc.content.forEachIndexed { index, noteContentBlock ->
                    if (index < insertedBlock.index) {
                        add(noteContentBlock)
                    } else {
                        if (index == insertedBlock.index) {
                            add(insertedBlock)
                        }
                        noteContentBlock.copy(index = index + 1).also {
                            add(it)
                            movingBlocks.add(it)
                        }
                    }
                }
                updateNoteContentBlocks(movingBlocks)
            }
        }
        noteWithContent.value = buildMap { put(nwc.note, newBlocks) }
        _event.emit(NoteScreenEvent.AddNoteBlock(insertedBlock))
        return noteContentBlockId
    }

    private suspend fun updateTitle(title: CharSequence) {
        noteWithContent.value?.note?.copy(title = title.toString())?.let { note ->
            if (noteIdFlow.value != -1L) {
                updateNote(note = note)
            } else {
                val noteId = insertNote(note = note)
                savedStateHandle[KEY_NOTE_ID] = noteId
            }
        }
    }

    private fun addBlockToBottom() {
        viewModelScope.launch {
            val index = noteWithContent.value?.content?.size ?: 0
            val noteId = noteWithContent.value?.note?.id
            addContentBlock(
                NoteContentBlock(id = null, noteId = noteId, index = index, content = "")
            )
        }
    }

    private fun format(formatType: FormatType) {
        val realType = if (formatType is FormatType.List.Ordered) {
            // if formatting ordered list, we need to find if pre block is ordered list and get its number.
            val blocks = noteWithContent.value?.content ?: return
            val focusingId = focusingBlockId.value ?: return
            val focusingContentBlock = blocks.findLast { it.id == focusingId } ?: return
            if (focusingContentBlock.index > 0) {
                blocks.findLast {
                    it.index == focusingContentBlock.index - 1
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
        collectFocusJobMap[blockId] = viewModelScope.launch {
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
        collectUpdateJobMap[blockId] = viewModelScope.launch {
            textFieldState.textAsFlow().map { charSequence ->
                val nwcMap = noteWithContent.value ?: return@map charSequence
                val blockIndex = nwcMap.content.indexOfLast { it.id == blockId }.takeIf { it != -1 }
                    ?: return@map charSequence
                val block = nwcMap.content[blockIndex]
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
                        index = block.index + 1,
                        noteId = nwcMap.note.id
                    )
                    val block2Id = addContentBlock(block2)
                    noteWithContent.value = nwcMap.toMutableMap().apply {
                        put(note, content.toMutableList().apply {
                            set(blockIndex, block.copy(content = block1Content))
                            add(blockIndex + 1, block2.copy(id = block2Id))
                        })
                    }
                    block1Content
                } else charSequence
            }.debounce(0.8.seconds).collect { textFieldCharSequence ->
                noteWithContent.value?.content?.find { it.id == blockId }?.let { block ->
                    updateNoteContentBlock(block.copy(content = textFieldCharSequence.toString()))
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Update note last modified time
        val nwc = noteWithContent.value ?: return
        val note = nwc.note.takeIf { it.id != null } ?: return
        viewModelScope.launch(NonCancellable) {
            if (nwc.content.isEmpty()) {
                deleteNotesAndItsBlocks(listOf(note))
            } else {
                updateNote(note.copy(time = Date()))
            }
        }
    }
}