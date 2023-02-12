package little.goose.schedule.utils

import little.goose.common.utils.getRealDate
import java.util.*
import kotlin.collections.HashMap

private val calendar by lazy { Calendar.getInstance() }

fun List<little.goose.schedule.data.entities.Schedule>.getMapDayBoolean(): HashMap<Int, Boolean> {
    val map = HashMap<Int, Boolean>()
    for (schedule in this) {
        map[schedule.time.getRealDate(calendar)] = true
    }
    return map
}