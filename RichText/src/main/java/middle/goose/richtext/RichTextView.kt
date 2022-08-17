package middle.goose.richtext

import android.content.Context
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.BulletSpan
import android.text.style.QuoteSpan
import android.util.AttributeSet
import androidx.annotation.ColorRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat

class RichTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatTextView(context, attrs) {

    private var bulletColor = 0
    private var bulletRadius = 0
    private var bulletGapWidth = 0
    private var historyEnable = true
    private var historySize = 100
    private var quoteColor = 0
    private var quoteStripeWidth = 0
    private var quoteGapWidth = 0

    init {
        context.obtainStyledAttributes(attrs, R.styleable.RichTextView).apply {
            bulletColor = getColor(R.styleable.RichTextView_bulletColor, getColor(R.color.red_500))
            bulletRadius = getDimensionPixelSize(R.styleable.RichTextView_bulletRadius, 2.5f.dp())
            bulletGapWidth = getDimensionPixelSize(R.styleable.RichTextView_bulletGapWidth, 6.dp())
            historyEnable = getBoolean(R.styleable.RichTextView_historyEnable, true)
            historySize = getInt(R.styleable.RichTextView_historySize, 50)
            quoteColor = getColor(R.styleable.RichTextView_quoteColor, getColor(R.color.blue_700))
            quoteStripeWidth =
                getDimensionPixelSize(R.styleable.RichTextView_quoteStripeWidth, 6.dp())
            quoteGapWidth = getDimensionPixelSize(R.styleable.RichTextView_quoteCapWidth, 2.5f.dp())
        }.also { it.recycle() }
    }

    private fun switchToRichStyle(editable: Editable, start: Int, end: Int) {
        val bulletSpans = editable.getSpans(
            start, end,
            BulletSpan::class.java
        )
        for (span in bulletSpans) {
            val spanStart = editable.getSpanStart(span)
            var spanEnd = editable.getSpanEnd(span)
            spanEnd =
                if (0 < spanEnd && spanEnd < editable.length && editable[spanEnd] == '\n') spanEnd - 1 else spanEnd
            editable.removeSpan(span)
            editable.setSpan(
                RichBulletSpan(bulletColor, bulletRadius, bulletGapWidth),
                spanStart, spanEnd,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        val quoteSpans = editable.getSpans(start, end, QuoteSpan::class.java)
        for (span in quoteSpans) {
            val spanStart = editable.getSpanStart(span)
            var spanEnd = editable.getSpanEnd(span)
            spanEnd = if (0 < spanEnd && spanEnd < editable.length && editable[spanEnd] == '\n')
                spanEnd - 1
            else
                spanEnd
            editable.removeSpan(span)
            editable.setSpan(
                RichQuoteSpan(quoteColor, quoteStripeWidth, quoteGapWidth),
                spanStart,
                spanEnd,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    fun fromHtml(source: String?) {
        val builder = SpannableStringBuilder()
        builder.append(HtmlParser.fromHtml(source))
        switchToRichStyle(builder, 0, builder.length)
        text = builder
    }

    /** ----------------------Utils-----------------------*/
    private fun getColor(@ColorRes colorRes: Int) = ContextCompat.getColor(context, colorRes)
    private fun Int.dp(): Int =
        (this * context.resources.displayMetrics.density * if (this > 0) 1 else -1).toInt()
    private fun Float.dp(): Int =
        (this * context.resources.displayMetrics.density * if (this > 0) 1 else -1).toInt()
    /** ----------------------Utils-----------------------*/
}