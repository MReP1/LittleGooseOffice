package little.goose.note.logic

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

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
        if (list is FormatType.List.Ordered) {
            val num = this.text.orderListNum
            if (num > 0) {
                return this.copy(
                    text = this.text.replaceRange(0, num.length + 2, list.value),
                    selection = TextRange(
                        this.selection.start + list.value.length - num.length - 2,
                        this.selection.end + list.value.length - num.length - 2
                    )
                )
            }
        }
        this.copy(
            text = list.value + this.text,
            selection = TextRange(
                this.selection.start + list.value.length,
                this.selection.end + list.value.length
            )
        )
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