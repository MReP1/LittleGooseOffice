package little.goose.memorial.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import little.goose.memorial.data.entities.Memorial
import little.goose.memorial.logic.MemorialRepository
import little.goose.memorial.ui.component.MemorialColumnState
import javax.inject.Inject

@HiltViewModel
class MemorialViewModel @Inject constructor(
    private val memorialRepository: MemorialRepository
) : ViewModel() {

    private val multiSelectedMemorials = MutableStateFlow<Set<Memorial>>(emptySet())

    private val memorials = memorialRepository.getAllMemorialFlow().stateIn(
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

    val topMemorial: StateFlow<Memorial?> = memorialRepository.getMemorialAtTopFlow()
        .map { it.firstOrNull() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )

    fun deleteMemorial(memorial: Memorial) {
        viewModelScope.launch {
            memorialRepository.deleteMemorial(memorial)
        }
    }

    private fun deleteMemorials(memorials: List<Memorial>) {
        viewModelScope.launch {
            memorialRepository.deleteMemorials(memorials)
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