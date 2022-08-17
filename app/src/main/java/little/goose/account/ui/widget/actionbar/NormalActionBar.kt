package little.goose.account.ui.widget.actionbar

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import little.goose.account.R
import little.goose.account.databinding.LayoutNormalActionBarBinding

class NormalActionBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private var binding: LayoutNormalActionBarBinding

    init {
        val view = inflate(context, R.layout.layout_normal_action_bar, this)
        binding = LayoutNormalActionBarBinding.bind(view)
        val attributesArray = context.obtainStyledAttributes(attrs, R.styleable.NormalActionBar)
        val textId = attributesArray.getResourceId(R.styleable.NormalActionBar_titleText, 0)
        binding.tvTitle.text = if (textId != 0) {
            resources.getText(textId)
        } else {
            attributesArray.getText(R.styleable.NormalActionBar_titleText)
        }
        attributesArray.recycle()
    }

    fun setOnBackClickListener(listener: OnClickListener) {
        binding.ivBack.setOnClickListener(listener)
    }

    fun setTitle(title: String) {
        binding.tvTitle.text = title
    }

}