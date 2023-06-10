package little.goose.common.utils

@Suppress("NOTHING_TO_INLINE")
enum class TimeType {
    DATE_TIME,   //代表年月日加上时间
    DATE,        //代表年月日
    TIME,        //仅代表时间
    YEAR,        //年
    MONTH,       //月
    DAY,         //日
    YEAR_MONTH,  //年月
    MONTH_DAY;   //月日

    inline fun containYear() =
        this == DATE_TIME || this == DATE || this == YEAR || this == YEAR_MONTH

    inline fun containMonth() =
        this == DATE_TIME || this == DATE || this == MONTH || this == YEAR_MONTH
                || this == MONTH_DAY

    inline fun containDay() =
        this == DATE_TIME || this == DATE || this == DAY || this == MONTH_DAY

    inline fun containTime() =
        this == DATE_TIME || this == TIME
}