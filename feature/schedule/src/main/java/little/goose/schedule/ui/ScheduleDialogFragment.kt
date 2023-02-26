package little.goose.schedule.ui

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import little.goose.common.constants.*
import little.goose.common.dialog.DateTimePickerBottomDialog
import little.goose.common.utils.*
import little.goose.design.system.component.dialog.DeleteDialog
import little.goose.design.system.component.dialog.rememberDialogState
import little.goose.design.system.theme.AccountTheme
import little.goose.schedule.R
import little.goose.schedule.data.entities.Schedule
import java.util.*

@AndroidEntryPoint
class ScheduleDialogFragment
private constructor() : DialogFragment() {

    private val viewModel by viewModels<ScheduleDialogViewModel>()

    @OptIn(ExperimentalComposeUiApi::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                AccountTheme {
                    val state by viewModel.scheduleDialogState.collectAsState()
                    val deleteDialogState = rememberDialogState()
                    ScheduleDialogScreen(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentSize(),
                        state = state,
                        onDeleteClick = deleteDialogState::show,
                        onChangeTimeClick = ::changeTime
                    )

                    DeleteDialog(
                        state = deleteDialogState,
                        onConfirm = {
                            viewModel.deleteSchedule()
                            dismiss()
                        }
                    )

                    val keyboard = LocalSoftwareKeyboardController.current
                    LaunchedEffect(viewModel.event) {
                        viewModel.event.collect { event ->
                            when (event) {
                                ScheduleDialogViewModel.Event.Cancel -> {
                                    dismiss()
                                }
                                ScheduleDialogViewModel.Event.Dismiss -> {
                                    dismiss()
                                }
                                ScheduleDialogViewModel.Event.HideKeyboard -> {
                                    keyboard?.hide()
                                }
                                ScheduleDialogViewModel.Event.NoContent -> {
                                    context?.let {
                                        Toast.makeText(
                                            it, getString(R.string.schedule_cant_be_blank),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun changeTime() {
        DateTimePickerBottomDialog.Builder()
            .setTime(viewModel.scheduleDialogState.value.schedule.time)
            .setConfirmAction(viewModel::changeTime)
            .setDimVisibility(false)
            .showNow(parentFragmentManager)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initWindow()
    }

    private fun initWindow() {
        dialog?.window?.setBackgroundDrawable(null)
        dialog?.window?.attributes?.apply {
            width = UIUtils.getWidthPercentPixel(0.76F)
            height = ViewGroup.LayoutParams.WRAP_CONTENT
            gravity = Gravity.CENTER
        }
    }

    companion object {
        fun newInstance(
            schedule: Schedule? = null,
            time: Date? = null
        ): ScheduleDialogFragment {
            return ScheduleDialogFragment().apply {
                arguments = Bundle().apply {
                    schedule?.let { putParcelable(KEY_SCHEDULE, it) }
                    time?.let { putSerializable(KEY_TIME, it) }
                }
            }
        }
    }

}

data class ScheduleDialogState(
    val schedule: Schedule,
    val onTitleChange: (String) -> Unit,
    val onContentChange: (String) -> Unit,
    val onCancelClick: () -> Unit,
    val onConfirmClick: () -> Unit
)

@Composable
private fun ScheduleDialogScreen(
    modifier: Modifier = Modifier,
    state: ScheduleDialogState,
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
            val isAdd = state.schedule.id == null

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = stringResource(
                    id = if (isAdd) R.string.add_schedule else R.string.modify_schedule
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = onChangeTimeClick) {
                Text(text = state.schedule.time.toChineseStringWithYear())
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = state.schedule.title,
                onValueChange = state.onTitleChange,
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = state.schedule.content,
                onValueChange = state.onContentChange,
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(30.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = {
                        if (isAdd) {
                            state.onCancelClick()
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
                    onClick = state.onConfirmClick,
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