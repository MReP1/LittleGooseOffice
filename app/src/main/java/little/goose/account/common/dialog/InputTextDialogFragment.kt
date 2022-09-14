package little.goose.account.common.dialog

import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import little.goose.account.R
import little.goose.account.databinding.LayoutDialogInputTextBinding
import little.goose.account.utils.viewBinding
import kotlin.math.abs

class InputTextDialogFragment : BottomSheetDialogFragment() {

    private val binding by viewBinding(LayoutDialogInputTextBinding::bind)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.layout_dialog_input_text, container, false)
    }

    private var confirmCallback: Function1<String, Unit>? = null
    private var cancelCallback: Function0<Unit>? = null
    private var inputText: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initBottomWindow()
        initCallback()
        initView()
    }

    private fun initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            ViewCompat.setWindowInsetsAnimationCallback(
                binding.root,
                object : WindowInsetsAnimationCompat.Callback(DISPATCH_MODE_STOP) {

                    private var startHeight = 0
                    private var lastDiffH = 0

                    override fun onPrepare(animation: WindowInsetsAnimationCompat) {
                        if (startHeight == 0) {
                            startHeight = binding.root.height
                        }
                    }

                    override fun onProgress(
                        insets: WindowInsetsCompat,
                        runningAnimations: MutableList<WindowInsetsAnimationCompat>
                    ): WindowInsetsCompat {

                        val typesInset = insets.getInsets(WindowInsetsCompat.Type.ime())
                        val otherInset = insets.getInsets(WindowInsetsCompat.Type.systemBars())

                        val diff = Insets.subtract(typesInset, otherInset).let {
                            Insets.max(it, Insets.NONE)
                        }

                        val diffH = abs(diff.top - diff.bottom)

                        binding.etInput.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                            bottomMargin = diffH
                        }

                        if (diffH < lastDiffH) {
                            dismiss()
                            ViewCompat.setWindowInsetsAnimationCallback(binding.root, null)
                        }

                        lastDiffH = diffH

                        return insets
                    }
                }
            )
        } else {
            binding.root.viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                var lastBottom = 0
                override fun onGlobalLayout() {
                    ViewCompat.getRootWindowInsets(binding.root)?.let { insets ->
                        val bottom = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
                        if (lastBottom != 0 && bottom == 0) {
                            // 收起键盘了，可以 dismiss 了
                            dismiss()
                            binding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        }
                        lastBottom = bottom
                    }
                }
            })
        }

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

    private var bottomSheetBehavior: BottomSheetBehavior<out View>? = null

    private fun initBottomWindow() {
        dialog?.window?.apply {
            setBackgroundDrawable(null)
            setSoftInputMode(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING
                } else {
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
                }
            )
            attributes?.apply {
                height = ViewGroup.LayoutParams.WRAP_CONTENT
                width = ViewGroup.LayoutParams.MATCH_PARENT
            }
        }

        (dialog as? BottomSheetDialog)?.behavior?.apply {
            bottomSheetBehavior = this
            state = BottomSheetBehavior.STATE_EXPANDED
            addBottomSheetCallback(bottomSheetCallback)
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