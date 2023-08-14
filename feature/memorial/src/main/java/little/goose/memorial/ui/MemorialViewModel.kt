package little.goose.memorial.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import little.goose.memorial.data.entities.Memorial
import little.goose.memorial.logic.DeleteMemorialsEventUseCase
import little.goose.memorial.logic.DeleteMemorialsUseCase
import little.goose.memorial.logic.GetAllMemorialFlowUseCase
import little.goose.memorial.logic.GetMemorialAtTopFlowUseCase
import little.goose.memorial.ui.component.MemorialColumnState
import javax.inject.Inject

@HiltViewModel
class MemorialViewModel @Inject constructor(
    getAllMemorialFlowUseCase: GetAllMemorialFlowUseCase,
    getMemorialAtTopFlowUseCase: GetMemorialAtTopFlowUseCase,
    private val deleteMemorialsUseCase: DeleteMemorialsUseCase,
    private val deleteMemorialsEventUseCase: DeleteMemorialsEventUseCase
) : ViewModel() {

    sealed class Event {
        data class DeleteMemorials(val memorials: List<Memorial>) : Event()
    }

    private val _event = MutableSharedFlow<Event>()
    val event = _event.asSharedFlow()

    private val multiSelectedMemorials = MutableStateFlow<Set<Memorial>>(emptySet())

    private val memorials = getAllMemorialFlowUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = listOf()
    )

    val memorialColumnState = combine(
        multiSelectedMemorials, memorials
    ) { multiSelectedMemorials, memorials ->
        MemorialColumnState(
            memorials = memorials,
            isMultiSelecting = multiSelectedMemorials.isNotEmpty(),
            multiSelectedMemorials = multiSelectedMemorials,
            deleteMemorials = ::deleteMemorials,
            onSelectMemorial = ::selectMemorial,
            selectAllMemorial = ::selectAllMemorials,
            cancelMultiSelecting = ::cancelMemorialsMultiSelecting
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MemorialColumnState(
            memorials = memorials.value,
            isMultiSelecting = multiSelectedMemorials.value.isNotEmpty(),
            multiSelectedMemorials = multiSelectedMemorials.value,
            deleteMemorials = ::deleteMemorials,
            onSelectMemorial = ::selectMemorial,
            selectAllMemorial = ::selectAllMemorials,
            cancelMultiSelecting = ::cancelMemorialsMultiSelecting
        )
    )

    val topMemorial: StateFlow<Memorial?> = getMemorialAtTopFlowUseCase()
        .map { it.firstOrNull() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )

    init {
        deleteMemorialsEventUseCase().onEach {
            _event.emit(Event.DeleteMemorials(it))
        }.launchIn(viewModelScope)
    }

    private fun deleteMemorials(memorials: List<Memorial>) {
        viewModelScope.launch {
            deleteMemorialsUseCase(memorials)
            cancelMemorialsMultiSelecting()
        }
    }

    private fun selectMemorial(memorial: Memorial, selected: Boolean) {
        multiSelectedMemorials.value = multiSelectedMemorials.value.toMutableSet().apply {
            if (selected) add(memorial) else remove(memorial)
        }
    }

    private fun selectAllMemorials() {
        multiSelectedMemorials.value = memorials.value.toSet()
    }

    private fun cancelMemorialsMultiSelecting() {
        multiSelectedMemorials.value = emptySet()
    }

}