package little.goose.memorial.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import little.goose.memorial.data.entities.Memorial
import little.goose.memorial.logic.MemorialRepository
import javax.inject.Inject

@HiltViewModel
class MemorialViewModel @Inject constructor(
    private val memorialRepository: MemorialRepository
) : ViewModel() {

    val memorials = memorialRepository.getAllMemorialFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = listOf()
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

}