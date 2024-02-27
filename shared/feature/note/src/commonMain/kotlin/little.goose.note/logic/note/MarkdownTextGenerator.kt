@file:Suppress("FunctionName")

package little.goose.note.logic.note

import little.goose.data.note.bean.NoteContentBlock

internal fun MarkdownTextGenerator(): (title: String, blocks: List<NoteContentBlock>) -> String {
    var currentTitle: String? = null
    var currentBlocks: List<NoteContentBlock>? = null
    var currentResult: String? = null
    return { title, blocks ->
        if (title == currentTitle && blocks === currentBlocks && currentResult != null) {
            currentResult!!
        } else buildString {
            if (title.isNotBlank()) {
                append("# ${title}\n\n")
            }
            append(blocks.joinToString("\n\n") { it.content })
        }.also {
            currentBlocks = blocks
            currentTitle = title
            currentResult = it
        }
    }
}