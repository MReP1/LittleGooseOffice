package middle.goose.richtext

import android.text.Editable
import android.text.Html
import android.text.Spannable
import android.text.Spanned
import android.text.style.BulletSpan
import android.text.style.StrikethroughSpan
import org.xml.sax.XMLReader

class RichTagHandler: Html.TagHandler {
    private class Li
    private class Strike

    override fun handleTag(opening: Boolean, tag: String, output: Editable, xmlReader: XMLReader) {
        if (opening) {
            if (tag.equals(BULLET_LI, ignoreCase = true)) {
                if (output.isNotEmpty() && output[output.length - 1] != '\n') {
                    output.append("\n")
                }
                start(output, Li())
            } else if (tag.equals(STRIKETHROUGH_S, ignoreCase = true) || tag.equals(
                    STRIKETHROUGH_STRIKE, ignoreCase = true
                ) || tag.equals(STRIKETHROUGH_DEL, ignoreCase = true)
            ) {
                start(output, Strike())
            }
        } else {
            if (tag.equals(BULLET_LI, ignoreCase = true)) {
                if (output.isNotEmpty() && output[output.length - 1] != '\n') {
                    output.append("\n")
                }
                end(output, Li::class.java, BulletSpan())
            } else if (tag.equals(STRIKETHROUGH_S, ignoreCase = true) || tag.equals(
                    STRIKETHROUGH_STRIKE, ignoreCase = true
                ) || tag.equals(STRIKETHROUGH_DEL, ignoreCase = true)
            ) {
                end(output, Strike::class.java, StrikethroughSpan())
            }
        }
    }

    private fun start(output: Editable, mark: Any) {
        output.setSpan(mark, output.length, output.length, Spanned.SPAN_MARK_MARK)
    }

    private fun end(output: Editable, kind: Class<out Any?>, vararg replaces: Any) {
        val last = getLast(output, kind)
        val start = output.getSpanStart(last)
        val end = output.length
        output.removeSpan(last)
        if (start != end) {
            for (replace in replaces) {
                output.setSpan(replace, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
    }

    private fun getLast(text: Editable, kind: Class<out Any?>): Any? {
        val spans = text.getSpans(0, text.length, kind)
        return if (spans.isEmpty()) {
            null
        } else {
            for (i in spans.size downTo 1) {
                if (text.getSpanFlags(spans[i - 1]) == Spannable.SPAN_MARK_MARK) {
                    return spans[i - 1]
                }
            }
            null
        }
    }

    companion object {
        private const val BULLET_LI = "li"
        private const val STRIKETHROUGH_S = "s"
        private const val STRIKETHROUGH_STRIKE = "strike"
        private const val STRIKETHROUGH_DEL = "del"
    }
}
