@file:Suppress("FunctionName")

package little.goose.note.logic.note

import androidx.compose.foundation.text2.input.TextFieldState
import androidx.compose.foundation.text2.input.textAsFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import little.goose.data.note.bean.NoteContentBlock
import little.goose.data.note.bean.NoteWithContent
import kotlin.time.Duration.Companion.seconds

fun TextFieldStateGetter(
    coroutineScope: CoroutineScope,
    getNoteWithContent: () -> NoteWithContent?,
    updateNoteWithContent: (NoteWithContent?) -> Unit,
    addContentBlock: suspend (NoteContentBlock) -> Long?,
    contentBlockTextFieldStateMap: MutableMap<Long, TextFieldState>,
    collectUpdateJobMap: MutableMap<Long, Job>,
    insertOrReplaceNoteContentBlock: suspend (NoteContentBlock) -> Long
): (Long, String) -> TextFieldState {
    return { blockId, blockContent ->
        contentBlockTextFieldStateMap.getOrPut(blockId) {
            TextFieldState(blockContent).also { textFieldState ->
                collectUpdateJobMap[blockId]?.cancel()
                collectUpdateJobMap[blockId] = coroutineScope.launch {
                    var tempCharSequence: CharSequence? = null
                    textFieldState.textAsFlow().map { charSequence ->
                        val nwc = getNoteWithContent() ?: return@map charSequence
                        val blockIndex = nwc.content.indexOfLast { it.id == blockId }
                            .takeIf { it != -1 } ?: return@map charSequence
                        val block = nwc.content[blockIndex]
                        val enterIndex = charSequence.lastIndexOf('\n')
                        if (enterIndex >= 0) {
                            val block1Content = charSequence.subSequence(0, enterIndex).toString()
                            val block2Content =
                                charSequence.subSequence(enterIndex + 1, charSequence.length)
                                    .toString()
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
                            updateNoteWithContent(nwc.copy(
                                content = nwc.content.toMutableList().apply {
                                    set(blockIndex, block.copy(content = block1Content))
                                    add(blockIndex + 1, block2.copy(id = block2Id))
                                }
                            ))
                            block1Content
                        } else charSequence
                    }.onEach {
                        tempCharSequence = it
                    }.debounce(0.8.seconds).onCompletion {
                        coroutineScope.launch(NonCancellable) {
                            getNoteWithContent()?.content?.find { it.id == blockId }?.let { block ->
                                insertOrReplaceNoteContentBlock(
                                    block.copy(content = tempCharSequence.toString())
                                )
                            }
                        }
                    }.collect { textFieldCharSequence ->
                        getNoteWithContent()?.content?.find { it.id == blockId }?.let { block ->
                            insertOrReplaceNoteContentBlock(
                                block.copy(content = textFieldCharSequence.toString())
                            )
                        }
                    }
                }
            }
        }
    }
}