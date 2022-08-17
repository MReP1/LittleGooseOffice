package little.goose.account.ui.widget.button

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import little.goose.account.utils.UIUtils
import little.goose.account.utils.dp16

object FloatViewManager {

    private var screenWidth = UIUtils.getScreenWidth()
    private var screenHeight = UIUtils.getScreenHeight()
    private var touchStartX = 0F
    private var touchStartY = 0F
    private var marginEnd = 0
    private var marginBottom = 0

    private var clickListener: View.OnClickListener? = null

    //暂时只能在ConstraintLayout使用
    @SuppressLint("ClickableViewAccessibility")
    fun setFloatView(floatView: View, onClickListener: View.OnClickListener? = null) {
        onClickListener?.let { clickListener = it }
        val gestureDetector =
            GestureDetector(floatView.context, object : GestureDetector.SimpleOnGestureListener() {
                override fun onSingleTapUp(e: MotionEvent): Boolean {
                    clickListener?.onClick(floatView)
                    return true
                }
            })

        val layoutParams = (floatView.layoutParams as ConstraintLayout.LayoutParams)
        floatView.setOnTouchListener { view, event ->
            gestureDetector.onTouchEvent(event)
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    touchStartX = event.rawX
                    touchStartY = event.rawY
                    marginEnd = layoutParams.rightMargin
                    marginBottom = layoutParams.bottomMargin
                }
                MotionEvent.ACTION_MOVE -> {
                    val deltaX = (event.rawX - touchStartX).toInt()
                    val deltaY = (event.rawY - touchStartY).toInt()
                    val tempMarginEnd = marginEnd - deltaX
                    val tempMarginBottom = marginBottom - deltaY
                    val maxMarginBottom = screenHeight - view.height
                    val maxMarginEnd = screenWidth - view.width
                    layoutParams.bottomMargin = when {
                        (tempMarginBottom < 0) -> 0
                        (tempMarginBottom > maxMarginBottom) -> maxMarginBottom
                        else -> tempMarginBottom
                    }
                    layoutParams.rightMargin = when {
                        (tempMarginEnd < 0) -> 0
                        (tempMarginEnd > maxMarginEnd) -> maxMarginEnd
                        else -> tempMarginEnd
                    }
                    view.layoutParams = layoutParams
                }
                MotionEvent.ACTION_UP -> {
                    startAnimate(floatView, layoutParams)
                }
            }
            return@setOnTouchListener true
        }
    }

    private fun startAnimate(floatView: View, layoutParams: ConstraintLayout.LayoutParams) {
        //如果在左边贴到左边去
        val targetX = if (layoutParams.rightMargin > (screenWidth - floatView.width) / 2) {
            screenWidth - floatView.width - dp16
        } else {
            dp16
        }
        ValueAnimator.ofInt(layoutParams.rightMargin, targetX).apply {
            interpolator = DecelerateInterpolator()
            duration = 300
            addUpdateListener {
                layoutParams.rightMargin = it.animatedValue as Int
                floatView.layoutParams = layoutParams
            }
        }.also { it.start() }
    }

}