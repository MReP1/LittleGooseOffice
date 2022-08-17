package little.goose.account.ui.widget.picker.adapter

import little.goose.account.utils.DateTimeUtils

class MinutePickerAdapter : WheelAdapter {

    private val minutes = DateTimeUtils.getMinuteList()

    override fun getValue(position: Int): String {
        if (position >= getMinIndex() && position <= getMaxIndex()) {
            return minutes[position]
        }
        return ""
    }

    override fun getPosition(value: String) = minutes.indexOf(value)

    override fun getTextWithMaximumLength() = "00"

    override fun getMaxIndex() = getSize() - 1

    override fun getMinIndex() = 0

    override fun getSize(): Int = minutes.size
}