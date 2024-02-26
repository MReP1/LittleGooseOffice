package little.goose.data.note.bean

import little.goose.shared.common.getCurrentTimeMillis

data class Note(
    val id: Long? = null,
    val title: String = "",
    val time: Long = getCurrentTimeMillis()
)