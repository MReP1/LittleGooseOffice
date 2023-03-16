package little.goose.common.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import little.goose.common.databinding.LayoutDialogBottomTimePickerBinding
import little.goose.common.dialog.time.TimeType
import little.goose.common.utils.getDate
import little.goose.common.utils.getMonth
import little.goose.common.utils.getYear
import java.util.*

class DateTimePickerBottomDialog : BottomSheetDialogFragment() {

    private lateinit var bottomBinding: LayoutDialogBottomTimePickerBinding

    private var time: Date? = null
    private var bottomSheetBehavior: BottomSheetBehavior<View>? = null
    private var confirmAction: Function1<Date, Unit>? = null

    private var year = -1
    private var month = 1
    private var day = 1

    private var type: TimeType? = null

    private var isDimShow = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        bottomBinding = LayoutDialogBottomTimePickerBinding.inflate(inflater, container, false)
        return bottomBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initBottomWindow()
        disableDrag()
    }

    private fun initView() {
        initTime()
        bottomBinding.btConfirm.setOnClickListener {
            confirmAction?.invoke(bottomBinding.timePicker.getTime())
            dismiss()
        }
    }

    private fun initTime() {
        if (time != null) {
            Calendar.getInstance().apply {
                time = this@DateTimePickerBottomDialog.time!!
                val year = getYear()
                val month = getMonth()
                val day = getDate()
                val hour = get(Calendar.HOUR_OF_DAY)
                val minute = get(Calendar.MINUTE)
                bottomBinding.timePicker.setDateTime(year, month, day, hour, minute)
            }
        }
        if (year > 0) {
            bottomBinding.timePicker.setDate(year, month, day)
        }
        type?.let {
            if (it == TimeType.YEAR || it == TimeType.MONTH) {
                (bottomBinding.timePicker.layoutParams as ConstraintLayout.LayoutParams).apply {
                    rightMargin = 0
                }
            } else if (it == TimeType.YEAR_MONTH) {
                (bottomBinding.timePicker.layoutParams as ConstraintLayout.LayoutParams).apply {
                    width = (resources.displayMetrics.widthPixels * 0.52F).toInt()
                    rightMargin = 0
                }
            }
            bottomBinding.timePicker.setType(it)
        }
    }

    private fun disableDrag() {
        view?.post {
            val parent = view?.parent as View
            val params = parent.layoutParams as CoordinatorLayout.LayoutParams
            val behavior = params.behavior
            bottomSheetBehavior = behavior as BottomSheetBehavior<View>?
            bottomSheetBehavior?.addBottomSheetCallback(bottomSheetCallback)
        }
    }

    private val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                //判断为向下拖动行为时，则强制设定状态为展开
                bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }

    private fun initBottomWindow() {
        dialog?.window?.apply {
            setBackgroundDrawable(null)
            if (!isDimShow) {
                clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            }
        }
    }

    private fun setTime(time: Date) {
        this.time = time
    }

    private fun setTime(year: Int, month: Int, day: Int) {
        this.year = year
        this.month = month
        this.day = day
    }

    private fun setType(type: TimeType) {
        this.type = type
    }

    private fun setConfirmAction(block: (date: Date) -> Unit) {
        confirmAction = block
    }

    private fun setDimVisibility(flag: Boolean) {
        isDimShow = flag
    }

    companion object {
        private const val TAG = "pick_time"
    }

    class Builder {
        private val dialog = DateTimePickerBottomDialog()

        fun setTime(time: Date?): Builder {
            time?.let { dialog.setTime(it) }
            return this
        }

        fun setTime(year: Int = 2001, month: Int = 1, day: Int = 1): Builder {
            dialog.setTime(year, month, day)
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

        fun setDimVisibility(flag: Boolean): Builder {
            dialog.setDimVisibility(flag)
            return this
        }

        fun showNow(fragmentManager: FragmentManager, tag: String = TAG) {
            dialog.showNow(fragmentManager, tag)
        }

    }
}