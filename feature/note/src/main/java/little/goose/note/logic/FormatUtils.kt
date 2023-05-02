package little.goose.note.logic

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

sealed class FormatType {

    sealed class Header(val value: String) : FormatType() {
        object H1 : Header("# ")
        object H2 : Header("## ")
        object H3 : Header("### ")
        object H4 : Header("#### ")
        object H5 : Header("##### ")
        object H6 : Header("###### ")
    }

}

suspend fun TextFieldValue.format(
    type: FormatType
): TextFieldValue {
    return if (this.text.length < 711) {
        internalFormat(type)
    } else withContext(Dispatchers.Default) {
        this@format.internalFormat(type)
    }
}

private fun TextFieldValue.internalFormat(type: FormatType): TextFieldValue {
    return when (type) {
        is FormatType.Header -> {
            formatHeader(type)
        }

        else -> this
    }
}

private fun TextFieldValue.formatHeader(
    header: FormatType.Header
): TextFieldValue {
    val contentTextFieldValue = this
    var cursorPosition = contentTextFieldValue.selection.start
    var currentLineCursorPosition = 0
    val lines = mutableListOf<String>()
    var startSplitIndex = 0
    var currentLineIndex = 0
    var isSingleLine = true
    contentTextFieldValue.text.forEachIndexed { index, char ->
        if (char == '\n') {
            isSingleLine = false
            lines.add(contentTextFieldValue.text.substring(startSplitIndex, index))
            startSplitIndex = index + 1
            if (index < cursorPosition) {
                currentLineCursorPosition = 0
                currentLineIndex++
            }
        } else if (index < cursorPosition) {
            currentLineCursorPosition++
        }
    }
    if (isSingleLine) {
        lines.add(contentTextFieldValue.text)
    } else {
        lines.add(contentTextFieldValue.text.substring(startSplitIndex))
    }

    val currentLine = lines[currentLineIndex]
    lines[currentLineIndex] = if (currentLine.startsWith(header.value)) {
        cursorPosition -= minOf(header.value.length, currentLineCursorPosition)
        currentLine.substring(header.value.length)
    } else if (!currentLine.startsWith("#")) {
        cursorPosition += header.value.length
        header.value + currentLine
    } else {
        var level = 0
        var isHeader = false
        for (char in currentLine) {
            if (char == '#') {
                level++
            } else {
                isHeader = char == ' '
                break
            }
        }
        if (isHeader) {
            cursorPosition -= minOf((level - (header.value.length - 1)), currentLineCursorPosition)
            currentLine.replaceRange(0, level + 1, header.value)
        } else {
            cursorPosition += header.value.length
            header.value + currentLine
        }
    }
    val newText = lines.joinToString(separator = "\n")
    return contentTextFieldValue.copy(
        text = newText,
        selection = TextRange(maxOf(cursorPosition, 0))
    )
}