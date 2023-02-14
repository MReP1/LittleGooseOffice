package little.goose.schedule.ui

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import little.goose.common.constants.*
import little.goose.common.localBroadcastManager
import little.goose.schedule.data.entities.Schedule
import little.goose.schedule.logic.ScheduleRepository
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ScheduleDialogViewModel @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
    private val savedStateHandle: SavedStateHandle,
    application: Application
) : AndroidViewModel(application) {

    enum class Event {
        Cancel,
        Dismiss,
        HideKeyboard,
        NoContent
    }

    private val schedule = savedStateHandle.getStateFlow(
        KEY_SCHEDULE,
        Schedule(time = savedStateHandle[KEY_TIME] ?: Date())
    )

    private val _event = MutableSharedFlow<Event>()
    val event = _event.asSharedFlow()

    private val _scheduleDialogState = MutableStateFlow(
        ScheduleDialogState(
            schedule = schedule.value,
            onTitleChange = { title ->
                savedStateHandle[KEY_SCHEDULE] = schedule.value.copy(title = title)
            },
            onContentChange = { content ->
                savedStateHandle[KEY_SCHEDULE] = schedule.value.copy(content = content)
            },
            onConfirmClick = ::confirm,
            onCancelClick = {
                viewModelScope.launch {
                    _event.emit(Event.Cancel)
                }
            }
        )
    )
    val scheduleDialogState = _scheduleDialogState.asStateFlow()

    init {
        viewModelScope.launch {
            schedule.collect { schedule ->
                _scheduleDialogState.value = scheduleDialogState.value.copy(schedule = schedule)
            }
        }
    }

    private fun confirm() {
        val schedule = schedule.value
        viewModelScope.launch(NonCancellable + Dispatchers.Main.immediate) {
            if (schedule.title.isBlank()) {
                _event.emit(Event.NoContent)
            } else {
                val isAdded = schedule.id == null
                if (isAdded) {
                    scheduleRepository.addSchedule(schedule)
                    _event.emit(Event.Dismiss)
                } else {
                    scheduleRepository.updateSchedule(schedule)
                    getApplication<Application>()
                        .localBroadcastManager.sendBroadcast(Intent(NOTIFY_UPDATE_SCHEDULE))
                    _event.emit(Event.Dismiss)
                }
            }
        }
    }

    fun deleteSchedule(schedule: Schedule = scheduleDialogState.value.schedule) {
        viewModelScope.launch(NonCancellable) {
            scheduleRepository.deleteSchedule(schedule)
            sendDeleteBroadcast()
        }
    }

    fun changeTime(time: Date) {
        savedStateHandle[KEY_SCHEDULE] = schedule.value.copy(time = time)
    }

    private fun sendDeleteBroadcast() {
        val intent = Intent(NOTIFY_DELETE_SCHEDULE).apply {
            setPackage(`package`)
            putExtra(KEY_DELETE_ITEM, schedule.value)
        }
        getApplication<Application>().localBroadcastManager.sendBroadcast(intent)
    }

}