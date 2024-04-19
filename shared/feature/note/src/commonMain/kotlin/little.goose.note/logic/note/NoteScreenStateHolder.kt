package little.goose.note.logic.note

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text2.input.TextFieldState
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
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
    private val noteScreenMode = MutableStateFlow(NoteScreenMode.Edit)
    private val cacheHolder = NoteScreenCacheHolder()
    private val dataBaseMutex = Mutex()

    private var focusingBlockId: Long? = null

    private val _event = MutableSharedFlow<NoteScreenEvent>()
    val event = _event.asSharedFlow()

    private val deleteNoteContentBlock: suspend (
        id: Long
    ) -> Unit = NoteContentBlockDeleter(
        mutex = dataBaseMutex,
        getNoteWithContent = noteWithContent::value,
        updateNoteWithContent = noteWithContent::value::set,
        insertOrReplaceNoteContentBlocks = insertOrReplaceNoteContentBlocks,
        deleteNoteContentBlockUseCase = deleteNoteContentBlockUseCase,
        cacheHolder = cacheHolder
    )

    private val format: (
        type: FormatType
    ) -> Unit = TextFormatter(
        getBlocks = { noteWithContent.value?.content },
        getFocusingId = ::focusingBlockId,
        getContentBlockTextFieldState = cacheHolder.contentBlockTextFieldStateMap::get
    )

    private val addContentBlock: suspend (
        block: NoteContentBlock
    ) -> Long = ContentBlockAdder(
        mutex = dataBaseMutex,
        getNoteWithContent = noteWithContent::value,
        updateNoteWithContent = noteWithContent::value::set,
        insertOrReplaceNote = insertOrReplaceNote,
        updateNoteId = noteIdFlow::value::set,
        focusRequesterMap = cacheHolder.focusRequesterMap,
        emitEvent = _event::emit,
        insertOrReplaceNoteContentBlock = insertOrReplaceNoteContentBlock,
        insertOrReplaceNoteContentBlocks = insertOrReplaceNoteContentBlocks
    )

    private val addBlockToBottom: suspend () -> Unit = BottomBlockAdder(
        getBottomIndex = { noteWithContent.value?.content?.size ?: 0 },
        getNoteId = noteIdFlow::value,
        addContentBlock = addContentBlock
    )

    private val mapContentState: (
        noteWithContent: NoteWithContent,
        mode: NoteScreenMode
    ) -> NoteContentState = ContentStateMapper(
        coroutineScope = coroutineScope,
        getNoteWithContent = noteWithContent::value,
        updateNoteWithContent = noteWithContent::value::set,
        getNoteId = noteIdFlow::value,
        updateNoteId = noteIdFlow::value::set,
        getFocusingId = ::focusingBlockId,
        updateFocusingId = ::focusingBlockId::set,
        addContentBlock = addContentBlock,
        insertOrReplaceNote = insertOrReplaceNote,
        cacheHolder = cacheHolder,
        insertOrReplaceNoteContentBlock = insertOrReplaceNoteContentBlock,
    )

    val noteScreenState = combine(
        noteWithContent.filterNotNull(), noteScreenMode
    ) { nwc, noteScreenMode ->
        NoteScreenState.Success(
            contentState = mapContentState(nwc, noteScreenMode),
            bottomBarState = when (noteScreenMode) {
                NoteScreenMode.Preview -> NoteBottomBarState.Preview
                NoteScreenMode.Edit -> NoteBottomBarState.Editing
            }
        )
    }.stateIn(coroutineScope, SharingStarted.WhileSubscribed(5000L), NoteScreenState.Loading)

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
                noteScreenMode.value = intent.mode
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

internal class NoteScreenCacheHolder {
    val contentBlockTextFieldStateMap = mutableMapOf<Long, TextFieldState>()
    val collectFocusJobMap = mutableMapOf<Long, Job>()
    val collectUpdateJobMap = mutableMapOf<Long, Job>()
    val focusRequesterMap = mutableMapOf<Long, FocusRequester>()
    val mutableInteractionSourceMap = mutableMapOf<Long, MutableInteractionSource>()

    fun clearCache(blockId: Long) {
        collectFocusJobMap.remove(blockId)?.cancel()
        collectUpdateJobMap.remove(blockId)?.cancel()
        focusRequesterMap.remove(blockId)
        mutableInteractionSourceMap.remove(blockId)
        contentBlockTextFieldStateMap.remove(blockId)
    }
}