package little.goose.design.system.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.node.invalidateMeasurement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.takeOrElse
import androidx.compose.ui.unit.toOffset
import little.goose.design.system.theme.GooseTheme
import kotlin.math.max

@Composable
fun AutoResizedText(
    modifier: Modifier = Modifier,
    text: String,
    factor: Float = 0.92F,
    textMeasurer: TextMeasurer = rememberTextMeasurer(),
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    color: Color = style.color,
    textAlignment: Alignment = Alignment.CenterStart
) {
    require(factor < 1F && factor > 0F) { "Scale factor must me in 0..1" }
    val fontSize = style.fontSize.takeOrElse { MaterialTheme.typography.bodyMedium.fontSize }
    val textColor = color.takeOrElse { LocalContentColor.current }
    Spacer(
        modifier = modifier
            .resizableText(
                textMeasurer = textMeasurer,
                text = text,
                textStyle = style.merge(color = textColor, fontSize = fontSize),
                alignment = textAlignment,
                factor = factor
            )
            .fillMaxSize()
    )
}

private fun Modifier.resizableText(
    textMeasurer: TextMeasurer,
    textStyle: TextStyle,
    text: String,
    factor: Float,
    alignment: Alignment
): Modifier = this then ResizableTextModifierElement(
    textMeasurer, textStyle, text, factor, alignment
)

private class ResizableTextNode(
    private var textMeasurer: TextMeasurer,
    private var textStyle: TextStyle,
    private var text: String,
    private var factor: Float,
    private var alignment: Alignment
) : Modifier.Node(), LayoutModifierNode, DrawModifierNode {

    private var textLayoutResult: TextLayoutResult? = null
    private var topLeftOffset = Offset(0F, 0F)
    private var layoutChanged = false
    private var textChanged = false
    private var lastMaxWidth = 0

    fun updateTextMeasure(textMeasurer: TextMeasurer) {
        this.textMeasurer = textMeasurer
    }

    fun updateDraw(textStyle: TextStyle): Boolean {
        return !textStyle.hasSameDrawAffectingAttributes(this.textStyle)
    }

    fun updateText(text: String): Boolean {
        if (this.text != text) {
            this.text = text
            return true
        }
        return false
    }

    fun updateLayout(textStyle: TextStyle, alignment: Alignment, factor: Float): Boolean {
        var changed: Boolean
        changed = !textStyle.hasSameLayoutAffectingAttributes(this.textStyle)
        this.textStyle = textStyle
        if (this.alignment != alignment) {
            this.alignment = alignment
            changed = true
        }
        if (this.factor != factor) {
            this.factor = factor
            changed = true
        }
        return changed
    }

    fun doInvalidations(
        layoutChanged: Boolean,
        textChanged: Boolean,
        drawChanged: Boolean
    ) {
        if (!isAttached) {
            return
        }
        this.layoutChanged = layoutChanged
        this.textChanged = textChanged
        if (layoutChanged || textChanged) {
            invalidateMeasurement()
            invalidateDraw()
        }
        if (drawChanged) {
            invalidateDraw()
        }
    }

    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureResult {
        if (textLayoutResult == null
            || layoutChanged
            || textChanged
            || lastMaxWidth != constraints.maxWidth
        ) {
            textChanged = false
            layoutChanged = false
            lastMaxWidth = constraints.maxWidth

            var isFirstMeasure = true
            do {
                if (!isFirstMeasure) {
                    textStyle = textStyle.copy(fontSize = textStyle.fontSize * factor)
                }
                textLayoutResult = textMeasurer.measure(
                    text = text,
                    style = textStyle,
                    maxLines = 1,
                    softWrap = false,
                    constraints = constraints.copy(maxHeight = Int.MAX_VALUE)
                )
                isFirstMeasure = false
            } while (textLayoutResult?.didOverflowWidth == true)
        }
        topLeftOffset = alignment.align(
            textLayoutResult!!.size,
            IntSize(constraints.maxWidth, constraints.maxHeight),
            layoutDirection
        ).toOffset()
        val placeable = measurable.measure(constraints)
        return layout(
            max(textLayoutResult?.size?.width ?: 0, constraints.maxWidth),
            max(textLayoutResult?.size?.height ?: 0, constraints.maxHeight)
        ) {
            placeable.place(0, 0)
        }
    }

    override fun ContentDrawScope.draw() {
        textLayoutResult?.let {
            drawText(it, topLeft = topLeftOffset, color = textStyle.color)
        }
    }

}

private class ResizableTextModifierElement(
    private val textMeasurer: TextMeasurer,
    private val textStyle: TextStyle,
    private val text: String,
    private val factor: Float,
    private val alignment: Alignment
) : ModifierNodeElement<ResizableTextNode>() {

    override fun create(): ResizableTextNode {
        return ResizableTextNode(textMeasurer, textStyle, text, factor, alignment)
    }

    override fun update(node: ResizableTextNode) {
        node.updateTextMeasure(textMeasurer)
        node.doInvalidations(
            drawChanged = node.updateDraw(textStyle),
            layoutChanged = node.updateLayout(textStyle, alignment, factor),
            textChanged = node.updateText(text)
        )
    }

    @Suppress("RedundantIf")
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ResizableTextModifierElement) return false
        if (textStyle != other.textStyle) return false
        if (text != other.text) return false
        if (factor != other.factor) return false
        if (alignment != other.alignment) return false
        // TextMeasurer no need to update frequently, so we don't need to equals TextMeasurer.
        return true
    }

    override fun hashCode(): Int {
        var result = text.hashCode()
        result = 31 * result + textStyle.hashCode()
        result = 31 * result + (factor * 100).toInt()
        result = 31 * result + alignment.hashCode()
        return result
    }

    override fun InspectorInfo.inspectableProperties() {
        // Show nothing in the inspector.
    }

}

@Preview(showBackground = true)
@Composable
private fun PreviewAutoResizedTextCenter() = GooseTheme {
    PreviewAutoResizedText(alignment = Alignment.Center)
}

@Preview(showBackground = true)
@Composable
private fun PreviewAutoResizedTextTopEnd() = GooseTheme {
    PreviewAutoResizedText(alignment = Alignment.TopEnd)
}

@Preview(showBackground = true)
@Composable
private fun PreviewAutoResizedTextBottomStart() = GooseTheme {
    PreviewAutoResizedText(alignment = Alignment.BottomStart)
}

@Preview(showBackground = true)
@Composable
private fun PreviewAutoResizedTextOffset() = GooseTheme {
    PreviewAutoResizedText(
        alignment = BiasAlignment(0.6F, -0.4F)
    )
}

@Composable
private fun PreviewAutoResizedText(alignment: Alignment) {
    var count by remember { mutableIntStateOf(1) }
    val text = remember(count) { "Hello World, Hello World. ".repeat(count) }
    Box(
        modifier = Modifier
            .size(200.dp)
            .clickable { count++ }
    ) {
        AutoResizedText(text = text, textAlignment = alignment)
    }
}