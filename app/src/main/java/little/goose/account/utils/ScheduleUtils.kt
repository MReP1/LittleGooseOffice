package little.goose.account.utils

import little.goose.account.logic.data.entities.Schedule
import java.util.*
import kotlin.collections.HashMap

private val calendar by lazy { Calendar.getInstance() }

fun List<Schedule>.getMapDayBoolean(): HashMap<Int, Boolean> {
    val map = HashMap<Int, Boolean>()
    for (schedule in this) {
        map[schedule.time.getRealDate(calendar)] = true
    }
    return map
}