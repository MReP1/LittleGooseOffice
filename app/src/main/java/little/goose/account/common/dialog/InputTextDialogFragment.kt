package little.goose.account.common.dialog

import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import little.goose.account.R
import little.goose.account.databinding.LayoutDialogInputTextBinding
import little.goose.account.utils.KeyBoard
import little.goose.account.utils.viewBinding

class InputTextDialogFragment : DialogFragment(R.layout.layout_dialog_input_text) {

    private val binding by viewBinding(LayoutDialogInputTextBinding::bind)
    private var confirmCallback: Function1<String, Unit>? = null
    private var cancelCallback: Function0<Unit>? = null
    private var inputText: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initBottomWindow()
        initCallback()
        initView()
    }

    private fun initView() {
        inputText?.let { binding.etInput.setText(it) }
        binding.etInput.requestFocus()
    }


    private fun setConfirmCallback(block: (String) -> Unit) {
        confirmCallback = block
    }

    private fun setCancelCallback(block: () -> Unit) {
        cancelCallback = block
    }

    private fun setInputText(text: String) {
        if (text.isNotBlank()) {
            inputText = text
        }
    }

    private fun initCallback() {
        binding.apply {
            tvConfirm.setOnClickListener {
                confirmCallback?.invoke(etInput.text.toString())
                this@InputTextDialogFragment.dismiss()
            }
            tvCancel.setOnClickListener {
                cancelCallback?.invoke()
                this@InputTextDialogFragment.dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        KeyBoard.hide(binding.etInput)
    }

    private fun initBottomWindow() {
        dialog?.window?.apply {
            setBackgroundDrawable(null)
            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
            attributes?.apply {
                gravity = Gravity.BOTTOM
                height = ViewGroup.LayoutParams.WRAP_CONTENT
                width = ViewGroup.LayoutParams.MATCH_PARENT
            }
        }
    }

    class Builder {
        private val dialog = InputTextDialogFragment()

        fun showNow(fragmentManager: FragmentManager, tag: String = "input") {
            dialog.showNow(fragmentManager, tag)
        }

        fun setConfirmCallback(block: (String) -> Unit): Builder {
            dialog.setConfirmCallback(block)
            return this
        }

        fun setCancelCallback(block: () -> Unit): Builder {
            dialog.setCancelCallback(block)
            return this
        }

        fun setInputText(text: String): Builder {
            dialog.setInputText(text)
            return this
        }
    }
}