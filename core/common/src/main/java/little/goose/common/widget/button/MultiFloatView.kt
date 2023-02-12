package little.goose.common.widget.button

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import little.goose.common.R
import little.goose.common.databinding.LayoutMultiFloatBinding
import little.goose.common.utils.UIUtils
import little.goose.common.utils.dp
import kotlin.math.absoluteValue

class MultiFloatView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: LayoutMultiFloatBinding
    private var isAllShow = true
    private var isUpShow = true
    private var isSideShow = true

    private var touchStartX = 0F
    private var touchStartY = 0F
    private var marginRight = 0
    private var marginBottom = 0

    private var isMoving = false

    init {
        val view = inflate(context, R.layout.layout_multi_float, this)
        binding = LayoutMultiFloatBinding.bind(view)
        val attributesArray =
            context.obtainStyledAttributes(attrs, R.styleable.MultiFloatView, defStyleAttr, 0)
        val drawable = attributesArray.getDrawable(R.styleable.MultiFloatView_main_drawable_src)
        isUpShow = attributesArray.getBoolean(R.styleable.MultiFloatView_is_up_show, true)
        binding.floatUp.visibility = if (isUpShow) View.VISIBLE else View.GONE
        isSideShow = attributesArray.getBoolean(R.styleable.MultiFloatView_is_side_show, true)
        binding.floatSide.visibility = if (isSideShow) View.VISIBLE else View.GONE
        drawable?.let { binding.floatButton.setImageDra(it) }
        attributesArray.recycle()
        post { setOnDragListener() }
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun setOnDragListener() {
        val tempLayoutParams = this.layoutParams as MarginLayoutParams
        binding.floatButton.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    touchStartX = event.rawX
                    touchStartY = event.rawY
                    marginRight = tempLayoutParams.rightMargin
                    marginBottom = tempLayoutParams.bottomMargin
                }

                MotionEvent.ACTION_MOVE -> {
                    val deltaX = (event.rawX - touchStartX).toInt()
                    val deltaY = (event.rawY - touchStartY).toInt()
                    if (deltaX.absoluteValue > 12.dp() || deltaY.absoluteValue > 12.dp()) {
                        isMoving = true
                    }
                    val tempMarginEnd = marginRight - deltaX
                    val tempMarginBottom = marginBottom - deltaY
                    val maxMarginRight = UIUtils.getScreenWidth() - this.width - 16.dp()
                    val maxMarginBottom = UIUtils.getScreenHeight() - this.height - 106.dp()
                    tempLayoutParams.bottomMargin = when {
                        (tempMarginBottom < 0) ->  0
                        (tempMarginBottom > maxMarginBottom) -> maxMarginBottom
                        else -> tempMarginBottom
                    }
                    tempLayoutParams.rightMargin = when {
                        (tempMarginEnd < 0) -> 0
                        (tempMarginEnd > maxMarginRight) -> maxMarginRight
                        else -> tempMarginEnd
                    }
                    this.layoutParams = tempLayoutParams
                    return@setOnTouchListener true
                }

                MotionEvent.ACTION_UP -> {
                    if (isMoving) {
                        isMoving = false
                        event.action = MotionEvent.ACTION_CANCEL
                    }
                    recoverPosition(tempLayoutParams)
                }
            }
            return@setOnTouchListener false
        }
    }

    private fun recoverPosition(tempLayoutParams: MarginLayoutParams) {
        val decelerateInterpolator = DecelerateInterpolator()
        ValueAnimator.ofInt(tempLayoutParams.rightMargin, 0).apply {
            interpolator = decelerateInterpolator
            duration = 300
            addUpdateListener {
                tempLayoutParams.rightMargin = it.animatedValue as Int
                this@MultiFloatView.layoutParams = layoutParams
            }
        }.also { it.start() }
        ValueAnimator.ofInt(tempLayoutParams.bottomMargin, 0).apply {
            interpolator = decelerateInterpolator
            duration = 300
            addUpdateListener {
                tempLayoutParams.bottomMargin = it.animatedValue as Int
                this@MultiFloatView.layoutParams = layoutParams
            }
        }.also { it.start() }
    }

    fun showDelete() {
        this.post {
            binding.apply {
                floatButton.setImageRes(R.drawable.icon_trash)
                floatAll.visibility = if (isAllShow) View.VISIBLE else View.GONE
                floatVector.visibility = View.VISIBLE

                floatUp.visibility = View.GONE
                floatSide.visibility = View.GONE
            }
            setViewFocus(true)
        }
    }

    fun hideDelete() {
        this.post {
            binding.apply {
                floatButton.setImageRes(R.drawable.icon_add)
                floatAll.visibility = View.GONE
                floatVector.visibility = View.GONE

                floatUp.visibility = if (isUpShow) View.VISIBLE else View.GONE
                floatSide.visibility = if (isSideShow) View.VISIBLE else View.GONE
            }
            setViewFocus(false)
        }
    }

    private fun setViewFocus(flag: Boolean) {
        if (flag) {
            isFocusableInTouchMode = true
            requestFocus()
        } else {
            isFocusableInTouchMode = false
            clearFocus()
        }
    }

    inline fun setOnBackPressListener(crossinline onBackPress: () -> Unit) {
        setOnKeyListener { _, keyCode, keyEvent ->
            if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.action == KeyEvent.ACTION_UP) {
                onBackPress()
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
    }

    fun setOnFloatButtonClickListener(listener: OnClickListener) {
        binding.floatButton.setOnClickListener(listener)
    }

    fun setOnFloatAllClickListener(listener: OnClickListener) {
        binding.floatAll.setOnClickListener(listener)
    }

    fun setOnFloatVectorClickListener(listener: OnClickListener) {
        binding.floatVector.setOnClickListener(listener)
    }

    fun setOnFloatButtonLongClickListener(listener: OnLongClickListener) {
        binding.floatButton.setOnLongClickListener(listener)
    }

    fun setOnFloatSideClickListener(listener: OnClickListener) {
        binding.floatSide.setOnClickListener(listener)
    }

    fun setOnFloatUpClickListener(listener: OnClickListener) {
        binding.floatUp.setOnClickListener(listener)
    }

    fun setButtonAllVisibility(visible: Boolean) {
        isAllShow = visible
    }

    fun setButtonUpVisibility(visible: Boolean) {
        isUpShow = visible
    }

    fun setButtonSideVisibility(visible: Boolean) {
        isSideShow = visible
    }

}