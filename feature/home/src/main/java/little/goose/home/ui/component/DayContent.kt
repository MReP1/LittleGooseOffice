package little.goose.home.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import little.goose.design.system.theme.RoundedCorner16
import little.goose.home.data.CalendarModel
import java.time.LocalDate

@Composable
internal fun MonthDay(
    modifier: Modifier,
    day: CalendarDay,
    isToday: Boolean,
    isCurrentDay: Boolean,
    model: CalendarModel,
    onClick: (LocalDate) -> Unit
) {
    DayContent(
        modifier = modifier,
        date = day.date,
        isCurrentMonth = day.position == DayPosition.MonthDate,
        isCurrentDay = isCurrentDay,
        isToday = isToday,
        model = model,
        onClick = onClick
    )
}

@Composable
internal fun DayContent(
    modifier: Modifier,
    date: LocalDate,
    isCurrentMonth: Boolean,
    isCurrentDay: Boolean,
    isToday: Boolean,
    model: CalendarModel,
    onClick: (LocalDate) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCorner16)
            .run {
                if (isCurrentDay) background(MaterialTheme.colorScheme.primary) else this
            }
            .clickable { onClick(date) },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val colorScheme = MaterialTheme.colorScheme
        if (model.containSomething) {
            Canvas(modifier = Modifier.size(14.dp)) {
                drawCircle(color = colorScheme.primaryContainer, radius = size.width / 4)
            }
        } else {
            Spacer(modifier = Modifier.height(14.dp))
        }
        Text(
            text = date.dayOfMonth.toString(),
            modifier = Modifier.run {
                if (isToday) background(colorScheme.tertiaryContainer, RoundedCorner16)
                    .padding(horizontal = 6.dp) else this
            },
            color = if (isToday) {
                MaterialTheme.colorScheme.onTertiaryContainer
            } else if (isCurrentDay) {
                MaterialTheme.colorScheme.onPrimary
            } else if (isCurrentMonth) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.outlineVariant
            }
        )
        if (model.balance.signum() != 0) {
            Text(
                text = model.balance.toPlainString() ?: "",
                modifier = Modifier.height(14.dp),
                style = MaterialTheme.typography.bodySmall,
                color = if (isCurrentDay) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        } else {
            Spacer(modifier = Modifier.height(14.dp))
        }
    }
}