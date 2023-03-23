package little.goose.schedule.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import little.goose.schedule.data.entities.Schedule
import little.goose.schedule.logic.ScheduleRepository
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val scheduleRepository: ScheduleRepository
) : ViewModel() {

    private val _schedules = MutableStateFlow<List<Schedule>>(listOf())
    val schedules = _schedules.asStateFlow()

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

    fun deleteSchedule(schedule: Schedule) {
        viewModelScope.launch {
            scheduleRepository.deleteSchedule(schedule)
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