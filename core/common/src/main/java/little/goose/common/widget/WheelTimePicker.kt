package little.goose.common.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import little.goose.common.dialog.time.TimeType
import little.goose.common.utils.DateTimeUtils
import little.goose.common.R
import little.goose.common.widget.adapter.*
import java.util.*

class WheelTimePicker @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val DEFAULT_ITEM_COUNT = 3
    private var itemCount = DEFAULT_ITEM_COUNT
    private var minIndex: Int
    private var maxIndex: Int
    private var wrapSelectorWheelPreferred: Boolean

    private var selectedTextColor: Int
    private var unSelectedTextColor: Int

    private val yearPicker: WheelPicker
    private val monthPicker: WheelPicker
    private val dayPicker: WheelPicker
    private val hourPicker: WheelPicker
    private val minutePicker: WheelPicker
    private val tvYear: TextView
    private val tvMonth: TextView
    private val tvDay: TextView
    private val tvHour: TextView
    private val tvMinute:TextView

    private var year: Int = 2000
    private var month: Int = 1
    private var day: Int = 1
    private var hour: Int = 1
    private var minute: Int = 1

    private var isYearShow = true
    private var isMonthShow = true
    private var isDayShow = true
    private var isHourShow = true
    private var isMinuteShow = true

    private var dayAdapter = DayPickerAdapter(DateTimeUtils.getDaysList(2000, 1))

    //之后再拓展
    init {
        inflate(context, R.layout.layout_wheel_time_picker, this)
        yearPicker = findViewById(R.id.year_picker)
        monthPicker = findViewById(R.id.month_picker)
        dayPicker = findViewById(R.id.date_picker)
        hourPicker = findViewById(R.id.hour_picker)
        minutePicker = findViewById(R.id.minute_picker)
        tvYear = findViewById(R.id.tv_year)
        tvMonth = findViewById(R.id.tv_month)
        tvDay = findViewById(R.id.tv_day)
        tvHour = findViewById(R.id.tv_hour)
        tvMinute = findViewById(R.id.tv_minute)
        val attributesArray =
            context.obtainStyledAttributes(attrs, R.styleable.WheelTimePicker, defStyleAttr, 0)

        itemCount =
            attributesArray.getInt(R.styleable.WheelTimePicker_wheelItemCount, DEFAULT_ITEM_COUNT)
        if (itemCount != DEFAULT_ITEM_COUNT) setItemCount(itemCount)

        minIndex = attributesArray.getInt(R.styleable.WheelTimePicker_min, Integer.MIN_VALUE)
        maxIndex = attributesArray.getInt(R.styleable.WheelTimePicker_max, Integer.MAX_VALUE)
        if (minIndex != Integer.MIN_VALUE) setMin(minIndex)
        if (maxIndex != Integer.MAX_VALUE) setMax(maxIndex)

        wrapSelectorWheelPreferred =
            attributesArray.getBoolean(R.styleable.WheelTimePicker_wrapSelectorWheel, false)
        if (wrapSelectorWheelPreferred) {
            setWrapSelectorWheel(wrapSelectorWheelPreferred)
        }

        val defaultSelectedTextColor = ContextCompat.getColor(context, R.color.nor_text_color)
        selectedTextColor = attributesArray.getColor(
            R.styleable.WheelTimePicker_selectedTextColor, defaultSelectedTextColor
        )
        if (selectedTextColor != defaultSelectedTextColor) {
            setSelectedTextColor(selectedTextColor)
        }

        val defaultUnSelectedTextColor = ContextCompat.getColor(context, R.color.hint_text_color)
        unSelectedTextColor =
            attributesArray.getColor(R.styleable.WheelTimePicker_textColor, defaultUnSelectedTextColor)
        if (unSelectedTextColor != defaultUnSelectedTextColor) {
            setUnSelectedTextColor(unSelectedTextColor)
        }
        //textSize
        val defaultType = attributesArray.getInt(R.styleable.WheelTimePicker_type, 0)
        setType(defaultType)

        attributesArray.recycle()

        initAdapter()
        setTimeListener()
    }

    fun setMin(min: Int) {
        yearPicker.setMin(min)
        monthPicker.setMin(min)
        dayPicker.setMin(min)
        hourPicker.setMin(min)
        minutePicker.setMin(min)
    }

    fun setMax(max: Int) {
        yearPicker.setMax(max)
        monthPicker.setMax(max)
        dayPicker.setMax(max)
        hourPicker.setMax(max)
        minutePicker.setMax(max)
    }

    fun setItemCount(itemCount: Int) {
        yearPicker.setWheelItemCount(itemCount)
        monthPicker.setWheelItemCount(itemCount)
        dayPicker.setWheelItemCount(itemCount)
        hourPicker.setWheelItemCount(itemCount)
        minutePicker.setWheelItemCount(itemCount)
    }

    fun setWrapSelectorWheel(wrap: Boolean) {
        yearPicker.setSelectorRoundedWrapPreferred(wrap)
        monthPicker.setSelectorRoundedWrapPreferred(wrap)
        dayPicker.setSelectorRoundedWrapPreferred(wrap)
        hourPicker.setSelectorRoundedWrapPreferred(wrap)
        minutePicker.setSelectorRoundedWrapPreferred(wrap)
    }

    fun setSelectedTextColor(color: Int) {
        yearPicker.setSelectedTextColor(color)
        monthPicker.setSelectedTextColor(color)
        dayPicker.setSelectedTextColor(color)
        hourPicker.setSelectedTextColor(color)
        minutePicker.setSelectedTextColor(color)
    }

    fun setUnSelectedTextColor(color: Int) {
        yearPicker.setUnselectedTextColor(color)
        monthPicker.setUnselectedTextColor(color)
        dayPicker.setUnselectedTextColor(color)
        hourPicker.setUnselectedTextColor(color)
        minutePicker.setUnselectedTextColor(color)
    }

    private fun initAdapter() {
        yearPicker.setAdapter(YearPickerAdapter())
        monthPicker.setAdapter(MonthPickerAdapter())
        dayPicker.setAdapter(dayAdapter)
        hourPicker.setAdapter(HourPickerAdapter())
        minutePicker.setAdapter(MinutePickerAdapter())
    }

    private fun updateDayPickerAdapter() {
        setDayPickerAdapter(
            DayPickerAdapter(DateTimeUtils.getDaysList(year, month))
        )
    }

    private fun setDayPickerAdapter(adapter: DayPickerAdapter) {
        if (adapter != dayAdapter) {
            dayAdapter = adapter
            val size = dayAdapter.getSize()
            if (dayPicker.getCurrentItem().toInt() > size) {
                dayPicker.scrollTo(dayAdapter.getMaxIndex())
                day = size
            }
            dayPicker.setAdapter(dayAdapter)
        }
    }

    fun setDateTime(year: Int, month: Int, day: Int, hour: Int, minute: Int) {
        setDate(year, month, day)
        setTime(hour, minute)
    }

    fun setDate(year: Int, month: Int, day: Int) {
        yearPicker.scrollToValue(year.toString())
        this.year = year
        monthPicker.scrollToValue(month.toString())
        this.month = month
        updateDayPickerAdapter()
        dayPicker.scrollToValue(day.toString())
        this.day = day
    }

    fun setTime(hour: Int, minute: Int) {
        hourPicker.scrollToValue(hour.toString())
        this.hour = hour
        minutePicker.scrollToValue(minute.toString())
        this.minute = minute
    }

    private fun setTimeListener() {
        if (isYearShow) {
            yearPicker.setOnValueChangeListener(object : OnValueChangeListener {
                override fun onValueChange(picker: WheelPicker, oldVal: String, newVal: String) {
                    year = newVal.toInt()
                    updateDayPickerAdapter()
                }
            })
        }
        if (isMonthShow) {
            monthPicker.setOnValueChangeListener(object : OnValueChangeListener {
                override fun onValueChange(picker: WheelPicker, oldVal: String, newVal: String) {
                    month = newVal.toInt()
                    updateDayPickerAdapter()
                }
            })
        }
        if (isDayShow) {
            dayPicker.setOnValueChangeListener(object : OnValueChangeListener {
                override fun onValueChange(picker: WheelPicker, oldVal: String, newVal: String) {
                    day = newVal.toInt()
                }
            })
        }
        if (isHourShow) {
            hourPicker.setOnValueChangeListener(object : OnValueChangeListener {
                override fun onValueChange(picker: WheelPicker, oldVal: String, newVal: String) {
                    hour = newVal.toInt()
                }
            })
        }
        if (isMinuteShow) {
            minutePicker.setOnValueChangeListener(object : OnValueChangeListener {
                override fun onValueChange(picker: WheelPicker, oldVal: String, newVal: String) {
                    minute = newVal.toInt()
                }
            })
        }
    }

    fun getTime(): Date = Calendar.getInstance().run {
        set(year, month - 1, day, hour, minute)
        time
    }

    fun setType(type: TimeType) {
        setType(type.type)
    }

    private fun setType(type: Int) {
        when (type) {
            DATETIME -> {
                yearPicker.isVisible = true
                tvYear.isVisible = true
                minutePicker.isVisible = true
                tvMonth.isVisible = true
                dayPicker.isVisible = true
                tvDay.isVisible = true
                monthPicker.isVisible = true
                tvMonth.isVisible = true
                dayPicker.isVisible = true
                tvDay.isVisible = true
            }
            DATE -> {
                hourPicker.isVisible = false
                tvHour.isVisible = false
                isHourShow = false
                minutePicker.isVisible = false
                tvMinute.isVisible = false
                isMinuteShow = false
                yearPicker.isVisible = true
                tvYear.isVisible = true
                monthPicker.isVisible = true
                tvMonth.isVisible = true
                dayPicker.isVisible = true
                tvDay.isVisible = true
            }
            TIME -> {
                yearPicker.isVisible = false
                tvYear.isVisible = false
                isYearShow = false
                monthPicker.isVisible = false
                tvMonth.isVisible = false
                isMonthShow = false
                dayPicker.isVisible = false
                tvDay.isVisible = false
                isDayShow = false
                hourPicker.isVisible = true
                tvHour.isVisible = true
                minutePicker.isVisible = true
                tvMinute.isVisible = true
            }
            YEAR -> {
                yearPicker.isVisible = true
                tvYear.isVisible = false
                monthPicker.isVisible = false
                tvMonth.isVisible = false
                isMonthShow = false
                dayPicker.isVisible = false
                tvDay.isVisible = false
                isDayShow = false
                hourPicker.isVisible = false
                tvHour.isVisible = false
                isHourShow = false
                minutePicker.isVisible = false
                tvMinute.isVisible = false
                isMinuteShow = false
            }
            MONTH -> {
                yearPicker.isVisible = false
                tvYear.isVisible = false
                isYearShow = false
                monthPicker.isVisible = true
                tvMonth.isVisible = true
                dayPicker.isVisible = false
                tvDay.isVisible = false
                isDayShow = false
                hourPicker.isVisible = false
                tvHour.isVisible = false
                isHourShow = false
                minutePicker.isVisible = false
                tvMinute.isVisible = false
                isMinuteShow = false
            }
            DAY -> {
                yearPicker.isVisible = false
                tvYear.isVisible = false
                isYearShow = false
                monthPicker.isVisible = false
                tvMonth.isVisible = false
                isMonthShow = false
                dayPicker.isVisible = true
                tvDay.isVisible = true
                hourPicker.isVisible = false
                tvHour.isVisible = false
                isHourShow = false
                minutePicker.isVisible = false
                tvMinute.isVisible = false
                isMinuteShow = false
            }
            YEAR_MONTH -> {
                yearPicker.isVisible = true
                tvYear.isVisible = true
                monthPicker.isVisible = true
                tvMonth.isVisible = true
                dayPicker.isVisible = false
                tvDay.isVisible = false
                isDayShow = false
                hourPicker.isVisible = false
                tvHour.isVisible = false
                isHourShow = false
                minutePicker.isVisible = false
                tvMinute.isVisible = false
                isMinuteShow = false
            }
        }
    }

    companion object {
        const val DATETIME = 0
        const val DATE = 1
        const val TIME = 2
        const val YEAR = 3
        const val MONTH = 4
        const val DAY = 5
        const val YEAR_MONTH = 6
    }
}