@file:Suppress("FunctionName")

package little.goose.note.logic

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text2.input.TextFieldState
import androidx.compose.foundation.text2.input.placeCursorAtEnd
import androidx.compose.ui.focus.FocusRequester
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import little.goose.data.note.bean.NoteContentBlock
import little.goose.data.note.bean.NoteWithContent
import little.goose.data.note.domain.InsertOrReplaceNoteContentBlockUseCase
import little.goose.data.note.domain.InsertOrReplaceNoteUseCase
import little.goose.note.ui.note.NoteBlockState
import little.goose.note.ui.note.NoteContentState
import little.goose.note.ui.note.NoteScreenMode

internal fun ContentStateMapper(
    coroutineScope: CoroutineScope,
    getNoteWithContent: () -> NoteWithContent?,
    updateNoteWithContent: (NoteWithContent?) -> Unit,
    getNoteId: () -> Long,
    updateNoteId: (Long) -> Unit,
    getFocusingId: () -> Long?,
    updateFocusingId: (Long?) -> Unit,
    addContentBlock: suspend (NoteContentBlock) -> Long?,
    insertOrReplaceNote: InsertOrReplaceNoteUseCase,
    contentBlockTextFieldStateMap: MutableMap<Long, TextFieldState>,
    collectUpdateJobMap: MutableMap<Long, Job>,
    collectFocusJobMap: MutableMap<Long, Job>,
    focusRequesterMap: MutableMap<Long, FocusRequester>,
    mutableInteractionSourceMap: MutableMap<Long, MutableInteractionSource>,
    insertOrReplaceNoteContentBlock: InsertOrReplaceNoteContentBlockUseCase
): (NoteWithContent, NoteScreenMode) -> NoteContentState {

    val titleState = TitleTextFieldState(
        coroutineScope = coroutineScope,
        getNoteWithContent = getNoteWithContent,
        getNoteId = getNoteId,
        updateNoteId = updateNoteId,
        insertOrReplaceNote = insertOrReplaceNote
    )

    val generatorMarkdownText: (
        title: String,
        blocks: List<NoteContentBlock>
    ) -> String = MarkdownTextGenerator()

    val getTextFieldState: (
        blockId: Long,
        content: String
    ) -> TextFieldState = TextFieldStateGetter(
        coroutineScope = coroutineScope,
        getNoteWithContent = getNoteWithContent,
        updateNoteWithContent = updateNoteWithContent,
        addContentBlock = addContentBlock,
        contentBlockTextFieldStateMap = contentBlockTextFieldStateMap,
        collectUpdateJobMap = collectUpdateJobMap,
        insertOrReplaceNoteContentBlock = insertOrReplaceNoteContentBlock
    )

    val generateInteractionSource: (
        id: Long
    ) -> MutableInteractionSource = InteractionSourceGetter(
        coroutineScope = coroutineScope,
        mutableInteractionSourceMap = mutableInteractionSourceMap,
        collectFocusJobMap = collectFocusJobMap,
        getFocusingId = getFocusingId,
        updateFocusingId = updateFocusingId
    )

    return { noteWithContent, noteScreenMode ->
        when (noteScreenMode) {
            NoteScreenMode.Preview -> NoteContentState.Preview(
                content = generatorMarkdownText(noteWithContent.note.title, noteWithContent.content)
            )

            NoteScreenMode.Edit -> NoteContentState.Edit(
                titleState = titleState.apply {
                    if (!text.contentEquals(noteWithContent.note.title)) {
                        edit {
                            replace(0, length, noteWithContent.note.title)
                            placeCursorAtEnd()
                        }
                    }
                },
                contentStateList = noteWithContent.content.map { block ->
                    val blockId = block.id!!
                    NoteBlockState(
                        id = blockId,
                        contentState = getTextFieldState(blockId, block.content),
                        interaction = generateInteractionSource(blockId),
                        focusRequester = focusRequesterMap.getOrPut(blockId, ::FocusRequester)
                    )
                }
            )
        }
    }
}