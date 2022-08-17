package little.goose.account.ui.widget.picker.adapter

import little.goose.account.utils.DateTimeUtils

class HourPickerAdapter : WheelAdapter {

    private val hours = DateTimeUtils.getHoursList()

    override fun getValue(position: Int): String {
        if (position >= getMinIndex() && position <= getMaxIndex())
            return hours[position]
        return ""
    }

    override fun getPosition(value: String): Int = hours.indexOf(value)

    override fun getTextWithMaximumLength() = "00"

    override fun getMaxIndex(): Int = getSize() -1

    override fun getMinIndex(): Int = 0

    override fun getSize(): Int = hours.size
}