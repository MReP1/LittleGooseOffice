package little.goose.account.ui.widget.text

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import little.goose.account.R
import little.goose.common.utils.DateTimeUtils
import little.goose.common.utils.getMonth
import little.goose.common.utils.getYear
import java.util.*

class MemorialTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatTextView(context, attrs) {

    private var calendar = Calendar.getInstance()
    private val curCalendar = Calendar.getInstance()

    private val headNode = TimeNode()
    private var curNode = headNode

    private var switchable = true

    init {
        //单循环
        headNode.next = headNode
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.MemorialTextView)
        switchable = typedArray.getBoolean(R.styleable.MemorialTextView_switchable, true)
        if (switchable) { setOnClickListener { switchType() } }
        typedArray.recycle()
    }


    fun setTime(time: Date) {
        clearNode()
        this.calendar.time = time
        val day = DateTimeUtils.getBetweenDay(curCalendar, calendar)
        headNode.text = day.toString() //定义第一个节点
        text = curNode.text
        if (!switchable) return //如果不可以点击就不算下面的东西了

        val curMonthDay = DateTimeUtils.getDaysByYearMonth(calendar.getYear(), calendar.getMonth())
        if (day > curMonthDay) {
            val monthDay = DateTimeUtils.getBetweenMonthDay(curCalendar, calendar)
            val monthDayStr = "${monthDay.month}个月${monthDay.day}天"
            addTimeNode(monthDayStr) //循环链表

            //如果大于12个月
            if (monthDay.month > 12) {
                if (monthDay.month % 12 != 0) {
                    addTimeNode("${monthDay.month / 12}年${monthDay.month % 12}个月${monthDay.day}天")
                } else {
                    addTimeNode("${monthDay.month / 12}年${monthDay.day}天")
                }
            }
        }
    }

    private fun clearNode() {
        while (headNode.next != headNode) {
            headNode.next = headNode.next?.next
        }
    }

    private fun addTimeNode(text: String) {
        val timeNode = TimeNode(text, headNode.next)
        headNode.next = timeNode
    }

    private fun switchType() {
        if (curNode.next != null) {
            curNode = curNode.next!!
            textSize = if (curNode.text.length > 4) {
                78F - curNode.text.length * 4.5F
            } else 78F
            text = curNode.text
        }
    }

    data class TimeNode(var text: String = "null", var next: TimeNode? = null)

}