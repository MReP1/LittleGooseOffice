package little.goose.design.system.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isUnspecified
import androidx.compose.ui.unit.toOffset
import little.goose.design.system.theme.GooseTheme

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
    val defaultFontSize = MaterialTheme.typography.bodyMedium.fontSize
    val layoutDirection = LocalLayoutDirection.current
    Spacer(
        modifier = modifier
            .fillMaxSize()
            .drawWithCache {
                var fontSize = if (style.fontSize.isUnspecified) defaultFontSize else style.fontSize
                var textLayoutResult: TextLayoutResult
                var isFirstMeasure = true
                do {
                    if (!isFirstMeasure) {
                        fontSize *= factor
                    }
                    textLayoutResult = textMeasurer.measure(
                        text = text,
                        style = style.copy(fontSize = fontSize),
                        maxLines = 1,
                        softWrap = false,
                        constraints = Constraints(maxWidth = size.width.toInt())
                    )
                    isFirstMeasure = false
                } while (textLayoutResult.didOverflowWidth)

                val offset = textAlignment.align(
                    textLayoutResult.size,
                    IntSize(size.width.toInt(), size.height.toInt()),
                    layoutDirection
                )

                onDrawWithContent {
                    drawText(
                        textLayoutResult,
                        color = color,
                        topLeft = offset.toOffset()
                    )
                }
            }
    )
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