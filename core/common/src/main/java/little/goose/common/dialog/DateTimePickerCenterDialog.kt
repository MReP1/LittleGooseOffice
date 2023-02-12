package little.goose.common.dialog

import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import little.goose.account.utils.*
import little.goose.common.databinding.LayoutDialogCenterTimePickerBinding
import little.goose.common.dialog.time.TimeType
import java.util.*
import little.goose.common.R

class DateTimePickerCenterDialog : DialogFragment(R.layout.layout_dialog_center_time_picker) {

    private val binding by viewBinding(LayoutDialogCenterTimePickerBinding::bind)

    private var time: Date? = null
    private var confirmAction: Function1<Date, Unit>? = null

    private var year = -1
    private var month = 1
    private var day = 1

    private var type: TimeType? = null

    private var width = UIUtils.getWidthPercentPixel(0.80F)
    private var height = ViewGroup.LayoutParams.WRAP_CONTENT

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initCenterWindow()
        initView()
    }

    private fun initView() {
        initTime()
        binding.btConfirm.setOnClickListener {
            confirmAction?.invoke(binding.timePicker.getTime())
            dismiss()
        }
    }

    private fun initTime() {
        if (time != null) {
            Calendar.getInstance().apply {
                time = this@DateTimePickerCenterDialog.time!!
                val year = getYear()
                val month = getMonth()
                val day = getDate()
                val hour = get(Calendar.HOUR_OF_DAY)
                val minute = get(Calendar.MINUTE)
                binding.timePicker.setDateTime(year, month, day, hour, minute)
            }
        }
        if (year > 0) {
            binding.timePicker.setDate(year, month, day)
        }
        type?.let {
            if (it == TimeType.YEAR || it == TimeType.MONTH || it == TimeType.DATE) {
                (binding.timePicker.layoutParams as FrameLayout.LayoutParams).rightMargin = 0
            }
            binding.timePicker.setType(it)
        }
    }

    private fun initCenterWindow() {
        dialog?.window?.apply {
            setBackgroundDrawable(null)
            attributes?.width = width
            attributes?.height = height
            attributes?.gravity = Gravity.CENTER
        }
    }

    private fun setCenterWindow(width: Int, height: Int) {
        this.width = width
        this.height = height
    }

    private fun setTime(time: Date) {
        this.time = time
    }

    private fun setTime(year: Int, month: Int, date: Int) {
        this.year = year
        this.month = month
        this.day = date
    }

    private fun setConfirmAction(block: (date: Date) -> Unit) {
        confirmAction = block
    }

    private fun setType(type: TimeType) {
        this.type = type
    }

    class Builder {
        private val dialog = DateTimePickerCenterDialog()

        fun setTime(time: Date): Builder {
            dialog.setTime(time)
            return this
        }

        fun setTime(year: Int = 2001, month: Int = 1, date: Int = 1): Builder {
            dialog.setTime(year, month, date)
            return this
        }

        fun setConfirmAction(block: (date: Date) -> Unit): Builder {
            dialog.setConfirmAction(block)
            return this
        }

        fun setType(type: TimeType): Builder {
            dialog.setType(type)
            return this
        }

        fun setCenterWindow(width: Int, height: Int): Builder {
            dialog.setCenterWindow(width, height)
            return this
        }

        fun showNow(fragmentManager: FragmentManager, tag: String = TAG) {
            dialog.showNow(fragmentManager, tag)
        }
    }

    companion object {
        private const val TAG = "pick_time"
    }
}