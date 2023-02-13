package little.goose.memorial.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import little.goose.memorial.data.constants.KEY_MEMORIAL
import little.goose.memorial.data.entities.Memorial
import javax.inject.Inject

@HiltViewModel
class MemorialShowViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _memorial = MutableStateFlow<Memorial>(savedStateHandle[KEY_MEMORIAL]!!)
    val memorial = _memorial.asStateFlow()

    fun updateMemorial(memorial: Memorial) {
        _memorial.value = memorial
    }

}