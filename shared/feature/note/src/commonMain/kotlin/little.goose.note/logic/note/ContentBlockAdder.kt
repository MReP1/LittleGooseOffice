@file:Suppress("FunctionName")

package little.goose.note.logic.note

import androidx.compose.ui.focus.FocusRequester
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import little.goose.data.note.bean.Note
import little.goose.data.note.bean.NoteContentBlock
import little.goose.data.note.bean.NoteWithContent
import little.goose.note.event.NoteScreenEvent

internal fun BottomBlockAdder(
    getBottomIndex: () -> Int,
    getNoteId: () -> Long,
    addContentBlock: suspend (block: NoteContentBlock) -> Long
): suspend () -> Unit {
    return {
        addContentBlock(
            NoteContentBlock(
                id = null,
                noteId = getNoteId(),
                sectionIndex = getBottomIndex().toLong(),
                content = ""
            )
        )
    }
}

internal fun ContentBlockAdder(
    mutex: Mutex,
    getNoteWithContent: () -> NoteWithContent?,
    updateNoteWithContent: (NoteWithContent?) -> Unit,
    insertOrReplaceNote: suspend (Note) -> Long,
    updateNoteId: (Long) -> Unit,
    focusRequesterMap: MutableMap<Long, FocusRequester>,
    emitEvent: suspend (NoteScreenEvent) -> Unit,
    insertOrReplaceNoteContentBlock: suspend (NoteContentBlock) -> Long,
    insertOrReplaceNoteContentBlocks: suspend (List<NoteContentBlock>) -> Unit
): suspend (block: NoteContentBlock) -> Long {
    return { block ->
        mutex.withLock {
            getNoteWithContent()?.let { nwc ->
                var noteWithContent = nwc

                val insertingBlock = if (block.noteId == null || block.noteId == -1L) {
                    // If this note doesn't exit, insert the note first.
                    val noteId = insertOrReplaceNote(noteWithContent.note)
                    noteWithContent = noteWithContent.copy(noteWithContent.note.copy(id = noteId))
                    updateNoteWithContent(noteWithContent)
                    updateNoteId(noteId)
                    block.copy(noteId = noteId)
                } else block

                // Insert the content block
                val noteContentBlockId = insertOrReplaceNoteContentBlock(insertingBlock)
                val insertedBlock = insertingBlock.copy(id = noteContentBlockId)

                val isLastIndex = noteWithContent.content.size.toLong() == insertedBlock.sectionIndex
                val newBlocks = if (isLastIndex) {
                    // Add block to end
                    noteWithContent.content + insertedBlock
                } else {
                    buildList {
                        val movingBlocks = mutableListOf<NoteContentBlock>()
                        noteWithContent.content.forEachIndexed { index, noteContentBlock ->
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
                val newNwc = noteWithContent.copy(content = newBlocks)
                updateNoteWithContent(newNwc)
                val index = newNwc.content.indexOfLast { it.id == noteContentBlockId }
                val focusRequester = focusRequesterMap.getOrPut(noteContentBlockId, ::FocusRequester)
                emitEvent(NoteScreenEvent.AddNoteBlock(index, focusRequester))
                noteContentBlockId
            } ?: -1
        }
    }
}