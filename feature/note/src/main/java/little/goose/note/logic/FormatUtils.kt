package little.goose.note.logic

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

sealed class FormatType(val value: String) {

    sealed class Header(value: String) : FormatType(value) {
        object H1 : Header("# ")
        object H2 : Header("## ")
        object H3 : Header("### ")
        object H4 : Header("#### ")
        object H5 : Header("##### ")
        object H6 : Header("###### ")
    }

    sealed class List(value: String) : FormatType(value) {
        object Unordered : List("- ")
        data class Ordered(private val num: Int) : List("$num. ")
    }

}

suspend fun String.format(
    type: FormatType
): String {
    return if (this.length < 711) {
        internalFormat(type)
    } else withContext(Dispatchers.Default) {
        this@format.internalFormat(type)
    }
}

private fun String.internalFormat(type: FormatType): String {
    return when (type) {
        is FormatType.Header -> {
            formatHeader(type)
        }

        is FormatType.List -> {
            formatList(type)
        }
    }
}

private fun String.formatHeader(
    header: FormatType.Header
): String {
    return if (this.startsWith(header.value)) {
        this.substring(header.value.length)
    } else if (!this.startsWith("#")) {
        header.value + this
    } else {
        var level = 0
        var isHeader = false
        for (char in this) {
            if (char == '#') {
                level++
            } else {
                isHeader = char == ' '
                break
            }
        }
        if (isHeader) {
            this.replaceRange(0, level + 1, header.value)
        } else {
            header.value + this
        }
    }
}

private fun String.formatList(
    list: FormatType.List
): String {
    return if (this.startsWith(list.value)) {
        this.substring(list.value.length)
    } else {
        val num = this.orderListNum
        if (num > 0) {
            this.replaceRange(0, num.length + 2, list.value)
        } else {
            list.value + this
        }
    }
}

fun TextFieldValue.format(
    type: FormatType
): TextFieldValue {
    return when (type) {
        is FormatType.Header -> {
            formatHeader(type)
        }

        is FormatType.List -> {
            formatList(type)
        }
    }
}

private fun TextFieldValue.formatHeader(
    header: FormatType.Header
): TextFieldValue {
    return if (this.text.startsWith(header.value)) {
        this.copy(
            text = this.text.substring(header.value.length),
            selection = TextRange(
                this.selection.start - header.value.length,
                this.selection.end - header.value.length
            )
        )
    } else if (!this.text.startsWith("#")) {
        this.copy(
            text = header.value + this.text,
            selection = TextRange(
                this.selection.start + header.value.length,
                this.selection.end + header.value.length
            )
        )
    } else {
        var level = 0
        var isHeader = false
        for (char in this.text) {
            if (char == '#') {
                level++
            } else {
                isHeader = char == ' '
                break
            }
        }
        if (isHeader) {
            this.copy(
                text = this.text.replaceRange(0, level + 1, header.value),
                selection = TextRange(
                    this.selection.start + header.value.length - level - 1,
                    this.selection.end + header.value.length - level - 1
                )
            )
        } else {
            this.copy(
                text = header.value + this.text,
                selection = TextRange(
                    this.selection.start + header.value.length,
                    this.selection.end + header.value.length
                )
            )
        }
    }
}

private fun TextFieldValue.formatList(
    list: FormatType.List
): TextFieldValue {
    return if (this.text.startsWith(list.value)) {
        this.copy(
            text = this.text.substring(list.value.length),
            selection = TextRange(
                this.selection.start - list.value.length,
                this.selection.end - list.value.length
            )
        )
    } else {
        val num = this.text.orderListNum
        if (num > 0) {
            this.copy(
                text = this.text.replaceRange(0, num.length + 2, list.value),
                selection = TextRange(
                    this.selection.start + list.value.length - num.length - 2,
                    this.selection.end + list.value.length - num.length - 2
                )
            )
        } else {
            this.copy(
                text = list.value + this.text,
                selection = TextRange(
                    this.selection.start + list.value.length,
                    this.selection.end + list.value.length
                )
            )
        }
    }
}

private val Int.length: Int
    get() = when (this) {
        0 -> 1
        else -> {
            var length = 0
            var num = this
            while (num > 0) {
                num /= 10
                length++
            }
            length
        }
    }

val String.orderListNum: Int
    get() {
        var num = 0
        if (this.length >= 3) {
            for (index in this.indices) {
                val char = this[index]
                if (char.isDigit()) {
                    num = num * 10 + char.code - 48
                } else {
                    if (char == '.') {
                        if (this.lastIndex > index) {
                            if (this[index + 1] == ' ') {
                                return num
                            } else break
                        } else break
                    } else break
                }
            }
        }
        return 0
    }

val String.formatNum: Int
    get() {
        return if (this.length >= 3) runCatching {
            if (this[0] == '-' && this[1] == ' ') {
                FormatType.List.Unordered.value.length
            } else if (this[0] == '#') {
                var level = 0
                var isHeader = false
                for (char in this) {
                    if (char == '#') {
                        level++
                    } else {
                        isHeader = char == ' '
                        break
                    }
                }
                if (isHeader) level + 1 else 0
            } else if (this[0].isDigit()) {
                orderListNum
            } else 0
        }.fold(
            onSuccess = { it },
            onFailure = { 0 }
        ) else 0
    }

val String.formatType: FormatType?
    get() {
        return if (this.length >= 3) runCatching {
            if (this[0] == '-' && this[1] == ' ') {
                FormatType.List.Unordered
            } else if (this[0] == '#') {
                var level = 0
                var isHeader = false
                for (char in this) {
                    if (char == '#') {
                        level++
                    } else {
                        isHeader = char == ' '
                        break
                    }
                }
                if (isHeader) when (level) {
                    1 -> FormatType.Header.H1
                    2 -> FormatType.Header.H2
                    3 -> FormatType.Header.H3
                    4 -> FormatType.Header.H4
                    5 -> FormatType.Header.H5
                    6 -> FormatType.Header.H6
                    else -> null
                } else null
            } else if (this[0].isDigit()) {
                val num = orderListNum
                if (num > 0) {
                    FormatType.List.Ordered(num)
                } else null
            } else null
        }.fold(
            onSuccess = { it },
            onFailure = { null }
        ) else null
    }

/**
 * ###
 */

suspend fun TextFieldValue.format2(
    type: FormatType
): TextFieldValue {
    return if (this.text.length < 711) {
        internalFormat2(type)
    } else withContext(Dispatchers.Default) {
        this@format2.internalFormat2(type)
    }
}

private fun TextFieldValue.internalFormat2(type: FormatType): TextFieldValue {
    return when (type) {
        is FormatType.Header -> {
            formatHeader2(type)
        }

        else -> this
    }
}

private fun TextFieldValue.formatHeader2(
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