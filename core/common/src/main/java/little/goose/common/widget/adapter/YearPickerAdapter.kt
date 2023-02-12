package little.goose.common.widget.adapter

import little.goose.account.utils.DateTimeUtils

class YearPickerAdapter : WheelAdapter {

    private val years = DateTimeUtils.getYearsList()

    override fun getValue(position: Int): String {
        if (position >= getMinIndex() && position <= getMaxIndex()) {
            return years[position]
        }
        return ""
    }

    override fun getPosition(value: String) = years.indexOf(value)

    override fun getTextWithMaximumLength() = "0000"

    override fun getMaxIndex() = getSize() - 1

    override fun getMinIndex() = 0

    override fun getSize() = years.size
}