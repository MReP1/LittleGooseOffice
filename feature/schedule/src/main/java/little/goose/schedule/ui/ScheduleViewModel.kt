package little.goose.schedule.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import little.goose.schedule.data.entities.Schedule
import little.goose.schedule.logic.ScheduleRepository
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val scheduleRepository: ScheduleRepository
) : ViewModel() {

    sealed class Event {
        data class DeleteSchedules(val schedules: List<Schedule>) : Event()
    }

    private val _event = MutableSharedFlow<Event>()
    val event = _event.asSharedFlow()

    private val multiSelectedSchedules = MutableStateFlow<Set<Schedule>>(emptySet())

    private val _schedules = MutableStateFlow<List<Schedule>>(listOf())
    val schedules = _schedules.asStateFlow()

    val scheduleColumnState = combine(
        multiSelectedSchedules, schedules
    ) { multiSelectedSchedules, schedules ->
        ScheduleColumnState(
            schedules = schedules,
            isMultiSelecting = multiSelectedSchedules.isNotEmpty(),
            multiSelectedSchedules = multiSelectedSchedules,
            onSelectSchedule = ::selectSchedule,
            selectAllSchedules = ::selectAllSchedules,
            cancelMultiSelecting = ::cancelMultiSelecting,
            onCheckedChange = ::checkSchedule,
            deleteSchedules = ::deleteSchedules
        )
    }.stateIn(
        scope = viewModelScope, SharingStarted.WhileSubscribed(5000L),
        initialValue = ScheduleColumnState(
            schedules = schedules.value,
            isMultiSelecting = multiSelectedSchedules.value.isNotEmpty(),
            multiSelectedSchedules = multiSelectedSchedules.value,
            onSelectSchedule = ::selectSchedule,
            selectAllSchedules = ::selectAllSchedules,
            cancelMultiSelecting = ::cancelMultiSelecting,
            onCheckedChange = ::checkSchedule,
            deleteSchedules = ::deleteSchedules
        )
    )

    fun addSchedule(schedule: Schedule) {
        viewModelScope.launch(Dispatchers.IO) {
            scheduleRepository.addSchedule(schedule)
        }
    }

    fun deleteSchedule(schedule: Schedule) {
        viewModelScope.launch {
            scheduleRepository.deleteSchedule(schedule)
            _event.emit(Event.DeleteSchedules(listOf(schedule)))
        }
    }

    private fun deleteSchedules(schedules: List<Schedule>) {
        viewModelScope.launch(Dispatchers.IO) {
            scheduleRepository.deleteSchedules(schedules)
            _event.emit(Event.DeleteSchedules(schedules))
        }
    }

    fun updateSchedule(schedule: Schedule) {
        viewModelScope.launch(Dispatchers.IO) {
            scheduleRepository.updateSchedule(schedule)
        }
    }

    private fun checkSchedule(schedule: Schedule, checked: Boolean) {
        updateSchedule(schedule = schedule.copy(isfinish = checked))
    }

    private fun selectSchedule(schedule: Schedule, select: Boolean) {
        multiSelectedSchedules.value = multiSelectedSchedules.value.toMutableSet().apply {
            if (select) add(schedule) else remove(schedule)
        }
    }

    private fun selectAllSchedules() {
        multiSelectedSchedules.value = schedules.value.toSet()
    }

    private fun cancelMultiSelecting() {
        multiSelectedSchedules.value = emptySet()
    }

    init {
        scheduleRepository.getAllScheduleFlow().onEach {
            _schedules.value = it
        }.launchIn(viewModelScope)
    }

}