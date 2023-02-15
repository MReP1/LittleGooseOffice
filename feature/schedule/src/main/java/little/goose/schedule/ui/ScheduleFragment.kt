package little.goose.schedule.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import little.goose.common.ItemSelectCallback
import little.goose.common.MultipleChoseHandler
import little.goose.common.constants.KEY_SCHEDULE
import little.goose.common.constants.NOTIFY_DELETE_SCHEDULE
import little.goose.common.constants.NOTIFY_UPDATE_SCHEDULE
import little.goose.common.decoration.ItemLinearLayoutDecoration
import little.goose.common.dialog.NormalDialogFragment
import little.goose.common.utils.*
import little.goose.design.system.theme.AccountTheme
import little.goose.schedule.R
import little.goose.schedule.databinding.FragmentScheduleBinding
import little.goose.schedule.data.entities.Schedule

@SuppressLint("NotifyDataSetChanged")
@AndroidEntryPoint
class ScheduleFragment
private constructor() : Fragment() {

    private val viewModel: ScheduleViewModel by viewModels()
    private var callback: ItemSelectCallback<Schedule>? = null

    companion object {
        fun newInstance(): ScheduleFragment {
            return ScheduleFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                AccountTheme {
                    ScheduleRoute(
                        modifier = Modifier.fillMaxSize(),
                        onScheduleClick = {

                        }
                    )
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        callback = null
    }
}

@Composable
private fun ScheduleRoute(
    modifier: Modifier,
    onScheduleClick: (Schedule) -> Unit
) {
    val viewModel: ScheduleViewModel = hiltViewModel()
    val schedules by viewModel.schedules.collectAsState()
    ScheduleScreen(
        modifier = modifier.fillMaxSize(),
        schedules = schedules,
        onScheduleClick = onScheduleClick,
        onCheckedChange = { schedule, isChecked ->
            viewModel.updateSchedule(schedule.copy(isfinish = isChecked))
        }
    )
}

@Composable
private fun ScheduleScreen(
    modifier: Modifier = Modifier,
    schedules: List<Schedule>,
    onScheduleClick: (Schedule) -> Unit,
    onCheckedChange: (Schedule, Boolean) -> Unit
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.background
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp)
        ) {
            items(
                items = schedules,
                key = { it.id ?: -1 }
            ) { schedule ->
                ScheduleItem(
                    modifier = Modifier.fillMaxWidth(),
                    schedule = schedule,
                    onCheckedChange = {
                        onCheckedChange(schedule, it)
                    },
                    onScheduleClick = onScheduleClick
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun ScheduleItem(
    modifier: Modifier,
    schedule: Schedule,
    onScheduleClick: (Schedule) -> Unit,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        onClick = {
            onScheduleClick(schedule)
        },
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1F)) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = schedule.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (schedule.content.isNotBlank()) {
                    Text(
                        text = schedule.content,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Text(
                    text = schedule.time.toChineseMonthDayTime(),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            Checkbox(checked = schedule.isfinish, onCheckedChange = onCheckedChange)
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}