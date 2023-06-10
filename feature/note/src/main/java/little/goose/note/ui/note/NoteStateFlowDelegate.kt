package little.goose.note.ui.note

import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import little.goose.note.data.entities.Note
import little.goose.note.data.entities.NoteContentBlock
import little.goose.note.logic.DeleteNoteContentBlockUseCase
import little.goose.note.logic.FormatType
import little.goose.note.logic.GetNoteFlowUseCase
import little.goose.note.logic.GetNoteWithContentMapFlowUseCase
import little.goose.note.logic.InsertNoteContentBlockUseCase
import little.goose.note.logic.InsertNoteUseCase
import little.goose.note.logic.UpdateNoteContentBlockUseCase
import little.goose.note.logic.UpdateNoteContentBlocksUseCase
import little.goose.note.logic.UpdateNoteUseCase
import little.goose.note.logic.content
import little.goose.note.logic.format
import little.goose.note.logic.note
import little.goose.note.logic.orderListNum
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class NoteRouteStateFlowDelegate(
    noteIdFlow: StateFlow<Long>,
    private val updateNoteId: (Long) -> Unit,
    private val coroutineScope: CoroutineScope,
    private val emitNoteScreenEvent: suspend (NoteScreenEvent) -> Unit,
    private val getNoteWithContentMapFlow: GetNoteWithContentMapFlowUseCase,
    private val insertNoteContentBlock: InsertNoteContentBlockUseCase,
    private val updateNoteContentBlock: UpdateNoteContentBlockUseCase,
    private val updateNoteContentBlocks: UpdateNoteContentBlocksUseCase,
    private val deleteNoteContentBlock: DeleteNoteContentBlockUseCase,
    private val insertNote: InsertNoteUseCase,
    private val updateNote: UpdateNoteUseCase,
    private val getNoteFlow: GetNoteFlowUseCase
) : ReadOnlyProperty<ViewModel, StateFlow<NoteScreenState>> {

    private val writingMutex = Mutex()

    private val _isPreview = MutableStateFlow(false)

    private val _focusingBlockId = MutableStateFlow<Long?>(null)

    private val noteWithContent = MutableStateFlow<Map<Note, List<NoteContentBlock>>?>(null)

    private var _textFieldValueCache = MutableStateFlow(mapOf<Long, TextFieldValue>())

    private val noteContentState: StateFlow<NoteContentState?> by NoteContentStateFlowDelegate(
        coroutineScope = coroutineScope,
        isPreview = _isPreview,
        focusingBlockId = _focusingBlockId,
        changeFocusingBlockId = { _focusingBlockId.value = it },
        noteWithContent = noteWithContent,
        textFieldValueCache = _textFieldValueCache,
        changeTextFileValueCache = { _textFieldValueCache.value = it },
        changeTitle = ::changeTitle,
        changeText = ::changeText,
        deleteContentBlock = ::deleteContentBlock,
        addContentBlock = { block ->
            coroutineScope.launchWithWritingMutex {
                addContentBlock(block)
            }
        }
    )

    private val noteBottomBarState: StateFlow<NoteBottomBarState> by NoteBottomStateFlowDelegate(
        coroutineScope = coroutineScope,
        isPreview = _isPreview,
        changeIsPreview = { _isPreview.value = it },
        format = ::format
    )

    private val noteScreenState = combine(
        noteContentState.filterNotNull(), noteBottomBarState
    ) { noteContentState, noteBottomBarState ->
        NoteScreenState.State(
            NoteScaffoldState(
                contentState = noteContentState,
                bottomBarState = noteBottomBarState
            )
        )
    }.stateIn(
        scope = coroutineScope,
        SharingStarted.WhileSubscribed(5000L),
        NoteScreenState.Loading
    )

    init {
        noteIdFlow.flatMapLatest { noteId ->
            if (noteId == -1L) {
                flowOf(mapOf(Pair(Note(), emptyList())))
            } else {
                getNoteWithContentMapFlow(noteId)
            }
        }.flatMapLatest { nwc ->
            if (nwc.isEmpty()) {
                combine(
                    getNoteFlow(noteIdFlow.value),
                    flowOf(emptyList<NoteContentBlock>())
                ) { note, contentBlocks ->
                    mapOf(Pair(note, contentBlocks))
                }
            } else {
                flow<Map<Note, List<NoteContentBlock>>> {
                    emit(nwc)
                }
            }
        }.onEach {
            noteWithContent.value = it
        }.launchIn(coroutineScope)
    }

    override fun getValue(thisRef: ViewModel, property: KProperty<*>): StateFlow<NoteScreenState> {
        return noteScreenState
    }

    private fun changeTitle(title: String) {
        if (title.contains('\n')) return
        coroutineScope.launchWithWritingMutex {
            val nwc = noteWithContent.value ?: return@launchWithWritingMutex
            val note = nwc.note.copy(title = title)
            noteWithContent.value = buildMap {
                put(note, nwc.content)
            }
            if (note.id != null) {
                updateNote(note)
            }
        }
    }

    private fun changeText(index: Int, id: Long, textFieldValue: TextFieldValue) {
        coroutineScope.launchWithWritingMutex {
            val nwcMap = noteWithContent.value ?: return@launchWithWritingMutex
            if (textFieldValue.text.lastIndexOf('\n') >= 0) {
                // when textFieldValue Contains \n, split it to two blocks
                val splitText = textFieldValue.text.split('\n')
                val block1Content = splitText[0]
                val block2Content = splitText[1]
                val block1 = nwcMap.content[index].copy(content = block1Content)
                val block2 = NoteContentBlock(
                    id = null,
                    content = block2Content,
                    index = index + 1,
                    noteId = nwcMap.note.id
                )
                val block2Id = addContentBlock(block2)
                _textFieldValueCache.value = _textFieldValueCache.value.toMutableMap().apply {
                    put(id, textFieldValue.copy(text = block1Content))
                    if (block2Id != null) {
                        put(block2Id, TextFieldValue(block2.content))
                    }
                }
                changeContentBlock(block1)
            } else runCatching {
                _textFieldValueCache.value = _textFieldValueCache.value.toMutableMap()
                    .apply { put(id, textFieldValue) }
                val newBlock = nwcMap.content[index].copy(content = textFieldValue.text)
                changeContentBlock(newBlock)
            }
        }
    }

    private fun format(
        type: FormatType
    ) {
        coroutineScope.launchWithWritingMutex {
            val nwc = noteWithContent.value ?: return@launchWithWritingMutex
            val focusingContentBlock = _focusingBlockId.value?.let { focusingBlockId ->
                nwc.content.findLast { it.id == focusingBlockId }
            } ?: return@launchWithWritingMutex
            focusingContentBlock.id ?: return@launchWithWritingMutex

            val realType =
                if (type is FormatType.List.Ordered && focusingContentBlock.index > 0) {
                    val previewNum =
                        nwc.content[focusingContentBlock.index - 1].content.orderListNum
                    FormatType.List.Ordered(previewNum + 1)
                } else type

            val content = _textFieldValueCache.value[focusingContentBlock.id]
                ?.format(realType)
                ?.also {
                    _textFieldValueCache.value = _textFieldValueCache.value.toMutableMap()
                        .apply { put(focusingContentBlock.id, it) }
                } ?: return@launchWithWritingMutex
            val newBlock = focusingContentBlock.copy(content = content.text)

            changeContentBlock(newBlock)
        }

    }

    private fun deleteContentBlock(block: NoteContentBlock) {
        coroutineScope.launchWithWritingMutex {
            val nwc = noteWithContent.value ?: return@launchWithWritingMutex
            val newBlocks = withContext(Dispatchers.Default) {
                buildList {
                    val movingBlocks = mutableListOf<NoteContentBlock>()
                    nwc.content.forEachIndexed { index, noteContentBlock ->
                        if (noteContentBlock.id == block.id) {
                            deleteNoteContentBlock(noteContentBlock)
                        } else if (index < block.index) {
                            add(noteContentBlock)
                        } else {
                            noteContentBlock.copy(index = index - 1).also {
                                add(it)
                                movingBlocks.add(it)
                            }
                        }
                    }
                    updateNoteContentBlocks(movingBlocks)
                }
            }
            noteWithContent.value = buildMap { put(nwc.note, newBlocks) }
        }
    }

    private suspend fun addContentBlock(block: NoteContentBlock): Long? {
        var nwc = noteWithContent.value ?: return null
        val insertBlock = if (block.noteId == null) {
            // If this note doesn't exit, insert the note first.
            val noteId = insertNote(nwc.note)
            nwc = buildMap { put(nwc.note.copy(id = noteId), nwc.content) }
            noteWithContent.value = nwc
            updateNoteId(noteId)
            block.copy(noteId = noteId)
        } else block

        // Insert the content block
        val noteContentBlockId = insertNoteContentBlock(insertBlock)
        val newBlock = insertBlock.copy(id = noteContentBlockId)

        val newBlocks = if (nwc.content.size == newBlock.index) {
            // Add to the end
            nwc.content + newBlock
        } else withContext(Dispatchers.Default) {
            buildList {
                val movingBlocks = mutableListOf<NoteContentBlock>()
                nwc.content.forEachIndexed { index, noteContentBlock ->
                    if (index < newBlock.index) {
                        add(noteContentBlock)
                    } else {
                        if (index == newBlock.index) {
                            add(newBlock)
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
        emitNoteScreenEvent(NoteScreenEvent.AddNoteBlock(newBlock))
        return noteContentBlockId
    }

    private suspend fun changeContentBlock(block: NoteContentBlock) {
        if (block.id == null) {
            addContentBlock(block)
        } else {
            val nwcMap = noteWithContent.value ?: return
            val content = nwcMap.content.toMutableList()
            val newNwc = buildMap {
                put(nwcMap.note, content.apply { set(block.index, block) })
            }
            noteWithContent.value = newNwc
            updateNoteContentBlock(block)
        }
    }

    private inline fun CoroutineScope.launchWithWritingMutex(
        crossinline block: suspend CoroutineScope.() -> Unit
    ) {
        launch { writingMutex.withLock { block() } }
    }

}

class NoteContentStateFlowDelegate(
    noteWithContent: StateFlow<Map<Note, List<NoteContentBlock>>?>,
    isPreview: StateFlow<Boolean>,
    private val focusingBlockId: StateFlow<Long?>,
    private val textFieldValueCache: StateFlow<Map<Long, TextFieldValue>>,
    private val coroutineScope: CoroutineScope,
    private val changeTextFileValueCache: (Map<Long, TextFieldValue>) -> Unit,
    private val changeFocusingBlockId: (Long?) -> Unit,
    private val changeTitle: (String) -> Unit,
    private val changeText: (Int, Long, TextFieldValue) -> Unit,
    private val deleteContentBlock: (NoteContentBlock) -> Unit,
    private val addContentBlock: (NoteContentBlock) -> Unit
) : ReadOnlyProperty<Any?, StateFlow<NoteContentState?>> {

    private var _interactionCache = mapOf<Long, MutableInteractionSource>()
    private var _focusRequesterCache = mapOf<Long, FocusRequester>()

    private var lastContentSize = 0
    private val noteContentStateFlow: StateFlow<NoteContentState?> = combine(
        noteWithContent
            .filterNotNull()
            .filter { it.isNotEmpty() },
        focusingBlockId,
        isPreview,
        textFieldValueCache
    ) { nwc, focusingBlockId, isPreview, textFieldValues ->
        val note = nwc.note
        val blockContents = nwc.content
        // 如果新增替换操作需求，需要注意修改这里
        if (blockContents.size != lastContentSize) {
            val interactionSources = mutableMapOf<Long, MutableInteractionSource>()
            val focusRequesters = mutableMapOf<Long, FocusRequester>()
            val newTextFieldValues = textFieldValues.toMutableMap()
            blockContents.forEach { block ->
                block.id!!
                val interactionSource = _interactionCache[block.id] ?: MutableInteractionSource()
                    .also { ins ->
                        ins.interactions.onEach {
                            if (it is FocusInteraction.Focus) {
                                changeFocusingBlockId(block.id)
                            } else if (it is FocusInteraction.Unfocus) {
                                if (this@NoteContentStateFlowDelegate.focusingBlockId.value == block.id) {
                                    changeFocusingBlockId(null)
                                }
                            }
                        }.launchIn(coroutineScope)
                    }
                val focusRequester = _focusRequesterCache[block.id] ?: FocusRequester()
                val textFieldValue = textFieldValues[block.id] ?: TextFieldValue(block.content)
                interactionSources[block.id] = interactionSource
                focusRequesters[block.id] = focusRequester
                newTextFieldValues[block.id] = textFieldValue
            }
            _interactionCache = interactionSources
            _focusRequesterCache = focusRequesters
            changeTextFileValueCache(newTextFieldValues)
        }
        lastContentSize = blockContents.size

        NoteContentState(
            note = note,
            content = blockContents,
            focusingBlockId = focusingBlockId,
            isPreview = isPreview,
            onTitleChange = changeTitle,
            onBlockChange = changeText,
            onBlockAdd = addContentBlock,
            onBlockDelete = deleteContentBlock,
            interactions = _interactionCache,
            focusRequesters = _focusRequesterCache,
            textFieldValues = textFieldValueCache.value
        )
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = null
    )

    override fun getValue(
        thisRef: Any?,
        property: KProperty<*>
    ): StateFlow<NoteContentState?> {
        return noteContentStateFlow
    }

}

class NoteBottomStateFlowDelegate(
    coroutineScope: CoroutineScope,
    isPreview: StateFlow<Boolean>,
    changeIsPreview: (Boolean) -> Unit,
    format: (FormatType) -> Unit
) : ReadOnlyProperty<Any?, StateFlow<NoteBottomBarState>> {

    private val noteBottomState = isPreview.map { isPreview ->
        NoteBottomBarState(
            isPreview = isPreview,
            onPreviewChange = changeIsPreview,
            onFormat = format
        )
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = NoteBottomBarState(isPreview.value, changeIsPreview, format)
    )

    override fun getValue(
        thisRef: Any?,
        property: KProperty<*>
    ): StateFlow<NoteBottomBarState> {
        return noteBottomState
    }

}