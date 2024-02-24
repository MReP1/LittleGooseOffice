package little.goose.note

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text2.input.TextFieldState
import androidx.compose.foundation.text2.input.placeCursorAtEnd
import androidx.compose.ui.focus.FocusRequester
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
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
import little.goose.note.logic.BottomBlockAdder
import little.goose.note.logic.ContentBlockAdder
import little.goose.note.logic.InteractionSourceGetter
import little.goose.note.logic.NoteContentBlockDeleter
import little.goose.note.logic.TextFieldStateGetter
import little.goose.note.logic.TextFormatter
import little.goose.note.logic.TitleTextFieldState
import little.goose.note.ui.note.NoteBlockState
import little.goose.note.ui.note.NoteBottomBarState
import little.goose.note.ui.note.NoteContentState
import little.goose.note.ui.note.NoteScreenIntent
import little.goose.note.ui.note.NoteScreenMode
import little.goose.note.ui.note.NoteScreenState
import little.goose.note.util.FormatType
import little.goose.shared.common.getCurrentTimeMillis

class NoteScreenStateHolder(
    noteId: Long,
    private val coroutineScope: CoroutineScope,
    insertOrReplaceNoteContentBlocks: InsertOrReplaceNoteContentBlocksUseCase,
    private val getNoteWithContentFlowWithNoteId: GetNoteWithContentFlowWithNoteIdUseCase,
    insertOrReplaceNoteContentBlock: InsertOrReplaceNoteContentBlockUseCase,
    private val insertOrReplaceNote: InsertOrReplaceNoteUseCase,
    private val deleteNoteAndItsBlocks: DeleteNoteAndItsBlocksUseCase,
    deleteNoteContentBlockUseCase: DeleteBlockUseCase
) {
    private val noteIdFlow = MutableStateFlow(noteId)
    private val noteWithContent = MutableStateFlow<NoteWithContent?>(null)
    private val focusingBlockId = MutableStateFlow<Long?>(null)
    private val isPreviewStateFlow = MutableStateFlow(false)
    private val contentBlockTextFieldState = mutableMapOf<Long, TextFieldState>()
    private val collectFocusJobMap = mutableMapOf<Long, Job>()
    private val collectUpdateJobMap = mutableMapOf<Long, Job>()
    private val focusRequesterMap = mutableMapOf<Long, FocusRequester>()
    private val mutableInteractionSourceMap = mutableMapOf<Long, MutableInteractionSource>()
    private val _event = MutableSharedFlow<NoteScreenEvent>()
    val event = _event.asSharedFlow()

    private val titleState = TitleTextFieldState(
        coroutineScope, noteWithContent::value,
        noteIdFlow::value, { noteIdFlow.value = it },
        insertOrReplaceNote
    )

    private val deleteNoteContentBlock = NoteContentBlockDeleter(
        coroutineScope = coroutineScope,
        getNoteWithContent = noteWithContent::value,
        updateNoteWithContent = { noteWithContent.value = it },
        insertOrReplaceNoteContentBlocks = insertOrReplaceNoteContentBlocks,
        deleter = { blockId ->
            deleteNoteContentBlockUseCase(blockId)
            collectFocusJobMap.remove(blockId)?.cancel()
            collectUpdateJobMap.remove(blockId)?.cancel()
            focusRequesterMap.remove(blockId)
            mutableInteractionSourceMap.remove(blockId)
            contentBlockTextFieldState.remove(blockId)
        }
    )

    private val format: (FormatType) -> Unit = TextFormatter(
        getBlocks = { noteWithContent.value?.content },
        getFocusingId = focusingBlockId::value,
        getContentBlockTextFieldState = contentBlockTextFieldState::get
    )

    private val generateInteractionSource = InteractionSourceGetter(
        coroutineScope, mutableInteractionSourceMap, collectFocusJobMap,
        getFocusId = focusingBlockId::value,
        updateFocusingId = { focusingBlockId.value = it }
    )

    private val addContentBlock: suspend (block: NoteContentBlock) -> Long = ContentBlockAdder(
        getNoteWithContent = noteWithContent::value,
        updateNoteWithContent = { noteWithContent.value = it },
        insertOrReplaceNote = insertOrReplaceNote,
        updateNoteId = { noteIdFlow.value = it },
        focusRequesterMap,
        _event::emit,
        insertOrReplaceNoteContentBlock, insertOrReplaceNoteContentBlocks
    )

    private val getTextFieldState = TextFieldStateGetter(
        coroutineScope,
        getNoteWithContent = noteWithContent::value,
        updateNoteWithContent = { noteWithContent.value = it },
        addContentBlock = addContentBlock,
        contentBlockTextFieldState,
        collectUpdateJobMap,
        insertOrReplaceNoteContentBlock
    )

    private val addBlockToBottom = BottomBlockAdder(
        getBottomIndex = { noteWithContent.value?.content?.size ?: 0 },
        getNoteId = noteIdFlow::value,
        addContentBlock = addContentBlock
    )

    val noteScreenState = combine(
        noteWithContent.filterNotNull(),
        isPreviewStateFlow
    ) { nwc, isPreview ->
        NoteScreenState.Success(
            contentState = if (isPreview) {
                NoteContentState.Preview(
                    content = generatorMarkdownText(nwc.note.title, nwc.content)
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
                            contentState = getTextFieldState(blockId, block.content),
                            interaction = generateInteractionSource(blockId),
                            focusRequester = focusRequesterMap.getOrPut(blockId, ::FocusRequester)
                        )
                    }
                )
            },
            bottomBarState = if (isPreview) {
                NoteBottomBarState.Preview
            } else {
                NoteBottomBarState.Editing
            }
        )
    }.stateIn(
        coroutineScope,
        SharingStarted.WhileSubscribed(5000L),
        NoteScreenState.Loading
    )

    val action = fun(intent: NoteScreenIntent) {
        when (intent) {
            is NoteScreenIntent.Format -> {
                format(intent.formatType)
            }

            NoteScreenIntent.AddBlockToBottom -> coroutineScope.launch {
                addBlockToBottom()
            }

            is NoteScreenIntent.DeleteBlock -> coroutineScope.launch {
                deleteNoteContentBlock(intent.id)
            }

            is NoteScreenIntent.ChangeNoteScreenMode -> {
                isPreviewStateFlow.value = when (intent.mode) {
                    NoteScreenMode.Preview -> true
                    NoteScreenMode.Edit -> false
                }
            }
        }
    }

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

    private fun generatorMarkdownText(title: String, content: List<NoteContentBlock>): String {
        return buildString {
            if (title.isNotBlank()) {
                append("# ${title}\n\n")
            }
            append(content.joinToString("\n\n") { it.content })
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