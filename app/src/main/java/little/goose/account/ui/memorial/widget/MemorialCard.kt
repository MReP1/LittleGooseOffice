package little.goose.account.ui.memorial.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import little.goose.account.logic.data.entities.Memorial
import little.goose.account.utils.appendTimePrefix
import little.goose.account.utils.appendTimeSuffix
import little.goose.account.utils.toChineseYearMonDayWeek

@Composable
fun MemorialCard(
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape,
    memorial: Memorial
) {
    val context = LocalContext.current
    Surface(
        modifier = modifier,
        shape = shape,
        shadowElevation = 3.dp
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .height(240.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .height(54.dp)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = memorial.content.appendTimeSuffix(memorial.time, context),
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            MemorialText(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1F)
                    .padding(18.dp),
                memorial = memorial
            )

            Text(text = memorial.time.toChineseYearMonDayWeek().appendTimePrefix(memorial.time))

            Spacer(modifier = Modifier.height(18.dp))
        }
    }
}