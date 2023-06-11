package little.goose.search.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import little.goose.schedule.data.entities.Schedule
import little.goose.schedule.logic.DeleteSchedulesUseCase
import little.goose.schedule.logic.InsertScheduleUseCase
import little.goose.schedule.logic.SearchScheduleByTextFlowUseCase
import little.goose.schedule.logic.UpdateScheduleUseCase
import little.goose.schedule.ui.ScheduleColumnState
import javax.inject.Inject

@HiltViewModel
class SearchScheduleViewModel @Inject constructor(
    private val insertScheduleUseCase: InsertScheduleUseCase,
    private val updateScheduleUseCase: UpdateScheduleUseCase,
    private val deleteSchedulesUseCase: DeleteSchedulesUseCase,
    private val searchScheduleByTextFlowUseCase: SearchScheduleByTextFlowUseCase
) : ViewModel() {

    private val _searchScheduleState = MutableStateFlow<SearchScheduleState>(
        SearchScheduleState.Empty(::search)
    )
    val searchScheduleState = _searchScheduleState.asStateFlow()

    private val _searchNoteEvent = MutableSharedFlow<SearchScheduleEvent>()
    val searchScheduleEvent = _searchNoteEvent.asSharedFlow()

    private var searchingJob: Job? = null

    private val multiSelectedSchedules = MutableStateFlow(emptySet<Schedule>())

    init {
        deleteSchedulesUseCase.deleteSchedulesEvent.onEach {
            _searchNoteEvent.emit(SearchScheduleEvent.DeleteSchedules(it))
        }.launchIn(viewModelScope)
    }

    fun search(keyword: String) {
        if (keyword.isBlank()) {
            _searchScheduleState.value = SearchScheduleState.Empty(::search)
            return
        }
        _searchScheduleState.value = SearchScheduleState.Loading(::search)
        searchingJob?.cancel()
        combine(
            searchScheduleByTextFlowUseCase(keyword),
            multiSelectedSchedules
        ) { schedules, multiSelectedSchedules ->
            _searchScheduleState.value = if (schedules.isEmpty()) {
                SearchScheduleState.Empty(::search)
            } else {
                SearchScheduleState.Success(
                    data = ScheduleColumnState(
                        schedules = schedules,
                        multiSelectedSchedules = multiSelectedSchedules,
                        isMultiSelecting = multiSelectedSchedules.isNotEmpty(),
                        onSelectSchedule = ::selectSchedule,
                        selectAllSchedules = ::selectAllSchedule,
                        deleteSchedules = ::deleteSchedules,
                        onCheckedChange = ::checkSchedule,
                        cancelMultiSelecting = ::cancelSchedulesMultiSelecting
                    ),
                    addSchedule = ::addSchedule,
                    modifySchedule = ::updateSchedule,
                    search = ::search
                )
            }
        }.launchIn(viewModelScope)
    }


    private fun selectSchedule(schedule: Schedule, selected: Boolean) {
        multiSelectedSchedules.value = multiSelectedSchedules.value.toMutableSet().apply {
            if (selected) add(schedule) else remove(schedule)
        }
    }

    private fun checkSchedule(schedule: Schedule, checked: Boolean) {
        viewModelScope.launch {
            updateScheduleUseCase(schedule.copy(isfinish = checked))
        }
    }

    private fun addSchedule(schedule: Schedule) {
        viewModelScope.launch {
            insertScheduleUseCase(schedule)
        }
    }

    private fun updateSchedule(schedule: Schedule) {
        viewModelScope.launch {
            updateScheduleUseCase(schedule)
        }
    }

    private fun deleteSchedules(schedules: List<Schedule>) {
        viewModelScope.launch {
            deleteSchedulesUseCase(schedules)
        }
    }

    private fun selectAllSchedule() {
        multiSelectedSchedules.value = (_searchScheduleState.value as? SearchScheduleState.Success)
            ?.data?.schedules?.toSet() ?: return
    }

    private fun cancelSchedulesMultiSelecting() {
        multiSelectedSchedules.value = emptySet()
    }
}