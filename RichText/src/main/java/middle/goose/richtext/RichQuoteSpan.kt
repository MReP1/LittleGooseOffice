package middle.goose.richtext

import android.graphics.Canvas
import android.graphics.Paint
import android.os.Parcel
import android.text.Layout
import android.text.style.QuoteSpan

class RichQuoteSpan: QuoteSpan {
    private var quoteColor: Int
    private var quoteStripeWidth: Int
    private var quoteGapWidth: Int

    constructor(quoteColor: Int, quoteStripeWidth: Int, quoteGapWidth: Int) {
        this.quoteColor = if (quoteColor != 0) quoteColor else DEFAULT_COLOR
        this.quoteStripeWidth =
            if (quoteStripeWidth != 0) quoteStripeWidth else DEFAULT_STRIPE_WIDTH
        this.quoteGapWidth = if (quoteGapWidth != 0) quoteGapWidth else DEFAULT_GAP_WIDTH
    }

    constructor(src: Parcel) : super(src) {
        quoteColor = src.readInt()
        quoteStripeWidth = src.readInt()
        quoteGapWidth = src.readInt()
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        super.writeToParcel(dest, flags)
        dest.writeInt(quoteColor)
        dest.writeInt(quoteStripeWidth)
        dest.writeInt(quoteGapWidth)
    }

    override fun getLeadingMargin(first: Boolean): Int {
        return quoteStripeWidth + quoteGapWidth
    }

    override fun drawLeadingMargin(
        c: Canvas, p: Paint, x: Int, dir: Int,
        top: Int, baseline: Int, bottom: Int,
        text: CharSequence, start: Int, end: Int,
        first: Boolean, layout: Layout
    ) {
        val style = p.style
        val color = p.color
        p.style = Paint.Style.FILL
        p.color = quoteColor
        c.drawRect(
            x.toFloat(),
            top.toFloat(),
            (x + dir * quoteGapWidth).toFloat(),
            bottom.toFloat(),
            p
        )
        p.style = style
        p.color = color
    }

    companion object {
        private const val DEFAULT_STRIPE_WIDTH = 2
        private const val DEFAULT_GAP_WIDTH = 8
        private const val DEFAULT_COLOR = -0xffff01
    }
}

