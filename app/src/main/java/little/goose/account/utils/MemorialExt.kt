package little.goose.account.utils

import android.content.Context
import little.goose.account.R
import little.goose.account.appContext
import little.goose.account.logic.data.entities.Memorial
import java.util.*
import kotlin.collections.HashMap

private val calendar by lazy { Calendar.getInstance() }

fun String.appendTimeSuffix(time: Date, context: Context = appContext) = if (time.isFuture()) {
    appContext.getString(R.string.content_future, this)
} else {
    context.getString(R.string.content_past, this)
}

fun String.appendTimePrefix(time: Date) = if (time.isFuture()) {
    appContext.getString(R.string.target_time, this)
} else {
    appContext.getString(R.string.original_time, this)
}

fun List<Memorial>.getMapDayBoolean(): Map<Int, Boolean> {
    val map = HashMap<Int, Boolean>()
    for (memorial in this) {
        map[memorial.time.getRealDate(calendar)] = true
    }
    return map
}