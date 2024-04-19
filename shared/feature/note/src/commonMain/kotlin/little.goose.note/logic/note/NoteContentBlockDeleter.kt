@file:Suppress("FunctionName")

package little.goose.note.logic.note

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import little.goose.data.note.bean.NoteContentBlock
import little.goose.data.note.bean.NoteWithContent
import little.goose.data.note.domain.DeleteBlockUseCase

internal fun NoteContentBlockDeleter(
    mutex: Mutex,
    getNoteWithContent: () -> NoteWithContent?,
    updateNoteWithContent: (NoteWithContent?) -> Unit,
    deleteNoteContentBlockUseCase: DeleteBlockUseCase,
    cacheHolder: NoteScreenCacheHolder,
    insertOrReplaceNoteContentBlocks: suspend (List<NoteContentBlock>) -> Unit
): suspend (id: Long) -> Unit {
    return { blockId ->
        mutex.withLock {
            getNoteWithContent()?.let { noteWithContent ->
                noteWithContent.content.find { it.id == blockId }?.let { deletingBlock ->
                    val newBlocks = buildList {
                        val movingBlocks = mutableListOf<NoteContentBlock>()
                        var deletingIndex = -1
                        noteWithContent.content.forEachIndexed { index, block ->
                            if (block.id == blockId) {
                                deletingIndex = index
                                deleteNoteContentBlockUseCase(blockId)
                            } else if (index < deletingBlock.sectionIndex) {
                                add(block)
                            } else {
                                block.copy(sectionIndex = index.toLong() - 1).also {
                                    add(it)
                                    movingBlocks.add(it)
                                }
                            }
                        }
                        // update memory data in case dirty data.
                        if (deletingIndex != -1) {
                            updateNoteWithContent(
                                noteWithContent.copy(
                                    content = noteWithContent.content.toMutableList()
                                        .apply { removeAt(deletingIndex) }
                                )
                            )
                        }
                        cacheHolder.clearCache(blockId)
                        insertOrReplaceNoteContentBlocks(movingBlocks)
                    }
                    updateNoteWithContent(NoteWithContent(noteWithContent.note, newBlocks))
                }
            }
        }
    }
}