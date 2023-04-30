package little.goose.note.logic

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun TextFieldValue.formatH1(): TextFieldValue {
    return if (this.text.length < 500) {
        internalFormatH1()
    } else withContext(Dispatchers.Default) {
        this@formatH1.internalFormatH1()
    }
}

private fun TextFieldValue.internalFormatH1(): TextFieldValue {
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
            if (index <= cursorPosition) {
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
    lines[currentLineIndex] = if (currentLine.startsWith("# ")) {
        cursorPosition -= minOf(2, currentLineCursorPosition)
        currentLine.substring(2)
    } else if (!currentLine.startsWith("#")) {
        cursorPosition += 2
        "# $currentLine"
    } else {
        var level = 0
        val firstBlank = currentLine.indexOf(' ')
        val isHeader = (firstBlank > 0) &&
                currentLine.subSequence(0, firstBlank).all { char ->
                    (char == '#').also { if (it) level++ }
                }
        if (isHeader) {
            cursorPosition -= minOf((level - 1), currentLineCursorPosition)
            currentLine.replaceRange(0, level, "#")
        } else {
            cursorPosition += 2
            "# $currentLine"
        }
    }
    val newText = lines.joinToString(separator = "\n")
    return contentTextFieldValue.copy(
        text = newText,
        selection = TextRange(maxOf(cursorPosition, 0))
    )
}