package little.goose.account.ui.component

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text2.BasicTextField2
import androidx.compose.foundation.text2.input.TextFieldLineLimits
import androidx.compose.foundation.text2.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.launch
import little.goose.account.R
import little.goose.account.ui.transaction.TransactionScreenIntent
import little.goose.common.utils.TimeType
import little.goose.common.utils.toChineseMonthDayTime
import little.goose.design.system.component.dialog.TimeSelectorBottomSheet
import java.util.Date
import kotlin.math.pow

@Composable
fun TransactionContentEditBar(
    modifier: Modifier = Modifier,
    isDescriptionEdit: Boolean,
    onIsDescriptionEditChange: (Boolean) -> Unit,
    time: Date,
    onTransactionChange: (TransactionScreenIntent.ChangeTransaction) -> Unit,
    descriptionTextFieldState: TextFieldState
) {
    var isShowTimeSelector by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    if (isShowTimeSelector) {
        val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        TimeSelectorBottomSheet(
            onDismissRequest = {
                scope.launch {
                    bottomSheetState.hide()
                    isShowTimeSelector = false
                }
            },
            initTime = time,
            type = TimeType.DATE_TIME,
            bottomSheetState = bottomSheetState,
            onConfirm = {
                onTransactionChange(TransactionScreenIntent.ChangeTransaction.Time(it))
            }
        )
    }
    val isDescriptionEditUpdateTransition = updateTransition(
        targetState = isDescriptionEdit, label = "is description edit"
    )
    val contentBarHeight by isDescriptionEditUpdateTransition.animateDp(
        label = "content bar height",
        transitionSpec = {
            tween(60, delayMillis = if (targetState) 140 else 0)
        }
    ) { if (it) 84.dp else 42.dp }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(contentBarHeight)
    ) {
        val dateScale by isDescriptionEditUpdateTransition.animateFloat(
            label = "date scale",
            transitionSpec = { tween(140) }
        ) { if (it) 0.88F else 1F }
        Surface(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .zIndex(1F),
            onClick = {
                isShowTimeSelector = true
            }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .scale(dateScale)
                    .alpha(dateScale.pow(5))
                    .padding(top = 8.dp, bottom = 8.dp, start = 20.dp, end = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Rounded.CalendarToday, contentDescription = "Calendar")
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = time.toChineseMonthDayTime(),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        val configuration = LocalConfiguration.current
        val descriptionWidth by isDescriptionEditUpdateTransition.animateDp(
            label = "description width",
            transitionSpec = { tween(140) }
        ) { if (it) configuration.screenWidthDp.dp else (configuration.screenWidthDp / 2).dp }
        Surface(
            modifier = Modifier
                .width(descriptionWidth)
                .align(Alignment.CenterEnd)
                .zIndex(2F),
            onClick = { onIsDescriptionEditChange(!isDescriptionEdit) }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp, bottom = 8.dp, start = 16.dp, end = 4.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (!isDescriptionEdit) {
                    Text(
                        text = descriptionTextFieldState.text.toString().ifBlank {
                            stringResource(id = R.string.description) + "..."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                } else {
                    val focusRequester = remember { FocusRequester() }
                    val lineLimits = remember {
                        TextFieldLineLimits.MultiLine(
                            minHeightInLines = 1,
                            maxHeightInLines = 2
                        )
                    }

                    BasicTextField2(
                        modifier = Modifier
                            .focusRequester(focusRequester)
                            .padding(end = 32.dp),
                        state = descriptionTextFieldState,
                        textStyle = MaterialTheme.typography.bodySmall,
                        lineLimits = lineLimits,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { onIsDescriptionEditChange(false) }
                        )
                    )

                    DisposableEffect(focusRequester) {
                        focusRequester.requestFocus()
                        onDispose { }
                    }

                    Row(modifier = Modifier.align(Alignment.BottomEnd)) {
                        IconButton(onClick = { onIsDescriptionEditChange(false) }) {
                            Icon(imageVector = Icons.Rounded.Done, contentDescription = "Done")
                        }
                    }
                }
            }
        }
    }
}