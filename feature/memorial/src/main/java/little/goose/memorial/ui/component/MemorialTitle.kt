package little.goose.memorial.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import little.goose.memorial.data.entities.Memorial
import little.goose.common.utils.DateTimeUtils
import little.goose.memorial.utils.appendTimePrefix
import little.goose.memorial.utils.appendTimeSuffix
import little.goose.common.utils.toChineseYearMonDayWeek
import little.goose.design.system.component.AutoResizedText
import little.goose.memorial.R
import java.util.*

@Composable
fun MemorialTitle(
    modifier: Modifier = Modifier,
    memorial: Memorial
) {
    Surface(modifier = modifier) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val context = LocalContext.current
            val (content, oriTime, verSpace, time, day) = createRefs()
            val barrier = createEndBarrier(content, oriTime)
            createVerticalChain(
                content, verSpace, oriTime,
                chainStyle = ChainStyle.Packed
            )

            Text(
                text = memorial.content
                    .appendTimeSuffix(memorial.time, context),
                modifier = Modifier.constrainAs(content) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start, 24.dp)
                    width = Dimension.fillToConstraints
                }
            )

            Spacer(
                modifier = Modifier.constrainAs(verSpace) {
                    height = Dimension.value(16.dp)
                }
            )

            Text(
                text = memorial.time
                    .toChineseYearMonDayWeek(context)
                    .appendTimePrefix(memorial.time, context),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.constrainAs(oriTime) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start, 24.dp)
                    width = Dimension.fillToConstraints
                }
            )

            val curCalendar = remember(memorial) { Calendar.getInstance() }
            val memCalendar = remember(memorial) {
                Calendar.getInstance().apply { setTime(memorial.time) }
            }

            Box(
                modifier = Modifier.constrainAs(time) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(day.start)
                    start.linkTo(barrier, margin = 8.dp)
                    width = Dimension.fillToConstraints
                },
                contentAlignment = Alignment.Center
            ) {
                AutoResizedText(
                    text = DateTimeUtils.getBetweenDay(curCalendar, memCalendar).toString(),
                    style = MaterialTheme.typography.displayLarge
                )
            }

            Text(
                text = stringResource(id = R.string.sky),
                modifier = Modifier.constrainAs(day) {
                    end.linkTo(parent.end, margin = 24.dp)
                    bottom.linkTo(parent.bottom, margin = 24.dp)
                }
            )
        }
    }
}

@Preview(widthDp = 375, heightDp = 200, showBackground = false)
@Composable
private fun PreviewMemorialTitle() {
    MemorialTitle(
        memorial = Memorial(
            id = null, "HelloWorld",
            isTop = true, Date().apply { time - 1000 * 60 * 60 * 24 * 3 * 4 }
        )
    )
}