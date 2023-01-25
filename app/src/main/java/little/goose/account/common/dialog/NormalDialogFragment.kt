package little.goose.account.common.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import little.goose.account.R
import little.goose.account.databinding.LayoutDialogNormalBinding
import little.goose.account.utils.UIUtils
import little.goose.account.utils.viewBinding

class NormalDialogFragment
private constructor() : DialogFragment(R.layout.layout_dialog_normal) {

    private val binding by viewBinding(LayoutDialogNormalBinding::bind)
    private var content = ""
    private var confirmFunction: Function1<View, Any>? = null
    private var cancelFunction: Function0<Any>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initWindow()
        initView()
    }

    private fun initView() {
        binding.apply {
            tvDialogContent.text = content
            confirmButton.setOnClickListener {
                confirmFunction?.invoke(it)
                dismiss()
            }
            cancelButton.setOnClickListener {
                cancelFunction?.invoke()
                dismiss()
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
    }

    private fun initWindow() {
        dialog?.window?.setBackgroundDrawable(null)
        dialog?.window?.attributes?.apply {
            width = UIUtils.getWidthPercentPixel(0.76F)
            height = ViewGroup.LayoutParams.WRAP_CONTENT
            gravity = Gravity.CENTER
        }
    }

    fun setScreenPercent(widthPercent: Float, heightPercent: Float) {
        dialog?.window?.attributes?.apply {
            width = UIUtils.getWidthPercentPixel(widthPercent)
            height = UIUtils.getWidthPercentPixel(heightPercent)
        }
    }

    fun setContent(msg: String) {
        content = msg
    }

    fun setConfirmCallback(block: (View) -> Unit) {
        confirmFunction = block
    }

    fun setCancelCallback(block: () -> Unit) {
        cancelFunction = block
    }

    class Builder {
        private val dialog = NormalDialogFragment()

        fun setContent(msg: String): Builder {
            dialog.setContent(msg)
            return this
        }

        fun setConfirmCallback(block: (View) -> Unit): Builder {
            dialog.setConfirmCallback(block)
            return this
        }

        fun setCancelCallback(block: () -> Unit): Builder {
            dialog.setCancelCallback(block)
            return this
        }

        fun setScreenPercent(widthPercent: Float, heightPercent: Float): Builder {
            dialog.setScreenPercent(widthPercent, heightPercent)
            return this
        }

        fun showNow(fragmentManager: FragmentManager, tag: String = "dialog") {
            dialog.showNow(fragmentManager, tag)
        }
    }
}