@file:Suppress("FunctionName")

package little.goose.note.logic

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import little.goose.data.note.bean.NoteContentBlock
import little.goose.data.note.bean.NoteWithContent

internal fun NoteContentBlockDeleter(
    mutex: Mutex,
    getNoteWithContent: () -> NoteWithContent?,
    updateNoteWithContent: (NoteWithContent?) -> Unit,
    deleter: suspend (id: Long) -> Unit,
    insertOrReplaceNoteContentBlocks: suspend (List<NoteContentBlock>) -> Unit
): suspend (id: Long) -> Unit {
    return { blockId ->
        mutex.withLock {
            getNoteWithContent()?.let { noteWithContent ->
                noteWithContent.content.find { it.id == blockId }?.let { deletingBlock ->
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
        }
    }
}