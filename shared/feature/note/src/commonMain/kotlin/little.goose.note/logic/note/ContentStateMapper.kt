@file:Suppress("FunctionName")

package little.goose.note.logic.note

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text2.input.TextFieldState
import androidx.compose.foundation.text2.input.placeCursorAtEnd
import androidx.compose.foundation.text2.input.textAsFlow
import androidx.compose.ui.focus.FocusRequester
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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
    cacheHolder: NoteScreenCacheHolder,
    insertOrReplaceNoteContentBlock: InsertOrReplaceNoteContentBlockUseCase
): (NoteWithContent, NoteScreenMode) -> NoteContentState {

    val titleState = TextFieldState().apply {
        textAsFlow().onEach { title ->
            getNoteWithContent()?.note?.copy(title = title.toString())?.let { note ->
                val id = insertOrReplaceNote(note)
                if (getNoteId() == -1L) {
                    updateNoteId(id)
                }
            }
        }.launchIn(coroutineScope)
    }

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
        contentBlockTextFieldStateMap = cacheHolder.contentBlockTextFieldStateMap,
        collectUpdateJobMap = cacheHolder.collectUpdateJobMap,
        insertOrReplaceNoteContentBlock = insertOrReplaceNoteContentBlock
    )

    val getInteractionSource: (
        id: Long
    ) -> MutableInteractionSource = InteractionSourceGetter(
        coroutineScope = coroutineScope,
        mutableInteractionSourceMap = cacheHolder.mutableInteractionSourceMap,
        collectFocusJobMap = cacheHolder.collectFocusJobMap,
        getFocusingId = getFocusingId,
        updateFocusingId = updateFocusingId
    )

    val getFocusRequester: (
        id: Long
    ) -> FocusRequester = { blockId ->
        cacheHolder.focusRequesterMap.getOrPut(blockId, ::FocusRequester)
    }

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
                        interaction = getInteractionSource(blockId),
                        focusRequester = getFocusRequester(blockId)
                    )
                }
            )
        }
    }
}