package little.goose.account.ui.widget.picker.adapter

class DayPickerAdapter(var days: List<Int>) : WheelAdapter {

    override fun getValue(position: Int): String {
        if (position >= getMinIndex() && position <= getMaxIndex()) {
            return days[position].toString()
        }
        return ""
    }

    override fun getPosition(value: String) = days.indexOf(value.toIntOrNull() ?: 0)

    override fun getTextWithMaximumLength() = "00"

    override fun getMaxIndex() = getSize() - 1

    override fun getMinIndex() = 0

    override fun getSize() = days.size

}