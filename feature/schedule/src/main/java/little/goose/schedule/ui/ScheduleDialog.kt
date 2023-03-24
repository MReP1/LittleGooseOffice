package little.goose.schedule.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import little.goose.common.utils.toChineseStringWithYear
import little.goose.design.system.component.dialog.*
import little.goose.schedule.R
import little.goose.schedule.data.entities.Schedule

@Composable
fun rememberScheduleDialogState(): ScheduleDialogState {
    return remember { ScheduleDialogState() }
}

@Stable
class ScheduleDialogState {

    var schedule by mutableStateOf(Schedule())

    internal val dialogState = DialogState(false)

    fun show(schedule: Schedule) {
        this.schedule = schedule
        dialogState.show()
    }

    fun dismiss() {
        dialogState.dismiss()
    }

}

@Composable
fun ScheduleDialog(
    state: ScheduleDialogState,
    onDelete: (Schedule) -> Unit,
    onAdd: (Schedule) -> Unit,
    onModify: (Schedule) -> Unit
) {
    val deleteScheduleDialogState = rememberDialogState()
    val timeSelectorState = rememberBottomSheetDialogState()
    val scope = rememberCoroutineScope()

    NormalDialog(state = state.dialogState) {
        ScheduleDialogScreen(
            schedule = state.schedule,
            onTitleChange = {
                state.schedule = state.schedule.copy(title = it)
            },
            onContentChange = {
                state.schedule = state.schedule.copy(content = it)
            },
            onCancelClick = state::dismiss,
            onConfirmClick = {
                if (state.schedule.id == null) {
                    onAdd(state.schedule)
                } else {
                    onModify(state.schedule)
                }
                state.dismiss()
            },
            onDeleteClick = deleteScheduleDialogState::show,
            onChangeTimeClick = {
                scope.launch {
                    timeSelectorState.open()
                }
            }
        )
    }

    DeleteDialog(
        state = deleteScheduleDialogState,
        onConfirm = {
            onDelete(state.schedule)
            state.dismiss()
        }
    )

    TimeSelectorBottomDialog(
        state = timeSelectorState,
        initTime = state.schedule.time,
        onConfirm = { state.schedule = state.schedule.copy(time = it) }
    )
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

            Spacer(modifier = Modifier.height(30.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = {
                        if (isAdd) {
                            onCancelClick()
                        } else {
                            onDeleteClick()
                        }
                    },
                    modifier = Modifier
                        .weight(1F)
                        .height(58.dp),
                    shape = RectangleShape
                ) {
                    Text(
                        text = stringResource(
                            id = if (isAdd) {
                                little.goose.common.R.string.cancel
                            } else {
                                little.goose.common.R.string.delete
                            }
                        )
                    )
                }

                Button(
                    onClick = onConfirmClick,
                    modifier = Modifier
                        .weight(1F)
                        .height(58.dp),
                    shape = RectangleShape
                ) {
                    Text(text = stringResource(id = little.goose.common.R.string.confirm))
                }
            }
        }
    }
}