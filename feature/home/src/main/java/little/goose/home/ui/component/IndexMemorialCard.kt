package little.goose.home.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import little.goose.common.utils.getRealDate
import little.goose.memorial.data.entities.Memorial
import little.goose.memorial.utils.appendTimeSuffix

@Composable
fun IndexMemorialCard(
    modifier: Modifier,
    memorial: Memorial
) {
    val context = LocalContext.current
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = memorial.content.appendTimeSuffix(
                    memorial.time, context
                ) + memorial.time.getRealDate() + "天"
            )
        }
    }
}