package little.goose.office.ui.home.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.text.TextPaint
import androidx.core.content.ContextCompat
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.WeekView
import little.goose.account.R
import little.goose.office.logic.data.constant.ACCOUNT
import little.goose.office.logic.data.constant.MEMORIAL
import little.goose.office.logic.data.constant.SCHEDULE
import little.goose.common.utils.dpf

class AccountWeekView(context: Context?) : WeekView(context) {

    //今天背景色
    private val currentDayPaint = Paint()

    private var padding = 0.dpf()

    //背景圆点
    private val pointPaint = Paint()

    private val moneyPaint = TextPaint()

    //圆点半径
    private var pointRadius = 2.dpf()

    private val moneyBottomPadding = 8.dpf()
    private val pointTopPadding = 13.dpf()

    init {
        currentDayPaint.apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            color = ContextCompat.getColor(getContext(), little.goose.common.R.color.item_selected)
        }
        pointPaint.apply {
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
            color = ContextCompat.getColor(getContext(), little.goose.common.R.color.white)
        }
        moneyPaint.apply {
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
            color = ContextCompat.getColor(getContext(), little.goose.common.R.color.add_button)
            textSize = 10.dpf()
            typeface = Typeface.defaultFromStyle(Typeface.BOLD)
        }
    }

    override fun onDrawSelected(
        canvas: Canvas, calendar: Calendar, x: Int, hasScheme: Boolean
    ): Boolean {
        mSelectTextPaint.style = Paint.Style.FILL
        canvas.drawRect(
            x + padding,
            padding,
            x + mItemWidth - padding,
            mItemHeight - padding,
            mSelectedPaint
        )
        return true
    }

    override fun onDrawScheme(canvas: Canvas, calendar: Calendar, x: Int) {
        for (scheme in calendar.schemes) {
            when (scheme.type) {
                ACCOUNT -> {
                    moneyPaint.color = scheme.shcemeColor
                    canvas.drawText(
                        scheme.scheme,
                        (x + mItemWidth / 2).toFloat(),
                        y + mItemHeight - moneyBottomPadding,
                        moneyPaint
                    )
                }
                SCHEDULE, MEMORIAL -> {
                    pointPaint.color = scheme.shcemeColor
                    canvas.drawCircle(
                        (x + mItemWidth / 2).toFloat(),
                        y + pointTopPadding,
                        pointRadius,
                        pointPaint
                    )
                }
            }
        }
    }

    override fun onDrawText(
        canvas: Canvas, calendar: Calendar, x: Int, hasScheme: Boolean, isSelected: Boolean
    ) {
        val textX = (x + mItemWidth / 2).toFloat()
        val textY = mTextBaseLine
        val isInRange = isInRange(calendar)
        when {
            isSelected -> {
                canvas.drawText(
                    calendar.day.toString(), textX, textY,
                    if (calendar.isCurrentDay) mCurDayTextPaint else mSelectTextPaint
                )
            }
            hasScheme -> {
                canvas.drawText(
                    calendar.day.toString(), textX, textY,
                    if (calendar.isCurrentMonth && isInRange) {
                        if (calendar.isCurrentDay) {
                            mCurDayTextPaint
                        } else {
                            mSchemeTextPaint
                        }
                    } else {
                        mOtherMonthTextPaint
                    }
                )
            }
            else -> {
                canvas.drawText(
                    calendar.day.toString(), textX, textY,
                    if (calendar.isCurrentDay) mCurDayTextPaint else if (calendar.isCurrentMonth && isInRange) mCurMonthTextPaint else mOtherMonthTextPaint
                )
            }
        }
    }
}