package little.goose.common.converters

import androidx.room.TypeConverter
import java.math.BigDecimal
import java.util.*

class CommonTypeConverters {

    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun toDate(millisSinceEpoch: Long?): Date? {
        return millisSinceEpoch?.let { Date(it) }
    }

    @TypeConverter
    fun fromBigDecimal(bigDecimal: BigDecimal?): String? {
        return bigDecimal?.toPlainString()
    }

    @TypeConverter
    fun toBigDecimal(string: String?): BigDecimal {
        return BigDecimal(string)
    }
}