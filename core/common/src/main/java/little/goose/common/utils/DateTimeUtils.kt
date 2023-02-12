@file:Suppress("NOTHING_TO_INLINE")

package little.goose.common.utils

import android.content.Context
import little.goose.common.R
import little.goose.common.utils.DateTimeUtils.getWeekFormYearMonthDate
import java.util.*
import kotlin.math.roundToLong

object DateTimeUtils {

    lateinit var appContext: Context

    private val hours = listOf(
        "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14",
        "15", "16", "17", "18", "19", "20", "21", "22", "23"
    )

    private val minutes = listOf(
        "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
        "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
        "21", "22", "23", "24", "25", "26", "27", "28", "29", "30",
        "31", "32", "33", "34", "35", "36", "37", "38", "39", "40",
        "41", "42", "43", "44", "45", "46", "47", "48", "49", "50",
        "51", "52", "53", "54", "55", "56", "57", "58", "59"
    )

    private val years = listOf(
        "2001", "2002", "2003", "2004", "2005", "2006", "2007", "2008",
        "2009", "2010", "2011", "2012", "2013", "2014", "2015", "2016",
        "2017", "2018", "2019", "2020", "2021", "2022", "2023", "2024",
        "2025", "2026", "2027", "2028", "2029", "2030", "2031", "2032",
        "2033", "2034", "2035", "2036", "2037", "2038", "2039", "2040",
    )

    private val months = listOf(
        "1", "2", "3", "4", "5", "6",
        "7", "8", "9", "10", "11", "12"
    )

    fun getYearsList() = years
    fun getMonthsList() = months
    fun getHoursList() = hours
    fun getMinuteList() = minutes
    fun getDaysList(year: Int, month: Int) = (1..getDaysByYearMonth(year, month)).toList()

    fun getDaysByYearMonth(year: Int, month: Int, calendar: Calendar = Calendar.getInstance()) =
        calendar.run {
            setYear(year)
            setMonth(month)
            setDate(1)
            roll(Calendar.DATE, -1)
            getDate()
        }

    fun getDaysByCalendar(calendar: Calendar = Calendar.getInstance()) = calendar.run {
        setDate(1)
        roll(Calendar.DATE, -1)
        getDate()
    }

    fun getCurrentYear() = Calendar.getInstance().run { getYear() }

    fun getCurrentMonth() = Calendar.getInstance().run { getMonth() }

    fun getCurrentDate() = Calendar.getInstance().run { getDate() }

    fun getSelectDateOfThisTime(year: Int, month: Int, day: Int): Date =
        Calendar.getInstance().run {
            setYear(year)
            setMonth(month)
            setDate(day)
            time
        }

    fun getWeekDay(weekDay: Int, context: Context = appContext): String {
        return when (weekDay) {
            1 -> context.getString(R.string.sunday)
            2 -> context.getString(R.string.monday)
            3 -> context.getString(R.string.tuesday)
            4 -> context.getString(R.string.wednesday)
            5 -> context.getString(R.string.thursday)
            6 -> context.getString(R.string.friday)
            7 -> context.getString(R.string.saturday)
            else -> "unknown"
        }
    }

    fun getWeekDayZ(weekDay: Int): String {
        return when (weekDay) {
            1 -> appContext.getString(R.string.sunday_z)
            2 -> appContext.getString(R.string.monday_z)
            3 -> appContext.getString(R.string.tuesday_z)
            4 -> appContext.getString(R.string.wednesday_z)
            5 -> appContext.getString(R.string.thursday_z)
            6 -> appContext.getString(R.string.friday_z)
            7 -> appContext.getString(R.string.saturday_z)
            else -> "unknown"
        }
    }

    fun getWeekFormYearMonthDate(
        year: Int,
        month: Int,
        date: Int,
        context: Context = appContext
    ): String {
        return Calendar.getInstance().run {
            setYear(year)
            setMonth(month)
            setDate(date)
            getWeekDay(get(Calendar.DAY_OF_WEEK), context)
        }
    }

    fun getWeekFormYearMonthDateZ(year: Int, month: Int, date: Int): String {
        return Calendar.getInstance().run {
            setYear(year)
            setMonth(month)
            setDate(date)
            getWeekDayZ(get(Calendar.DAY_OF_WEEK))
        }
    }

    fun getTimeFormatTen(time: Int): String {
        return if (time >= 10) {
            time.toString()
        } else {
            "0$time"
        }
    }

    fun getBetweenDay(calendar1: Calendar, calendar2: Calendar) =
        if (calendar1.timeInMillis > calendar2.timeInMillis) {
            ((calendar1.timeInMillis - calendar2.timeInMillis).toDouble() / 3600000 / 24).roundToLong()
        } else {
            ((calendar2.timeInMillis - calendar1.timeInMillis).toDouble() / 3600000 / 24).roundToLong()
        }

    fun getBetweenMonthDay(calendar1: Calendar, calendar2: Calendar): MonthDay {
        val cal1: Calendar
        val cal2: Calendar
        if (calendar1.timeInMillis > calendar2.timeInMillis) {
            cal1 = calendar1
            cal2 = calendar2
        } else {
            cal1 = calendar2
            cal2 = calendar1
        }
        val year = cal1.getYear() - cal2.getYear()
        var month = cal1.getMonth() - cal2.getMonth() + year * 12
        var day = cal1.getDate() - cal2.getDate()
        if (day < 0) {
            val tempDay = getDaysByYearMonth(cal1.getYear(), cal1.getMonth() - 1)
            month--
            day += tempDay
        }
        return MonthDay(month, day)
    }
}

fun Date.toChineseStringWithYear(): String {
    Calendar.getInstance().apply {
        time = this@toChineseStringWithYear
        val year = getYear()
        val month = getMonth()
        val day = get(Calendar.DATE)
        val hour = DateTimeUtils.getTimeFormatTen(get(Calendar.HOUR_OF_DAY))
        val minute = DateTimeUtils.getTimeFormatTen(get(Calendar.MINUTE))
        return "${year}年${month}月${day}日 $hour : $minute"
    }
}

fun Date.toChineseYearMonDayWeek(context: Context = DateTimeUtils.appContext): String {
    Calendar.getInstance().apply {
        time = this@toChineseYearMonDayWeek
        val year = getYear()
        val month = getMonth()
        val day = getDate()
        return "${year}年${month}月${day}日 ${getWeekFormYearMonthDate(year, month, day, context)}"
    }
}

fun Date.toChineseMonthDayTime(): String {
    Calendar.getInstance().apply {
        time = this@toChineseMonthDayTime
        val month = getMonth()
        val day = getDate()
        val hour = DateTimeUtils.getTimeFormatTen(get(Calendar.HOUR_OF_DAY))
        val minute = DateTimeUtils.getTimeFormatTen(get(Calendar.MINUTE))
        return "${month}月${day}日 $hour : $minute"
    }
}

fun Date.toChineseMonthDay(): String {
    Calendar.getInstance().apply {
        time = this@toChineseMonthDay
        val month = getMonth()
        val day = getDate()
        return "${month}月${day}日"
    }
}

fun Date.toChineseYearMonthDay(): String {
    Calendar.getInstance().apply {
        time = this@toChineseYearMonthDay
        val year = getYear()
        val month = getMonth()
        val day = getDate()
        return "${year}年${month}月${day}日"
    }
}

fun Date.toChineseYearMonth(): String {
    Calendar.getInstance().apply {
        time = this@toChineseYearMonth
        val year = getYear()
        val month = getMonth()
        return "${year}年${month}月"
    }
}

fun Date.toChineseYear(): String {
    return Calendar.getInstance().run {
        time = this@toChineseYear
        val year = getYear()
        "${year}年"
    }
}

fun Date.toChineseMonth(): String {
    Calendar.getInstance().apply {
        time = this@toChineseMonth
        return "${getMonth()}月"
    }
}

fun getOneDayRange(year: Int, month: Int, date: Int): TimeRange {
    val calendar = Calendar.getInstance()
    calendar.apply {
        clear()
        setDate(date)
        setYear(year)
        setMonth(month)
    }
    val startTime = calendar.timeInMillis
    val endTime = startTime + 24L * 3600L * 1000L
    return TimeRange(startTime, endTime)
}

fun getOneMonthRange(year: Int, month: Int): TimeRange {
    val calendar = Calendar.getInstance()
    calendar.apply {
        clear()
        setDate(1)
        setYear(year)
        setMonth(month)
    }
    val startTime = calendar.timeInMillis
    val dayNum = DateTimeUtils.getDaysByYearMonth(year, month, calendar)
    val endTime = startTime + dayNum * 24L * 3600L * 1000L //日时分秒
    return TimeRange(startTime, endTime)
}

inline fun Date.getRealYear(calendar: Calendar = Calendar.getInstance()) = calendar.run {
    time = this@getRealYear
    getYear()
}

inline fun Date.getRealMonth(calendar: Calendar = Calendar.getInstance()) = calendar.run {
    time = this@getRealMonth
    getMonth()
}

inline fun Date.getRealDate(calendar: Calendar = Calendar.getInstance()) = calendar.run {
    time = this@getRealDate
    getDate()
}

inline fun Date.getRealTime(calendar: Calendar = Calendar.getInstance()) = calendar.run {
    time = this@getRealTime
    val hour = DateTimeUtils.getTimeFormatTen(get(Calendar.HOUR_OF_DAY))
    val minute = DateTimeUtils.getTimeFormatTen(get(Calendar.MINUTE))
    "$hour : $minute"
}

inline fun Date.isFuture() = this.time > System.currentTimeMillis()
inline fun Date.isPast() = this.time < System.currentTimeMillis()

/** ---------------Calendar获取年月日----------------*/
inline fun Calendar.getDate() = this.get(Calendar.DATE)
inline fun Calendar.setDate(date: Int) = this.set(Calendar.DATE, date)
inline fun Calendar.getMonth() = this.get(Calendar.MONTH) + 1
inline fun Calendar.setMonth(month: Int) = this.set(Calendar.MONTH, month - 1)
inline fun Calendar.getYear() = this.get(Calendar.YEAR)
inline fun Calendar.setYear(year: Int) = this.set(Calendar.YEAR, year)