package little.goose.chart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ChartLabel(
    modifier: Modifier = Modifier,
    color: Color,
    text: String,
    colorPalette: @Composable () -> Unit = {
        Spacer(modifier = Modifier.padding(horizontal = 4.dp).size(8.dp).clip(RoundedCornerShape(3.dp)).background(color))
    },
    label: @Composable () -> Unit = {
        Text(text, style = MaterialTheme.typography.labelSmall)
    }
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        colorPalette()
        label()
    }
}