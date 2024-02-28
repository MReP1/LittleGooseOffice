package little.goose.shared.common.utils

fun Float.progressWith(start: Float, min: Float, max: Float): Float {
    return if (this < start) {
        min
    } else {
        ((this - start) / (max - start)).coerceAtMost(max)
    }
}