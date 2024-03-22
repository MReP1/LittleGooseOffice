package little.goose.note.logic.note

import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text2.input.TextFieldState
import androidx.compose.foundation.text2.input.delete
import androidx.compose.foundation.text2.input.insert
import androidx.compose.foundation.text2.input.rememberTextFieldState
import androidx.compose.foundation.text2.input.textAsFlow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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
import little.goose.note.ui.note.NoteScreenIntent
import little.goose.note.ui.note.NoteScreenMode
import little.goose.note.ui.note.NoteScreenState
import little.goose.note.util.FormatType
import little.goose.note.util.orderListNum
import little.goose.shared.common.getCurrentTimeMillis
import little.goose.shared.ui.architecture.MviHolder
import org.koin.compose.koinInject
import kotlin.time.Duration.Companion.seconds

@Composable
fun rememberNoteScreenStateHolder(
    initNoteId: Long,
    initTitle: String,
    insertOrReplaceNoteContentBlocks: InsertOrReplaceNoteContentBlocksUseCase = koinInject(),
    getNoteWithContentFlowWithNoteId: GetNoteWithContentFlowWithNoteIdUseCase = koinInject(),
    insertOrReplaceNoteContentBlock: InsertOrReplaceNoteContentBlockUseCase = koinInject(),
    insertOrReplaceNote: InsertOrReplaceNoteUseCase = koinInject(),
    deleteNoteAndItsBlocks: DeleteNoteAndItsBlocksUseCase = koinInject(),
    deleteNoteContentBlockUseCase: DeleteBlockUseCase = koinInject()
): MviHolder<NoteScreenState, NoteScreenEvent, NoteScreenIntent> {
    require(initNoteId != -1L)

    val coroutineScope = rememberCoroutineScope()

    val noteId by rememberSaveable {
        mutableStateOf(initNoteId)
    }

    val databaseMutex = remember {
        Mutex()
    }

    val cacheHolder = remember {
        NoteScreenCacheHolder()
    }

    var noteScreenMode by rememberSaveable {
        mutableStateOf(NoteScreenMode.Edit)
    }

    var focusId by rememberSaveable {
        mutableStateOf<Long?>(null)
    }

    var noteWithContent by remember {
        mutableStateOf<NoteWithContent?>(null)
    }

    val titleState = rememberTextFieldState(initTitle)

    val event = remember {
        MutableSharedFlow<NoteScreenEvent>()
    }

    LaunchedEffect(noteId) {
        getNoteWithContentFlowWithNoteId(noteId).collect {
            noteWithContent = it
        }
    }

    LaunchedEffect(titleState) {
        titleState.textAsFlow().collect { text ->
            noteWithContent?.note?.copy(title = text.toString())?.let { note ->
                insertOrReplaceNote(note)
            }
        }
    }

    val generateMarkdownText: (
        title: String, blocks: List<NoteContentBlock>
    ) -> String = remember { MarkdownTextGenerator() }

    val addContentBlock: suspend (block: NoteContentBlock) -> Long = { block ->
        databaseMutex.withLock {
            noteWithContent?.let { nwc ->
                // Insert the content block
                val noteContentBlockId = insertOrReplaceNoteContentBlock(block)
                val insertedBlock = block.copy(id = noteContentBlockId)
                val isLastIndex = nwc.content.size.toLong() == insertedBlock.sectionIndex
                val newBlocks = if (isLastIndex) {
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
                noteWithContent = newNwc
                val index = newNwc.content.indexOfLast { it.id == noteContentBlockId }
                val focusRequester = cacheHolder.focusRequesterMap.getOrPut(
                    noteContentBlockId, ::FocusRequester
                )
                event.emit(NoteScreenEvent.AddNoteBlock(index, focusRequester))
                noteContentBlockId
            } ?: -1
        }
    }

    val getTextFieldState: (
        blockId: Long, content: String
    ) -> TextFieldState = { blockId, blockContent ->
        cacheHolder.contentBlockTextFieldStateMap.getOrPut(blockId) {
            TextFieldState(blockContent).also { textFieldState ->
                cacheHolder.collectUpdateJobMap[blockId]?.cancel()
                cacheHolder.collectUpdateJobMap[blockId] = coroutineScope.launch {
                    textFieldState.textAsFlow().map { charSequence ->
                        val nwc = noteWithContent ?: return@map charSequence
                        val blockIndex = nwc.content.indexOfLast { it.id == blockId }
                            .takeIf { it != -1 } ?: return@map charSequence
                        val block = nwc.content[blockIndex]
                        val enterIndex = charSequence.lastIndexOf('\n')
                        if (enterIndex >= 0) {
                            val block1Content = charSequence.subSequence(
                                0, enterIndex
                            ).toString()
                            val block2Content = charSequence.subSequence(
                                enterIndex + 1, charSequence.length
                            ).toString()
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
                            noteWithContent = nwc.copy(
                                content = nwc.content.toMutableList().apply {
                                    set(blockIndex, block.copy(content = block1Content))
                                    add(blockIndex + 1, block2.copy(id = block2Id))
                                }
                            )
                            block1Content
                        } else charSequence
                    }.debounce(0.8.seconds).collect { textFieldCharSequence ->
                        noteWithContent?.content?.find { it.id == blockId }?.let { block ->
                            insertOrReplaceNoteContentBlock(
                                block.copy(content = textFieldCharSequence.toString())
                            )
                        }
                    }
                }
            }
        }
    }

    val bottomBarMode = remember(noteWithContent, noteScreenMode) {
        noteWithContent?.let {
            when (noteScreenMode) {
                NoteScreenMode.Preview -> NoteBottomBarState.Preview
                NoteScreenMode.Edit -> NoteBottomBarState.Editing
            }
        } ?: NoteBottomBarState.Editing
    }

    val noteScreenState = remember(noteWithContent, noteScreenMode, bottomBarMode) {
        noteWithContent?.let { nwc ->
            NoteScreenState.Success(
                contentState = when (noteScreenMode) {
                    NoteScreenMode.Preview -> NoteContentState.Preview(
                        content = generateMarkdownText(nwc.note.title, nwc.content)
                    )

                    NoteScreenMode.Edit -> NoteContentState.Edit(
                        titleState = titleState,
                        contentStateList = nwc.content.map { block ->
                            val blockId = block.id!!
                            NoteBlockState(
                                id = blockId,
                                contentState = getTextFieldState(blockId, block.content),
                                interaction = cacheHolder
                                    .mutableInteractionSourceMap
                                    .getOrPut(blockId) {
                                        MutableInteractionSource().also { mis ->
                                            cacheHolder.collectFocusJobMap[blockId]?.cancel()
                                            cacheHolder.collectFocusJobMap[blockId] =
                                                coroutineScope.launch {
                                                    mis.interactions.collect { interaction ->
                                                        when (interaction) {
                                                            is FocusInteraction.Focus -> {
                                                                focusId = blockId
                                                            }

                                                            is FocusInteraction.Unfocus -> {
                                                                if (blockId == focusId) {
                                                                    focusId = null
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                        }
                                    },
                                focusRequester = cacheHolder
                                    .focusRequesterMap
                                    .getOrPut(blockId, ::FocusRequester)
                            )
                        }
                    )
                },
                bottomBarState = bottomBarMode
            )
        } ?: NoteScreenState.Loading
    }

    DisposableEffect(Unit) {
        onDispose {
            // Update note last modified time
            val nwc = noteWithContent ?: return@onDispose
            val note = nwc.note.takeIf { it.id != null } ?: return@onDispose
            coroutineScope.launch(NonCancellable + Dispatchers.IO) {
                if (nwc.content.isEmpty()) {
                    deleteNoteAndItsBlocks(note.id!!)
                } else {
                    insertOrReplaceNote(note.copy(time = getCurrentTimeMillis()))
                }
            }
        }
    }

    val action: (NoteScreenIntent) -> Unit = fun(intent) {
        when (intent) {
            is NoteScreenIntent.Format -> {
                val formatType = intent.formatType
                val focusingId = focusId ?: return
                val realType = if (formatType is FormatType.List.Ordered) {
                    // if formatting ordered list, we need to consider if pre block is ordered list and get its number.
                    val blocks = noteWithContent?.content ?: return
                    val focusingContentBlock = blocks.findLast { it.id == focusingId } ?: return
                    if (focusingContentBlock.sectionIndex > 0L) {
                        blocks.findLast {
                            it.sectionIndex == focusingContentBlock.sectionIndex - 1
                        }?.content?.orderListNum?.let { preNum ->
                            FormatType.List.Ordered(preNum + 1)
                        } ?: formatType
                    } else formatType
                } else formatType

                cacheHolder.contentBlockTextFieldStateMap[focusingId]?.let { tfs ->
                    tfs.edit {
                        if (asCharSequence().startsWith(realType.value)) {
                            delete(0, realType.value.length)
                        } else {
                            insert(0, realType.value)
                        }
                    }
                }
            }

            NoteScreenIntent.AddBlockToBottom -> coroutineScope.launch {
                val newBlock = NoteContentBlock(
                    id = null, noteId = noteId,
                    sectionIndex = noteWithContent?.content?.size?.toLong() ?: 0L,
                    content = ""
                )
                addContentBlock(newBlock)
            }

            is NoteScreenIntent.DeleteBlock -> coroutineScope.launch {
                databaseMutex.withLock {
                    noteWithContent?.let { nwc ->
                        nwc.content.find { it.id == intent.id }?.let { deletingBlock ->
                            val newBlocks = buildList {
                                val movingBlocks = mutableListOf<NoteContentBlock>()
                                nwc.content.forEachIndexed { index, block ->
                                    if (block.id == intent.id) {
                                        deleteNoteContentBlockUseCase(intent.id)
                                        cacheHolder.clearCache(intent.id)
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
                            noteWithContent = NoteWithContent(nwc.note, newBlocks)
                        }
                    }
                }
            }

            is NoteScreenIntent.ChangeNoteScreenMode -> {
                noteScreenMode = intent.mode
            }
        }
    }

    return remember(noteScreenState, event, action) {
        MviHolder(noteScreenState, event, action)
    }
}