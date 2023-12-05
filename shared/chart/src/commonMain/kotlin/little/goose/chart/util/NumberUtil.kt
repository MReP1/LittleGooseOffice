package little.goose.chart.util

fun String.roundTo(num: Int): String {
    val dotIndex = this.indexOf('.')
    return if (dotIndex > 0 && dotIndex + num < lastIndex) {
        substring(0, dotIndex + num)
    } else this
}