package little.goose.common.utils

private var id = 0L
fun generateUnitId(): Long = synchronized(Unit) { id++ }