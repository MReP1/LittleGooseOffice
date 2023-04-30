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
    val lines = contentTextFieldValue.text
        .split("\n")
        .toMutableList()
    var cursorPosition = contentTextFieldValue.selection.start
    var currentLineCursorPosition = 0
    val currentLineIndex = contentTextFieldValue.text
        .subSequence(0, cursorPosition)
        .count { char ->
            (char == '\n').also {
                if (it) {
                    currentLineCursorPosition = 0
                } else {
                    currentLineCursorPosition++
                }
            }
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