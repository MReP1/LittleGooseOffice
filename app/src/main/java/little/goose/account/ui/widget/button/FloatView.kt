package little.goose.account.ui.widget.button

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import com.google.android.material.card.MaterialCardView
import little.goose.account.R
import little.goose.account.databinding.ItemFlowButtonBinding
import little.goose.account.utils.viewBinding

class FloatView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : MaterialCardView(context, attrs, defStyleAttr) {

    private val binding by viewBinding(R.layout.item_flow_button, ItemFlowButtonBinding::bind)

    init {
        val attributesArray =
            context.obtainStyledAttributes(attrs, R.styleable.FloatView, defStyleAttr, 0)
        val drawable = attributesArray.getDrawable(R.styleable.FloatView_drawable_src)
        binding.ivContent.setImageDrawable(drawable)
        attributesArray.recycle()
    }

    fun setImageRes(resource: Int) {
        binding.ivContent.setImageResource(resource)
    }

    fun setImageDra(drawable: Drawable) {
        binding.ivContent.setImageDrawable(drawable)
    }

}