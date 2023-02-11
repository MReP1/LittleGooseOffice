package little.goose.account.ui.memorial.widget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import little.goose.account.logic.data.entities.Memorial
import little.goose.account.utils.DateTimeUtils
import little.goose.account.utils.getMonth
import little.goose.account.utils.getYear
import little.goose.common.CircularLinkList
import little.goose.design.system.component.AutoResizedText
import java.util.*

@Composable
fun MemorialText(
    modifier: Modifier = Modifier,
    memorial: Memorial,
    switchable: Boolean = true
) {
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

    Box(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = switchable
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
}