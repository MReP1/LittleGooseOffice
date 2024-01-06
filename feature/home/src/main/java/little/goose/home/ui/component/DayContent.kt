package little.goose.home.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import little.goose.design.system.theme.RoundedCorner12

@Composable
fun DayContent(
    onClick: () -> Unit,
    modifier: Modifier,
    textMeasurer: TextMeasurer,
    dateText: String,
    money: String?,
    isToday: Boolean,
    isCurrentDay: Boolean,
    isCurrentMonth: Boolean,
    drawPoint: Boolean
) {
    val typography = MaterialTheme.typography
    val colorScheme = MaterialTheme.colorScheme
    Spacer(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCorner12)
            .clickable(onClick = onClick)
            .drawWithCache {
                val dateTextLayoutResult = textMeasurer.measure(
                    text = dateText,
                    style = typography.titleSmall,
                    overflow = TextOverflow.Clip,
                    softWrap = false,
                    maxLines = 1,
                    constraints = Constraints(maxWidth = size.width.toInt())
                )
                val moneyTextLayoutResult = if (money != null) {
                    textMeasurer.measure(
                        text = money,
                        style = typography.bodySmall,
                        overflow = TextOverflow.Ellipsis,
                        softWrap = false,
                        maxLines = 1,
                        constraints = Constraints(maxWidth = size.width.toInt())
                    )
                } else null
                val dateTopLeft = Offset(
                    (size.width - dateTextLayoutResult.size.width) / 2,
                    (size.height - dateTextLayoutResult.size.height) / 2
                )
                onDrawWithContent {
                    if (isCurrentDay) {
                        drawRoundRect(
                            color = colorScheme.primary,
                            cornerRadius = CornerRadius(12.dp.toPx(), 12.dp.toPx())
                        )
                    }
                    if (isToday) {
                        drawRoundRect(
                            color = colorScheme.tertiaryContainer,
                            topLeft = Offset(
                                dateTopLeft.x - 6.dp.toPx(),
                                dateTopLeft.y
                            ),
                            size = Size(
                                width = dateTextLayoutResult.size.width + 12.dp.toPx(),
                                height = dateTextLayoutResult.size.height.toFloat(),
                            ),
                            cornerRadius = CornerRadius(16.dp.toPx())
                        )
                    }
                    if (drawPoint) {
                        drawCircle(
                            color = colorScheme.primaryContainer,
                            radius = 4.dp.toPx(),
                            center = Offset(size.width / 2, dateTopLeft.y - 6.dp.toPx())
                        )
                    }
                    drawText(
                        textLayoutResult = dateTextLayoutResult,
                        topLeft = dateTopLeft,
                        color = if (isToday) {
                            colorScheme.onTertiaryContainer
                        } else if (isCurrentDay) {
                            colorScheme.onPrimary
                        } else if (isCurrentMonth) {
                            colorScheme.onSurface
                        } else {
                            colorScheme.outlineVariant
                        }
                    )
                    moneyTextLayoutResult?.let {
                        drawText(
                            textLayoutResult = moneyTextLayoutResult,
                            topLeft = Offset(
                                (size.width - moneyTextLayoutResult.size.width) / 2,
                                dateTopLeft.y + dateTextLayoutResult.size.height
                            ),
                            color = if (isCurrentDay) {
                                colorScheme.onPrimary
                            } else {
                                colorScheme.onSurface
                            }
                        )
                    }
                }
            }
    )
}