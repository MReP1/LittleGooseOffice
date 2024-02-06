package little.goose.note.util

sealed class FormatType(val value: String) {

    sealed class Header(value: String) : FormatType(value) {
        data object H1 : Header("# ")
        data object H2 : Header("## ")
        data object H3 : Header("### ")
        data object H4 : Header("#### ")
        data object H5 : Header("##### ")
        data object H6 : Header("###### ")
    }

    sealed class List(value: String) : FormatType(value) {
        data object Unordered : List("- ")
        data class Ordered(private val num: Int) : List("$num. ")
    }

    data object Quote : FormatType("> ")
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