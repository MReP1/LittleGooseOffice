@file:Suppress("FunctionName")

package little.goose.note.logic

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import little.goose.data.note.bean.NoteContentBlock
import little.goose.data.note.bean.NoteWithContent

internal fun NoteContentBlockDeleter(
    coroutineScope: CoroutineScope,
    getNoteWithContent: () -> NoteWithContent?,
    updateNoteWithContent: (NoteWithContent?) -> Unit,
    deleter: suspend (id: Long) -> Unit,
    insertOrReplaceNoteContentBlocks: suspend (List<NoteContentBlock>) -> Unit
): suspend (id: Long) -> Unit {
    val channel = Channel<Long>()
    coroutineScope.launch {
        for (blockId in channel) {
            val noteWithContent = getNoteWithContent() ?: return@launch
            val deletingBlock = noteWithContent.content.find { it.id == blockId } ?: return@launch
            val newBlocks = buildList {
                val movingBlocks = mutableListOf<NoteContentBlock>()
                noteWithContent.content.forEachIndexed { index, block ->
                    if (block.id == blockId) {
                        deleter(blockId)
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
            updateNoteWithContent(NoteWithContent(noteWithContent.note, newBlocks))
        }
    }
    return channel::send
}