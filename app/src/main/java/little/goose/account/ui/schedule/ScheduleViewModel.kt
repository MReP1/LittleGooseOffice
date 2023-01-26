package little.goose.account.ui.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import little.goose.account.common.receiver.DeleteItemBroadcastReceiver
import little.goose.account.common.receiver.NormalBroadcastReceiver
import little.goose.account.logic.ScheduleRepository
import little.goose.account.logic.data.entities.Schedule

class ScheduleViewModel : ViewModel() {

    private val _schedules = MutableStateFlow<List<Schedule>>(listOf())
    val schedules = _schedules.asStateFlow()

    var deleteReceiver = DeleteItemBroadcastReceiver<Schedule>()

    var updateReceiver: NormalBroadcastReceiver = NormalBroadcastReceiver()

    init {
        ScheduleRepository.getAllScheduleFlow().onEach {
            _schedules.value = it
        }.launchIn(viewModelScope)
    }

    fun updateSchedules() {
        viewModelScope.launch {
            _schedules.value = emptyList()
            _schedules.value = ScheduleRepository.getAllSchedule()
        }
    }

}