package little.goose.schedule.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import little.goose.schedule.data.entities.Schedule
import little.goose.schedule.logic.DeleteSchedulesEventUseCase
import little.goose.schedule.logic.DeleteSchedulesUseCase
import little.goose.schedule.logic.GetAllScheduleFlowUseCase
import little.goose.schedule.logic.UpdateScheduleUseCase
import javax.inject.Inject

@HiltViewModel
class ScheduleHomeViewModel @Inject constructor(
    private val updateScheduleUseCase: UpdateScheduleUseCase,
    private val deleteSchedulesUseCase: DeleteSchedulesUseCase,
    getAllScheduleFlow: GetAllScheduleFlowUseCase,
    deleteSchedulesEventUseCase: DeleteSchedulesEventUseCase
) : ViewModel() {

    sealed class Event {
        data class DeleteSchedules(val schedules: List<Schedule>) : Event()
    }

    private val _event = MutableSharedFlow<Event>()
    val event = _event.asSharedFlow()

    private val multiSelectedSchedules = MutableStateFlow<Set<Schedule>>(emptySet())

    val schedules = getAllScheduleFlow().stateIn(
        scope = viewModelScope, SharingStarted.WhileSubscribed(5000L),
        initialValue = emptyList()
    )

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

    init {
        deleteSchedulesEventUseCase().onEach {
            _event.emit(Event.DeleteSchedules(it))
        }.launchIn(viewModelScope)
    }

    private fun deleteSchedules(schedules: List<Schedule>) {
        viewModelScope.launch {
            deleteSchedulesUseCase(schedules)
        }
    }

    private fun updateSchedule(schedule: Schedule) {
        viewModelScope.launch {
            updateScheduleUseCase(schedule)
            cancelMultiSelecting()
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

}