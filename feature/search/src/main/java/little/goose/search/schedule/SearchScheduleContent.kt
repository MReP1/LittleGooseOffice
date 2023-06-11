package little.goose.search.schedule

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DoneAll
import androidx.compose.material.icons.rounded.RemoveDone
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import little.goose.design.system.component.MovableActionButton
import little.goose.design.system.component.MovableActionButtonState
import little.goose.design.system.component.dialog.DeleteDialog
import little.goose.design.system.component.dialog.DeleteDialogState
import little.goose.design.system.theme.AccountTheme
import little.goose.schedule.data.entities.Schedule
import little.goose.schedule.ui.ScheduleColumn
import little.goose.schedule.ui.ScheduleColumnState

@Composable
internal fun SearchScheduleContent(
    modifier: Modifier = Modifier,
    scheduleColumnState: ScheduleColumnState,
    onNavigateToScheduleDialog: (Long) -> Unit
) {
    if (scheduleColumnState.schedules.isNotEmpty()) {
        ScheduleColumn(
            modifier = modifier.fillMaxSize(),
            state = scheduleColumnState,
            onScheduleClick = { schedule ->
                schedule.id?.let { id -> onNavigateToScheduleDialog(id) }
            }
        )
    }

    val deleteDialogState = remember { DeleteDialogState() }

    if (scheduleColumnState.isMultiSelecting) {
        val buttonState = remember { MovableActionButtonState() }
        LaunchedEffect(buttonState) { buttonState.expend() }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
        ) {
            MovableActionButton(
                modifier = Modifier.align(Alignment.BottomEnd),
                state = buttonState,
                mainButtonContent = {
                    Icon(imageVector = Icons.Rounded.Delete, contentDescription = "Delete")
                },
                onMainButtonClick = {
                    deleteDialogState.show(onConfirm = {
                        scheduleColumnState.deleteSchedules(
                            scheduleColumnState.multiSelectedSchedules.toList()
                        )
                    })
                },
                topSubButtonContent = {
                    Icon(imageVector = Icons.Rounded.DoneAll, contentDescription = "SelectAll")
                },
                onTopSubButtonClick = {
                    scheduleColumnState.selectAllSchedules()
                },
                bottomSubButtonContent = {
                    Icon(imageVector = Icons.Rounded.RemoveDone, contentDescription = "Remove")
                },
                onBottomSubButtonClick = {
                    scheduleColumnState.cancelMultiSelecting()
                }
            )
        }
    }

    DeleteDialog(state = deleteDialogState)
}

@Preview
@Composable
private fun PreviewSearchScheduleContent() = AccountTheme {
    SearchScheduleContent(
        scheduleColumnState = ScheduleColumnState(
            schedules = (0..5).map {
                Schedule(
                    id = it.toLong(),
                    title = "title$it",
                    content = "content$it",
                    isfinish = it % 2 == 0
                )
            },
            isMultiSelecting = true,
            multiSelectedSchedules = emptySet(),
            onSelectSchedule = { _, _ -> },
            onCheckedChange = { _, _ -> },
            selectAllSchedules = {},
            cancelMultiSelecting = {},
            deleteSchedules = {}
        ),
        onNavigateToScheduleDialog = {}
    )
}