package little.goose.schedule.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.dialog
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import little.goose.common.constants.DEEP_LINK_THEME_AND_HOST
import little.goose.common.utils.TimeType
import little.goose.common.utils.toChineseStringWithYear
import little.goose.design.system.component.dialog.DeleteDialog
import little.goose.design.system.component.dialog.DeleteDialogState
import little.goose.design.system.component.dialog.TimeSelectorBottomSheet
import little.goose.design.system.theme.AccountTheme
import little.goose.schedule.R
import little.goose.schedule.data.entities.Schedule

const val KEY_SCHEDULE_ID = "schedule_id"

const val ROUTE_DIALOG_SCHEDULE = "dialog_schedule"

private const val DEEP_LINK_URI_PATTERN_SCHEDULE_DIALOG =
    "$DEEP_LINK_THEME_AND_HOST/$ROUTE_DIALOG_SCHEDULE" +
            "/$KEY_SCHEDULE_ID={$KEY_SCHEDULE_ID}"

fun NavController.navigateToScheduleDialog(scheduleId: Long?) {
    navigate("$ROUTE_DIALOG_SCHEDULE/${scheduleId ?: -1}") {
        launchSingleTop = true
    }
}

fun NavGraphBuilder.scheduleRoute(
    onDismissRequest: () -> Unit
) {
    dialog(
        route = "$ROUTE_DIALOG_SCHEDULE/{$KEY_SCHEDULE_ID}",
        deepLinks = listOf(
            navDeepLink {
                uriPattern = DEEP_LINK_URI_PATTERN_SCHEDULE_DIALOG
            }
        ),
        arguments = listOf(
            navArgument(KEY_SCHEDULE_ID) {
                type = NavType.LongType
                defaultValue = -1L
            }
        )
    ) {
        val deleteScheduleDialogState = remember { DeleteDialogState() }
        val viewModel = hiltViewModel<ScheduleDialogViewModel>()
        val schedule by viewModel.schedule.collectAsStateWithLifecycle()
        var isShowTimeSelectorDialog by remember { mutableStateOf(false) }
        val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        if (isShowTimeSelectorDialog) {
            TimeSelectorBottomSheet(
                modifier = Modifier.width(300.dp),
                initTime = schedule.time,
                type = TimeType.DATE_TIME,
                bottomSheetState = bottomSheetState,
                onConfirm = viewModel::updateScheduleTime
            )
        }
        DisposableEffect(bottomSheetState.currentValue) {
            if (bottomSheetState.currentValue == SheetValue.Hidden) {
                isShowTimeSelectorDialog = false
            }
            onDispose { }
        }

        ScheduleDialogScreen(
            schedule = schedule,
            onTitleChange = viewModel::updateScheduleTitle,
            onContentChange = viewModel::updateScheduleContent,
            onCancelClick = onDismissRequest,
            onConfirmClick = {
                if (schedule.id == null) {
                    viewModel.insertSchedule()
                } else {
                    viewModel.updateSchedule()
                }
                onDismissRequest()
            },
            onDeleteClick = {
                deleteScheduleDialogState.show(onConfirm = {
                    viewModel.deleteSchedule()
                    onDismissRequest()
                })
            },
            onChangeTimeClick = {
                isShowTimeSelectorDialog = true
            }
        )

        DeleteDialog(
            state = deleteScheduleDialogState
        )
    }
}

@Composable
private fun ScheduleDialogScreen(
    modifier: Modifier = Modifier,
    schedule: Schedule,
    onTitleChange: (String) -> Unit,
    onContentChange: (String) -> Unit,
    onCancelClick: () -> Unit,
    onConfirmClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onChangeTimeClick: () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val isAdd = schedule.id == null

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = stringResource(
                    id = if (isAdd) R.string.add_schedule else R.string.modify_schedule
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = onChangeTimeClick) {
                Text(text = schedule.time.toChineseStringWithYear())
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = schedule.title,
                onValueChange = onTitleChange,
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth(),
                label = {
                    Text(text = stringResource(id = R.string.schedule_title_tag))
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = schedule.content,
                onValueChange = onContentChange,
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth(),
                label = {
                    Text(text = stringResource(id = R.string.schedule_content_tag))
                }
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = {
                        if (isAdd) {
                            onCancelClick()
                        } else {
                            onDeleteClick()
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = if (isAdd) {
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.58F)
                        } else {
                            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.68F)
                        }
                    )
                ) {
                    Text(
                        text = stringResource(
                            id = if (isAdd) little.goose.common.R.string.cancel
                            else little.goose.common.R.string.delete
                        )
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                TextButton(
                    onClick = onConfirmClick,
                    modifier = Modifier
                ) {
                    Text(text = stringResource(id = little.goose.common.R.string.confirm))
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewScheduleDialog() = AccountTheme {
    ScheduleDialogScreen(
        schedule = Schedule(),
        onTitleChange = {},
        onContentChange = {},
        onCancelClick = {},
        onConfirmClick = {},
        onDeleteClick = {},
        onChangeTimeClick = {}
    )
}