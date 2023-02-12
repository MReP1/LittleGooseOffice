package little.goose.memorial.utils

import android.content.Context
import little.goose.common.utils.getRealDate
import little.goose.common.utils.isFuture
import little.goose.memorial.R
import little.goose.memorial.data.entities.Memorial
import java.util.*

private val calendar by lazy { Calendar.getInstance() }

fun String.appendTimeSuffix(time: Date, context: Context) = if (time.isFuture()) {
    context.getString(R.string.content_future, this)
} else {
    context.getString(R.string.content_past, this)
}

fun String.appendTimePrefix(time: Date, context: Context) = if (time.isFuture()) {
    context.getString(R.string.target_time, this)
} else {
    context.getString(R.string.original_time, this)
}

fun List<Memorial>.getMapDayBoolean(): Map<Int, Boolean> {
    val map = HashMap<Int, Boolean>()
    for (memorial in this) {
        map[memorial.time.getRealDate(calendar)] = true
    }
    return map
}