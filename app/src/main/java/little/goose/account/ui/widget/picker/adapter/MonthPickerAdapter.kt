package little.goose.account.ui.widget.picker.adapter

import little.goose.account.utils.DateTimeUtils

class MonthPickerAdapter : WheelAdapter {

    private val months = DateTimeUtils.getMonthsList()

    override fun getValue(position: Int): String {
        if (position >= getMinIndex() && position <= getMaxIndex())
            return months[position]
        return ""
    }

    override fun getPosition(value: String) = months.indexOf(value)

    override fun getTextWithMaximumLength() = "00"

    override fun getMaxIndex() = getSize() - 1

    override fun getMinIndex() = 0

    override fun getSize() = months.size
}