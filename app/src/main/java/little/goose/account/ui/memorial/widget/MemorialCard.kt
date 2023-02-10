package little.goose.account.ui.memorial.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import little.goose.account.logic.data.entities.Memorial
import little.goose.account.utils.*
import little.goose.common.CircularLinkList
import little.goose.design.system.component.AutoResizedText
import java.util.*

@Composable
fun MemorialCard(
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape,
    memorial: Memorial
) {
    val context = LocalContext.current
    val curCalendar = remember(memorial) { Calendar.getInstance() }

    val times = remember(memorial) {
        CircularLinkList<String>().apply {
            val memorialCalendar = Calendar.getInstance()
            memorialCalendar.time = memorial.time
            val days = DateTimeUtils.getBetweenDay(memorialCalendar, curCalendar)
            add(days.toString())
            val curMonthDay = DateTimeUtils.getDaysByYearMonth(
                memorialCalendar.getYear(),
                memorialCalendar.getMonth()
            )
            if (days > curMonthDay) {
                val monthDay = DateTimeUtils.getBetweenMonthDay(curCalendar, memorialCalendar)
                val monthDayStr = "${monthDay.month}个月${monthDay.day}天"
                add(monthDayStr)
                if (monthDay.month > 12) {
                    if (monthDay.month % 12 != 0) {
                        add("${monthDay.month / 12}年${monthDay.month % 12}个月${monthDay.day}天")
                    } else {
                        add("${monthDay.month / 12}年${monthDay.day}天")
                    }
                }
            }
        }
    }
    val currentTime by times.currentNote.collectAsState("")
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

            Box(
                modifier = Modifier
                    .weight(1F)
                    .fillMaxWidth()
                    .padding(18.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        times.next()
                    },
                contentAlignment = Alignment.Center
            ) {
                AutoResizedText(
                    text = currentTime.toString(),
                    style = MaterialTheme.typography.displayLarge,
                )
            }

            Text(text = memorial.time.toChineseYearMonDayWeek().appendTimePrefix(memorial.time))

            Spacer(modifier = Modifier.height(18.dp))
        }
    }
}