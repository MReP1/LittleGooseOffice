package little.goose.account.ui.widget.graph

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import little.goose.account.R

class HorizontalPercentView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val contentPaint = Paint()
    private val basePaint = Paint()
    private var percent = 0.5F

    init {
        val attributesArray = context.obtainStyledAttributes(
            attrs, R.styleable.HorizontalPercentView, defStyleAttr, 0
        )
        val contentColor = attributesArray.getColor(
            R.styleable.HorizontalPercentView_contentColor,
            ContextCompat.getColor(context, R.color.cancel_button)
        )
        contentPaint.color = contentColor
        val baseColor = attributesArray.getColor(
            R.styleable.HorizontalPercentView_baseColor,
            ContextCompat.getColor(context, R.color.background_color)
        )
        basePaint.color = baseColor
        attributesArray.recycle()
    }

    fun setPercent(percent: Float?) {
        this.percent = percent ?: 0F
        invalidate()
    }

    fun setPercent(percent: Double?) {
        this.percent = percent?.toFloat() ?: 0F
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val height = height.toFloat()
        val width = width.toFloat()
        val fillWidth = width * percent
        canvas.drawRect(0F, 0F, fillWidth, height, contentPaint)
        canvas.drawRect(fillWidth, 0F, width, height, basePaint)
    }
}