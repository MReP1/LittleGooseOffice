package little.goose.schedule.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import little.goose.common.receiver.DeleteItemBroadcastReceiver
import little.goose.common.receiver.NormalBroadcastReceiver
import little.goose.schedule.logic.ScheduleRepository
import little.goose.schedule.data.entities.Schedule
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val scheduleRepository: ScheduleRepository
) : ViewModel() {

    private val _schedules = MutableStateFlow<List<Schedule>>(listOf())
    val schedules = _schedules.asStateFlow()

    var deleteReceiver = DeleteItemBroadcastReceiver<Schedule>()

    var updateReceiver: NormalBroadcastReceiver = NormalBroadcastReceiver()

    fun addSchedule(schedule: Schedule) {
        viewModelScope.launch(Dispatchers.IO) {
            scheduleRepository.addSchedule(schedule)
        }
    }

    fun addSchedules(schedules: List<Schedule>) {
        viewModelScope.launch(Dispatchers.IO) {
            scheduleRepository.addSchedules(schedules)
        }
    }

    fun deleteSchedules(schedules: List<Schedule>) {
        viewModelScope.launch(Dispatchers.IO) {
            scheduleRepository.deleteSchedules(schedules)
        }
    }

    fun updateSchedule(schedule: Schedule) {
        viewModelScope.launch(Dispatchers.IO) {
            scheduleRepository.updateSchedule(schedule)
        }
    }

    init {
        scheduleRepository.getAllScheduleFlow().onEach {
            _schedules.value = it
        }.launchIn(viewModelScope)
    }

    fun updateSchedules() {
        viewModelScope.launch {
            _schedules.value = emptyList()
            _schedules.value = scheduleRepository.getAllSchedule()
        }
    }

}