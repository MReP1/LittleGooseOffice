package little.goose.memorial.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import little.goose.memorial.data.constants.KEY_MEMORIAL_ID
import little.goose.memorial.data.entities.Memorial
import little.goose.memorial.logic.GetMemorialFlowUseCase
import javax.inject.Inject

@HiltViewModel
class MemorialShowViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getMemorialFlowUseCase: GetMemorialFlowUseCase
) : ViewModel() {

    val memorial = getMemorialFlowUseCase(savedStateHandle[KEY_MEMORIAL_ID]!!).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        Memorial(content = "", isTop = false)
    )

}