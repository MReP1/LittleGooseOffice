package middle.goose.richtext

import android.content.Context
import android.graphics.Typeface
import android.text.*
import android.text.style.*
import android.util.AttributeSet
import android.view.inputmethod.InputMethodManager
import androidx.annotation.ColorRes
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import little.goose.account.richtext.R
import java.util.*

class RichEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs), TextWatcher {

    companion object {
        const val FORMAT_BOLD = 0x01
        const val FORMAT_ITALIC = 0x02
        const val FORMAT_UNDERLINED = 0x03
        const val FORMAT_STRIKETHROUGH = 0x04
        const val FORMAT_BULLET = 0x05
        const val FORMAT_QUOTE = 0x06
    }

    private var bulletColor = 0
    private var bulletRadius = 0
    private var bulletGapWidth = 0
    private var historyEnable = true
    private var historySize = 100
    private var quoteColor = 0
    private var quoteStripeWidth = 0
    private var quoteGapWidth = 0

    private val historyList = LinkedList<Editable>()
    private var historyWorking = false
    private var historyCursor = 0

    private var inputBefore: SpannableStringBuilder? = null
    private var inputLast: Editable? = null

    private var currentType = Typeface.NORMAL

    private var isUnderline = false
    private var isStrikeThrough = false

    private var startPosition = -1
    private var textCount = -1


    init {
        context.obtainStyledAttributes(attrs, R.styleable.RichEditText).apply {
            bulletColor = getColor(R.styleable.RichEditText_bulletColor, getColor(R.color.red_500))
            bulletRadius = getDimensionPixelSize(R.styleable.RichEditText_bulletRadius, 8.dp())
            bulletGapWidth = getDimensionPixelSize(R.styleable.RichEditText_bulletGapWidth, 8.dp())
            historyEnable = getBoolean(R.styleable.RichEditText_historyEnable, true)
            historySize = getInt(R.styleable.RichEditText_historySize, 50)
            quoteColor = getColor(R.styleable.RichEditText_quoteColor, getColor(R.color.blue_700))
            quoteStripeWidth =
                getDimensionPixelSize(R.styleable.RichEditText_quoteStripeWidth, 4.dp())
            quoteGapWidth = getDimensionPixelSize(R.styleable.RichEditText_quoteCapWidth, 6.dp())
        }.also { it.recycle() }

        if (historyEnable && historySize <= 0) {
            throw IllegalArgumentException("historySize must be > 0")
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        addTextChangedListener(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        removeTextChangedListener(this)
    }

    fun setCurrentNormal() {
        currentType = Typeface.NORMAL
    }

    /** ---------------------- StyleSpan ---------------------*/

    fun bold() {
        if (!contains(FORMAT_BOLD)) {
            styleValid(Typeface.BOLD, selectionStart, selectionEnd)
        } else {
            styleInvalid(Typeface.BOLD, selectionStart, selectionEnd)
        }
    }

    fun setCurrentBold() {
        currentType = if (currentType == Typeface.ITALIC) {
            Typeface.BOLD_ITALIC
        } else Typeface.BOLD
    }

    fun italic() {
        if (!contains(FORMAT_ITALIC)) {
            styleValid(Typeface.ITALIC, selectionStart, selectionEnd)
        } else {
            styleInvalid(Typeface.ITALIC, selectionStart, selectionEnd)
        }
    }

    fun setCurrentItalic() {
        currentType = if (currentType == Typeface.BOLD) {
            Typeface.BOLD_ITALIC
        } else Typeface.ITALIC
    }

    private fun styleValid(style: Int, start: Int, end: Int) {
        if (style.isBaseType() && start <= end) {
            editableText.setSpan(StyleSpan(style), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    private fun styleInvalid(style: Int, start: Int, end: Int) {
        when (style) {
            Typeface.NORMAL, Typeface.BOLD, Typeface.ITALIC, Typeface.BOLD_ITALIC -> {}
            else -> return
        }
        if (start >= end) {
            return
        }
        val spans = editableText.getSpans(start, end, StyleSpan::class.java)
        val list = ArrayList<RichPosition>()
        for (span in spans) {
            if (span.style == style) {
                list.add(
                    RichPosition(
                        editableText.getSpanStart(span),
                        editableText.getSpanEnd(span)
                    )
                )
                editableText.removeSpan(span)
            }
        }
        for (part in list) {
            if (part.isValid()) {
                if (part.start < start) {
                    styleValid(style, part.start, start)
                }
                if (part.end > end) {
                    styleValid(style, end, part.end)
                }
            }
        }
    }

    fun containStyle(style: Int, start: Int, end: Int): Boolean {
        when (style) {
            Typeface.NORMAL, Typeface.BOLD, Typeface.ITALIC, Typeface.BOLD_ITALIC -> {}
            else -> return false
        }
        if (start > end) return false

        return if (start == end) {
            if (start - 1 < 0 || start + 1 > editableText.length) {
                false
            } else {
                val before = editableText.getSpans(start - 1, start, StyleSpan::class.java)
                val after = editableText.getSpans(start, start + 1, StyleSpan::class.java)
                before.isNotEmpty() && after.isNotEmpty()
                        && before[0].style == style && after[0].style == style
            }
        } else {
            val builder = StringBuilder()

            // Make sure no duplicate characters be added
            for (i in start until end) {
                val spans = editableText.getSpans(i, i + 1, StyleSpan::class.java)
                for (span in spans) {
                    if (span.style == style) {
                        builder.append(editableText.subSequence(i, i + 1).toString())
                        break
                    }
                }
            }
            editableText.subSequence(start, end).toString() == builder.toString()
        }
    }

    /** ---------------------- UnderlineSpan ---------------------*/

    fun underline() {
        if (!contains(FORMAT_UNDERLINED)) {
            underlineValid(selectionStart, selectionEnd)
        } else {
            underlineInvalid(selectionStart, selectionEnd)
        }
    }

    fun setCurrentUnderLine(isUnderLine: Boolean) {
        this.isUnderline = isUnderLine
    }

    private fun underlineValid(start: Int, end: Int) {
        if (start >= end) return
        editableText.setSpan(UnderlineSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    private fun underlineInvalid(start: Int, end: Int) {
        if (start >= end) {
            return
        }
        val spans = editableText.getSpans(start, end, UnderlineSpan::class.java)
        val list: MutableList<RichPosition> = ArrayList<RichPosition>()
        for (span in spans) {
            list.add(RichPosition(editableText.getSpanStart(span), editableText.getSpanEnd(span)))
            editableText.removeSpan(span)
        }
        for (part in list) {
            if (part.isValid()) {
                if (part.start < start) {
                    underlineValid(part.start, start)
                }
                if (part.end > end) {
                    underlineValid(end, part.end)
                }
            }
        }
    }

    fun containUnderline(start: Int, end: Int): Boolean {
        if (start > end) return false
        return if (start == end) {
            if (start - 1 < 0 || start + 1 > editableText.length) {
                false
            } else {
                val before = editableText.getSpans(start - 1, start, UnderlineSpan::class.java)
                val after = editableText.getSpans(start, start + 1, UnderlineSpan::class.java)
                before.isNotEmpty() && after.isNotEmpty()
            }
        } else {
            val builder = StringBuilder()
            for (i in start until end) {
                if (editableText.getSpans(i, i + 1, UnderlineSpan::class.java).isNotEmpty()) {
                    builder.append(editableText.subSequence(i, i + 1).toString())
                }
            }
            editableText.subSequence(start, end).toString() == builder.toString()
        }
    }

    /** ---------------------- StrikethroughSpan ---------------------*/

    fun strikethrough() {
        if (!contains(FORMAT_STRIKETHROUGH)) {
            strikethroughValid(selectionStart, selectionEnd)
        } else {
            strikethroughInvalid(selectionStart, selectionEnd)
        }
    }

    fun setCurrentStrikeThrough(isStrikeThrough: Boolean) {
        this.isStrikeThrough = isStrikeThrough
    }

    private fun strikethroughValid(start: Int, end: Int) {
        if (start >= end) return
        editableText.setSpan(StrikethroughSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    private fun strikethroughInvalid(start: Int, end: Int) {
        if (start >= end) return
        val spans = editableText.getSpans(start, end, StrikethroughSpan::class.java)
        val list: MutableList<RichPosition> = ArrayList<RichPosition>()
        for (span in spans) {
            list.add(RichPosition(editableText.getSpanStart(span), editableText.getSpanEnd(span)))
            editableText.removeSpan(span)
        }
        for (part in list) {
            if (part.isValid()) {
                if (part.start < start) {
                    strikethroughValid(part.start, start)
                }
                if (part.end > end) {
                    strikethroughValid(end, part.end)
                }
            }
        }
    }

    fun containStrikethrough(start: Int, end: Int): Boolean {
        if (start > end) return false
        return if (start == end) {
            if (start - 1 < 0 || start + 1 > editableText.length) {
                false
            } else {
                val before = editableText.getSpans(start - 1, start, StrikethroughSpan::class.java)
                val after = editableText.getSpans(start, start + 1, StrikethroughSpan::class.java)
                before.isNotEmpty() && after.isNotEmpty()
            }
        } else {
            val builder = StringBuilder()
            for (i in start until end) {
                if (editableText.getSpans(i, i + 1, StrikethroughSpan::class.java).isNotEmpty()) {
                    builder.append(editableText.subSequence(i, i + 1).toString())
                }
            }
            editableText.subSequence(start, end).toString() == builder.toString()
        }
    }


    /** ---------------------- BulletSpan ---------------------*/

    fun bullet(valid: Boolean = !contains(FORMAT_BULLET)) {
        if (valid) {
            bulletValid()
        } else {
            bulletInvalid()
        }
    }

    private fun bulletValid() {
        val lines = TextUtils.split(editableText.toString(), "\n")
        for (i in lines.indices) {
            if (containBullet(i)) {
                continue
            }
            var lineStart = 0
            for (j in 0 until i) {
                lineStart += lines[j].length + 1 // \n
            }
            val lineEnd = lineStart + lines[i].length
            if (lineStart >= lineEnd) {
                continue
            }

            // Find selection area inside
            var bulletStart = 0
            var bulletEnd = 0
            if (lineStart <= selectionStart && selectionEnd <= lineEnd) {
                bulletStart = lineStart
                bulletEnd = lineEnd
            } else if (selectionStart <= lineStart && lineEnd <= selectionEnd) {
                bulletStart = lineStart
                bulletEnd = lineEnd
            }
            if (bulletStart < bulletEnd) {
                editableText.setSpan(
                    RichBulletSpan(bulletColor, bulletRadius, bulletGapWidth),
                    bulletStart,
                    bulletEnd,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
    }

    private fun bulletInvalid() {
        val lines = TextUtils.split(editableText.toString(), "\n")
        for (i in lines.indices) {
            if (!containBullet(i)) {
                continue
            }
            var lineStart = 0
            for (j in 0 until i) {
                lineStart += lines[j].length + 1
            }
            val lineEnd = lineStart + lines[i].length
            if (lineStart >= lineEnd) {
                continue
            }
            var bulletStart = 0
            var bulletEnd = 0
            if (lineStart <= selectionStart && selectionEnd <= lineEnd) {
                bulletStart = lineStart
                bulletEnd = lineEnd
            } else if (selectionStart <= lineStart && lineEnd <= selectionEnd) {
                bulletStart = lineStart
                bulletEnd = lineEnd
            }
            if (bulletStart < bulletEnd) {
                val spans = editableText.getSpans(
                    bulletStart, bulletEnd,
                    BulletSpan::class.java
                )
                for (span in spans) {
                    editableText.removeSpan(span)
                }
            }
        }
    }

    fun containBullet(): Boolean {
        val lines = TextUtils.split(editableText.toString(), "\n")
        val list: MutableList<Int> = ArrayList()
        for (i in lines.indices) {
            var lineStart = 0
            for (j in 0 until i) {
                lineStart += lines[j].length + 1
            }
            val lineEnd = lineStart + lines[i].length
            if (lineStart >= lineEnd) continue
            if (lineStart <= selectionStart && selectionEnd <= lineEnd) {
                list.add(i)
            } else if (selectionStart <= lineStart && lineEnd <= selectionEnd) {
                list.add(i)
            }
        }
        for (i in list) {
            if (!containBullet(i)) {
                return false
            }
        }
        return true
    }

    fun containBullet(index: Int): Boolean {
        val lines = TextUtils.split(editableText.toString(), "\n")
        if (index < 0 || index >= lines.size) return false
        var start = 0
        for (i in 0 until index) {
            start += lines[i].length + 1
        }
        val end = start + lines[index].length
        if (start >= end) return false
        val spans = editableText.getSpans(start, end, BulletSpan::class.java)
        return spans.isNotEmpty()
    }

    /** ---------------------- QuoteSpan ---------------------*/

    fun quote(valid: Boolean = !contains(FORMAT_QUOTE)) {
        if (valid) {
            quoteValid()
        } else {
            quoteInvalid()
        }
    }

    private fun quoteValid() {
        val lines = TextUtils.split(editableText.toString(), "\n")
        for (i in lines.indices) {
            if (containQuote(i)) {
                continue
            }
            var lineStart = 0
            for (j in 0 until i) {
                lineStart += lines[j].length + 1 // \n
            }
            val lineEnd = lineStart + lines[i].length
            if (lineStart >= lineEnd) {
                continue
            }
            var quoteStart = 0
            var quoteEnd = 0
            if (lineStart <= selectionStart && selectionEnd <= lineEnd) {
                quoteStart = lineStart
                quoteEnd = lineEnd
            } else if (selectionStart <= lineStart && lineEnd <= selectionEnd) {
                quoteStart = lineStart
                quoteEnd = lineEnd
            }
            if (quoteStart < quoteEnd) {
                editableText.setSpan(
                    RichQuoteSpan(quoteColor, quoteStripeWidth, quoteGapWidth),
                    quoteStart,
                    quoteEnd,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
    }

    private fun quoteInvalid() {
        val lines = TextUtils.split(editableText.toString(), "\n")
        for (i in lines.indices) {
            if (!containQuote(i)) continue
            var lineStart = 0
            for (j in 0 until i) {
                lineStart += lines[j].length + 1
            }
            val lineEnd = lineStart + lines[i].length
            if (lineStart >= lineEnd) continue
            var quoteStart = 0
            var quoteEnd = 0
            if (lineStart <= selectionStart && selectionEnd <= lineEnd) {
                quoteStart = lineStart
                quoteEnd = lineEnd
            } else if (selectionStart <= lineStart && lineEnd <= selectionEnd) {
                quoteStart = lineStart
                quoteEnd = lineEnd
            }
            if (quoteStart < quoteEnd) {
                val spans = editableText.getSpans(quoteStart, quoteEnd, QuoteSpan::class.java)
                for (span in spans) {
                    editableText.removeSpan(span)
                }
            }
        }
    }

    fun containQuote(): Boolean {
        val lines = TextUtils.split(editableText.toString(), "\n")
        val list: MutableList<Int> = ArrayList()
        for (i in lines.indices) {
            var lineStart = 0
            for (j in 0 until i) {
                lineStart += lines[j].length + 1
            }
            val lineEnd = lineStart + lines[i].length
            if (lineStart >= lineEnd) continue
            if (lineStart <= selectionStart && selectionEnd <= lineEnd) {
                list.add(i)
            } else if (selectionStart <= lineStart && lineEnd <= selectionEnd) {
                list.add(i)
            }
        }
        for (i in list) {
            if (!containQuote(i))
                return false
        }
        return true
    }

    fun containQuote(index: Int): Boolean {
        val lines = TextUtils.split(editableText.toString(), "\n")
        if (index < 0 || index >= lines.size) {
            return false
        }
        var start = 0
        for (i in 0 until index) {
            start += lines[i].length + 1
        }
        val end = start + lines[index].length
        if (start >= end) {
            return false
        }
        val spans = editableText.getSpans(start, end, QuoteSpan::class.java)
        return spans.isNotEmpty()
    }

    /** ---------------------- Redo/Undo ---------------------*/

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        if (!historyEnable || historyWorking) return
        inputBefore = SpannableStringBuilder(text)
    }

    override fun onTextChanged(
        text: CharSequence?,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        startPosition = start
        textCount = lengthAfter - lengthBefore
    }

    override fun afterTextChanged(editable: Editable?) {

        //实时设置格式
        styleValid(currentType, startPosition, startPosition + textCount)
        //下划线
        if (isUnderline) underlineValid(startPosition, startPosition + textCount)
        //划掉
        if (isStrikeThrough) strikethroughValid(startPosition, startPosition + textCount)

        if (!historyEnable || historyWorking) return

        inputLast = SpannableStringBuilder(text)

        if (text != null && text.toString() == inputBefore.toString()) return

        if (historyList.size >= historySize) {
            historyList.removeAt(0)
        }

        inputBefore?.let { historyList.add(it) }
        historyCursor = historyList.size
    }

    fun redo() {
        if (!redoValid()) return
        historyWorking = true
        if (historyCursor >= historyList.size - 1) {
            historyCursor = historyList.size
            text = inputLast
        } else {
            historyCursor++
            text = historyList[historyCursor]
        }
        setSelection(editableText.length)
        historyWorking = false
    }

    fun undo() {
        if (!undoValid()) return
        historyWorking = true
        historyCursor--
        text = historyList[historyCursor]
        setSelection(editableText.length)
        historyWorking = false
    }

    private fun redoValid(): Boolean {
        return if (!historyEnable || historySize <= 0 || historyList.size <= 0 || historyWorking) {
            false
        } else historyCursor < historyList.size - 1 || historyCursor >= historyList.size - 1 && inputLast != null
    }

    private fun undoValid(): Boolean {
        if (!historyEnable || historySize <= 0 || historyWorking) {
            return false
        }
        return !(historyList.size <= 0 || historyCursor <= 0)
    }

    fun clearHistory() {
        historyList.clear()
    }


    /** ---------------------- Helper -------------------------*/
    operator fun contains(format: Int): Boolean {
        return when (format) {
            FORMAT_BOLD -> containStyle(Typeface.BOLD, selectionStart, selectionEnd)
            FORMAT_ITALIC -> containStyle(Typeface.ITALIC, selectionStart, selectionEnd)
            FORMAT_UNDERLINED -> containUnderline(selectionStart, selectionEnd)
            FORMAT_STRIKETHROUGH -> containStrikethrough(selectionStart, selectionEnd)
            FORMAT_BULLET -> containBullet()
            FORMAT_QUOTE -> containQuote()
            else -> false
        }
    }

    fun hideSoftInput() {
        clearFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    fun showSoftInput() {
        requestFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(this, InputMethodManager.SHOW_FORCED)
    }

    fun fromHtml(source: String?) {
        val builder = SpannableStringBuilder()
        builder.append(HtmlParser.fromHtml(source))
        switchToRichStyle(builder, 0, builder.length)
        text = builder
    }

    fun toHtml(): String {
        return HtmlParser.toHtml(editableText)
    }

    fun switchToRichStyle(editable: Editable, start: Int, end: Int) {
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
                spanStart,
                spanEnd,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        val quoteSpans = editable.getSpans(
            start, end,
            QuoteSpan::class.java
        )
        for (span in quoteSpans) {
            val spanStart = editable.getSpanStart(span)
            var spanEnd = editable.getSpanEnd(span)
            spanEnd =
                if (0 < spanEnd && spanEnd < editable.length && editable[spanEnd] == '\n') spanEnd - 1 else spanEnd
            editable.removeSpan(span)
            editable.setSpan(
                RichQuoteSpan(quoteColor, quoteStripeWidth, quoteGapWidth),
                spanStart,
                spanEnd,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    private fun Int.isBaseType() = this == Typeface.NORMAL
            || this == Typeface.BOLD || this == Typeface.ITALIC
            || this == Typeface.BOLD_ITALIC

    /** ----------------------Utils-----------------------*/
    private fun getColor(@ColorRes colorRes: Int) = ContextCompat.getColor(context, colorRes)

    private fun Int.dp(): Int =
        (this * context.resources.displayMetrics.density * if (this > 0) 1 else -1).toInt()

    private fun Float.dp(): Int =
        (this * context.resources.displayMetrics.density * if (this > 0) 1 else -1).toInt()
    /** ----------------------Utils-----------------------*/
}