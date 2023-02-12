package little.goose.common.dialog.time

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class TimeType(val type: Int): Parcelable {
    DATE_TIME(0),   //代表年月日加上时间
    DATE(1),        //代表年月日
    TIME(2),        //仅代表时间
    YEAR(3),        //年
    MONTH(4),       //月
    DAY(5),         //日
    YEAR_MONTH(6),  //年月
    MONTH_DAY(7)    //月日
}