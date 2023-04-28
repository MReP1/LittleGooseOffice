package little.goose.memorial.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import little.goose.memorial.data.entities.Memorial
import little.goose.common.utils.DateTimeUtils
import little.goose.memorial.utils.appendTimeSuffix
import java.util.*

@Composable
fun MemorialItem(
    modifier: Modifier = Modifier,
    memorial: Memorial,
    isMultiSelecting: Boolean = false,
    selected: Boolean = false,
    onMemorialClick: (Memorial) -> Unit,
    onSelectMemorial: (Memorial, Boolean) -> Unit
) {
    val context = LocalContext.current
    Card(
        onClick = {
            onMemorialClick(memorial)
        },
        modifier = modifier.wrapContentSize()
    ) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    if (isMultiSelecting) {
                        onSelectMemorial(memorial, !selected)
                    } else {
                        onMemorialClick(memorial)
                    }
                },
                onLongClick = {
                    onSelectMemorial(memorial, !selected)
                }
            ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                Modifier
                    .wrapContentWidth()
                    .height(52.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = memorial.content.appendTimeSuffix(memorial.time, context),
                    modifier = Modifier.weight(1F)
                )
                Spacer(modifier = Modifier.width(16.dp))

                val curCalendar = remember(memorial) {
                    Calendar.getInstance()
                }

                val memCalendar = remember(memorial) {
                    Calendar.getInstance().apply { time = memorial.time }
                }

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(64.dp)
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(vertical = 16.dp, horizontal = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = DateTimeUtils.getBetweenDay(curCalendar, memCalendar)
                            .toString(),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Box(
                    modifier = Modifier
                        .width(64.dp)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.secondary)
                        .padding(vertical = 16.dp, horizontal = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(id = little.goose.memorial.R.string.sky),
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                }
            }
            if (selected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    tint = MaterialTheme.colorScheme.tertiary,
                    contentDescription = "selected",
                    modifier = Modifier
                        .matchParentSize()
                        .alpha(0.5F)
                )
            }
        }
    }
}

@Preview(widthDp = 375, heightDp = 100)
@Composable
private fun PreviewMemorialItem() {
    MemorialItem(
        memorial = Memorial(null, "HelloWorld", false, Date()),
        selected = true,
        onMemorialClick = {},
        onSelectMemorial = { _, _ -> }
    )
}