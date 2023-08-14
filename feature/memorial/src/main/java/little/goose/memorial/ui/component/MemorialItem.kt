package little.goose.memorial.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import little.goose.common.utils.DateTimeUtils
import little.goose.common.utils.toChineseYearMonDayWeek
import little.goose.memorial.data.entities.Memorial
import little.goose.memorial.utils.appendTimePrefix
import little.goose.memorial.utils.appendTimeSuffix
import java.util.Calendar
import java.util.Date

private const val TOTAL_ANIM_MILLIS = 266
private const val DELAY_ANIM_MILLIS = 82

@Composable
fun MemorialItem(
    modifier: Modifier = Modifier,
    memorial: Memorial,
    isExpended: Boolean = false,
    isMultiSelecting: Boolean = false,
    selected: Boolean = false,
    onMemorialClick: (Memorial) -> Unit,
    onMemorialEdit: (Memorial) -> Unit,
    onMemorialDelete: (Memorial) -> Unit,
    onSelectMemorial: (Memorial, Boolean) -> Unit
) {
    val context = LocalContext.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
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
        val transition = updateTransition(
            targetState = isExpended, label = "memorial expend animation"
        )

        val contentHeight by transition.animateDp(
            transitionSpec = {
                tween(
                    durationMillis = TOTAL_ANIM_MILLIS,
                    delayMillis = if (targetState) DELAY_ANIM_MILLIS else 0,
                    easing = FastOutSlowInEasing
                )
            },
            label = "content height"
        ) {
            if (it) 180.dp else 52.dp
        }
        Column(
            modifier = Modifier.height(contentHeight)
        ) {
            Row(
                Modifier.height(52.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                val titleBackgroundColor by transition.animateColor(
                    transitionSpec = {
                        tween(
                            durationMillis = TOTAL_ANIM_MILLIS,
                            delayMillis = if (targetState) 0 else DELAY_ANIM_MILLIS,
                            easing = FastOutSlowInEasing
                        )
                    },
                    label = "background color"
                ) {
                    if (it) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surface
                }

                Surface(
                    color = titleBackgroundColor,
                    modifier = Modifier
                        .weight(1F)
                        .fillMaxHeight(),
                    tonalElevation = 1.8.dp
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        val spacerWidth by transition.animateFloat(
                            transitionSpec = {
                                tween(
                                    durationMillis = TOTAL_ANIM_MILLIS,
                                    easing = FastOutSlowInEasing
                                )
                            },
                            label = "spacer width"
                        ) {
                            if (it) 0.5F else 0.13F
                        }
                        Spacer(modifier = Modifier.weight(spacerWidth))
                        val textColor by transition.animateColor(
                            transitionSpec = {
                                tween(
                                    durationMillis = TOTAL_ANIM_MILLIS,
                                    easing = FastOutSlowInEasing
                                )
                            },
                            label = "text color"
                        ) {
                            if (it) MaterialTheme.colorScheme.onPrimaryContainer
                            else MaterialTheme.colorScheme.onSurface
                        }
                        Text(
                            text = memorial.content.appendTimeSuffix(memorial.time, context),
                            color = textColor
                        )
                        Spacer(modifier = Modifier.weight(1 - spacerWidth))
                    }
                }


                val curCalendar = remember(memorial) {
                    Calendar.getInstance()
                }

                val memCalendar = remember(memorial) {
                    Calendar.getInstance().apply { time = memorial.time }
                }

                val boxWidth by transition.animateDp(
                    transitionSpec = {
                        tween(
                            durationMillis = TOTAL_ANIM_MILLIS,
                            easing = FastOutSlowInEasing
                        )
                    },
                    label = "box width"
                ) {
                    if (it) 0.dp else 64.dp
                }

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(boxWidth)
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
                        .fillMaxHeight()
                        .width(boxWidth)
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
            
            transition.AnimatedVisibility(
                modifier = Modifier.height(contentHeight - 52.dp),
                visible = { it },
                enter = fadeIn(
                    tween(
                        durationMillis = TOTAL_ANIM_MILLIS,
                        delayMillis = DELAY_ANIM_MILLIS,
                        easing = FastOutSlowInEasing
                    )
                ),
                exit = fadeOut(
                    tween(
                        durationMillis = TOTAL_ANIM_MILLIS,
                        easing = FastOutSlowInEasing
                    )
                )
            ) {
                MemorialExpendedContent(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(contentHeight - 52.dp),
                    memorial = memorial,
                    contentHeight = contentHeight,
                    onMemorialEdit = onMemorialEdit,
                    onMemorialDelete = onMemorialDelete
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

@Composable
private fun MemorialExpendedContent(
    modifier: Modifier,
    memorial: Memorial,
    contentHeight: Dp,
    onMemorialEdit: (Memorial) -> Unit,
    onMemorialDelete: (Memorial) -> Unit
) {
    val context = LocalContext.current
    Surface(
        modifier = modifier,
        tonalElevation = 1.8.dp
    ) {
        Column {
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(contentHeight - 102.dp)
                    .padding(horizontal = 18.dp),
                contentAlignment = Alignment.Center
            ) {
                MemorialText(memorial = memorial)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = memorial.time
                        .toChineseYearMonDayWeek(context)
                        .appendTimePrefix(memorial.time, context),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    modifier = Modifier
                        .weight(1F)
                        .basicMarquee()
                )
                IconButton(onClick = { onMemorialEdit(memorial) }) {
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = "Edit"
                    )
                }
                IconButton(onClick = { onMemorialDelete(memorial) }) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = "Delete"
                    )
                }
            }
        }
    }
}

@Preview(widthDp = 375)
@Composable
private fun PreviewMemorialItem() {
    MemorialItem(
        memorial = Memorial(null, "HelloWorld", false, Date()),
        selected = true,
        onMemorialClick = {},
        onSelectMemorial = { _, _ -> },
        onMemorialEdit = {},
        onMemorialDelete = {}
    )
}