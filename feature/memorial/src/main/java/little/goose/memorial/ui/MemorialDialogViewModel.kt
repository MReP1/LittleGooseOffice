package little.goose.memorial.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import little.goose.memorial.data.constants.KEY_MEMORIAL_ID
import little.goose.memorial.data.entities.Memorial
import little.goose.memorial.logic.DeleteMemorialsUseCase
import little.goose.memorial.logic.GetMemorialFlowUseCase
import javax.inject.Inject

@HiltViewModel
class MemorialDialogViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getMemorialFlowUseCase: GetMemorialFlowUseCase,
    private val deleteMemorialsUseCase: DeleteMemorialsUseCase
) : ViewModel() {

    val memorial = getMemorialFlowUseCase(
        savedStateHandle.get<Long>(KEY_MEMORIAL_ID)!!
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = Memorial()
    )

    fun deleteMemorial() {
        val memorial = memorial.value.takeIf { it.id != null } ?: return
        viewModelScope.launch(NonCancellable) {
            deleteMemorialsUseCase(listOf(memorial))
        }
    }

}