package little.goose.account.utils

import com.haibin.calendarview.Calendar

object CalendarViewUtils {

    fun getSchemeCalendar(
        year: Int, month: Int, day: Int
    ): Calendar {
        return Calendar().apply {
            setYear(year)
            setMonth(month)
            setDay(day)
        }
    }


}