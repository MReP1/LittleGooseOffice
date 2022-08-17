package middle.goose.richtext

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.os.Parcel
import android.text.Layout
import android.text.Spanned
import android.text.style.BulletSpan

class RichBulletSpan : BulletSpan {

    private var bulletPath: Path? = null

    private var bulletColor = DEFAULT_COLOR
    private var radius = DEFAULT_RADIUS
    private var bulletGapWidth = DEFAULT_GAP_WIDTH

    constructor(bulletColor: Int, bulletRadius: Int, bulletGapWidth: Int) {
        this.bulletColor = if (bulletColor != 0) bulletColor else DEFAULT_COLOR
        this.radius = if (bulletRadius != 0) bulletRadius else DEFAULT_RADIUS
        this.bulletGapWidth = if (bulletGapWidth != 0) bulletGapWidth else DEFAULT_GAP_WIDTH
    }

    constructor(src: Parcel) : super(src) {
        bulletColor = src.readInt()
        radius = src.readInt()
        bulletGapWidth = src.readInt()
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        super.writeToParcel(dest, flags)
        dest.writeInt(bulletColor)
        dest.writeInt(radius)
        dest.writeInt(bulletGapWidth)
    }

    override fun getLeadingMargin(first: Boolean): Int {
        return 2 * radius + bulletGapWidth
    }

    override fun drawLeadingMargin(
        c: Canvas, p: Paint, x: Int, dir: Int,
        top: Int, baseline: Int, bottom: Int,
        text: CharSequence, start: Int, end: Int,
        first: Boolean, l: Layout?
    ) {
        if ((text as Spanned).getSpanStart(this) == start) {
            val style = p.style
            val oldColor = p.color
            p.color = bulletColor
            p.style = Paint.Style.FILL
            if (c.isHardwareAccelerated) {
                if (bulletPath == null) {
                    bulletPath = Path()
                    // Bullet is slightly better to avoid aliasing artifacts on mdpi devices.
                    bulletPath!!.addCircle(0.0f, 0.0f, radius.toFloat(), Path.Direction.CW)
                }
                c.save()
                c.translate((x + dir * radius).toFloat(), (top + bottom) / 2.0f)
                c.drawPath(bulletPath!!, p)
                c.restore()
            } else {
                c.drawCircle(
                    (x + dir * radius).toFloat(),
                    (top + bottom) / 2.0f,
                    radius.toFloat(),
                    p
                )
            }
            p.color = oldColor
            p.style = style
        }
    }

    companion object {
        private const val DEFAULT_COLOR = 0
        private const val DEFAULT_RADIUS = 6
        private const val DEFAULT_GAP_WIDTH = 2
    }
}