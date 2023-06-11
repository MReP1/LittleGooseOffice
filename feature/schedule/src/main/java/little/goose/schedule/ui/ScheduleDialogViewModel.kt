package little.goose.schedule.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import little.goose.schedule.data.entities.Schedule
import little.goose.schedule.logic.DeleteSchedulesUseCase
import little.goose.schedule.logic.GetScheduleByIdFlowUseCase
import little.goose.schedule.logic.InsertScheduleUseCase
import little.goose.schedule.logic.UpdateScheduleUseCase
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ScheduleDialogViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getScheduleByIdFlowUseCase: GetScheduleByIdFlowUseCase,
    private val deleteSchedulesUseCase: DeleteSchedulesUseCase,
    private val insertScheduleUseCase: InsertScheduleUseCase,
    private val updateScheduleUseCase: UpdateScheduleUseCase
) : ViewModel() {

    private val _schedule = MutableStateFlow(Schedule())
    val schedule = _schedule.asStateFlow()

    init {
        savedStateHandle.get<Long>(KEY_SCHEDULE_ID)?.takeIf { it > 0 }?.let { scheduleId ->
            getScheduleByIdFlowUseCase(scheduleId).onEach {
                _schedule.value = it
            }.launchIn(viewModelScope)
        }
    }

    fun deleteSchedule() {
        val schedule = _schedule.value.takeIf { it.id != null } ?: return
        viewModelScope.launch(NonCancellable) {
            deleteSchedulesUseCase(listOf(schedule))
        }
    }

    fun insertSchedule() {
        val schedule = _schedule.value.takeIf { it.id == null } ?: return
        viewModelScope.launch(NonCancellable) {
            insertScheduleUseCase(schedule)
        }
    }

    fun updateSchedule() {
        val schedule = _schedule.value.takeIf { it.id != null } ?: return
        viewModelScope.launch(NonCancellable) {
            updateScheduleUseCase(schedule)
        }
    }

    fun updateScheduleTitle(title: String) {
        _schedule.value = _schedule.value.copy(title = title)
    }

    fun updateScheduleContent(content: String) {
        _schedule.value = _schedule.value.copy(content = content)
    }

    fun updateScheduleTime(time: Date) {
        _schedule.value = _schedule.value.copy(time = time)
    }

}