package little.goose.account.ui.widget.card

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import little.goose.account.ui.theme.RoundedCorner12

/**
 * Material3 Card的卡片颜色等于Surface颜色的话会有一个颜色叠加渲染的效果
 * 使用这个Card不使用那个效果
 * */
@Composable
fun ShadowCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = RoundedCorner12,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    contentColor: Color = contentColorFor(containerColor),
    border: BorderStroke? = null,
    content: @Composable (ColumnScope.() -> Unit)
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = shape,
        color = containerColor,
        contentColor = contentColor,
        shadowElevation = 0.dp,
        border = border,
        interactionSource = interactionSource
    ) {
        Column(content = content)
    }
}