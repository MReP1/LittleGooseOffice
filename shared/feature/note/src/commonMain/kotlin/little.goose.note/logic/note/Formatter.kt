@file:Suppress("FunctionName")

package little.goose.note.logic.note

import androidx.compose.foundation.text2.input.TextFieldState
import androidx.compose.foundation.text2.input.delete
import androidx.compose.foundation.text2.input.insert
import little.goose.data.note.bean.NoteContentBlock
import little.goose.note.util.FormatType
import little.goose.note.util.orderListNum

internal fun TextFormatter(
    getBlocks: () -> List<NoteContentBlock>?,
    getFocusingId: () -> Long?,
    getContentBlockTextFieldState: (id: Long) -> TextFieldState?
) = fun(formatType: FormatType) {
    val focusingId = getFocusingId() ?: return
    val realType = if (formatType is FormatType.List.Ordered) {
        // if formatting ordered list, we need to consider if pre block is ordered list and get its number.
        val blocks = getBlocks() ?: return
        val focusingContentBlock = blocks.findLast { it.id == focusingId } ?: return
        if (focusingContentBlock.sectionIndex > 0L) {
            blocks.findLast {
                it.sectionIndex == focusingContentBlock.sectionIndex - 1
            }?.content?.orderListNum?.let { preNum ->
                FormatType.List.Ordered(preNum + 1)
            } ?: formatType
        } else formatType
    } else formatType

    getContentBlockTextFieldState(focusingId)?.let { tfs ->
        tfs.edit {
            if (asCharSequence().startsWith(realType.value)) {
                delete(0, realType.value.length)
            } else {
                insert(0, realType.value)
            }
        }
    }
}