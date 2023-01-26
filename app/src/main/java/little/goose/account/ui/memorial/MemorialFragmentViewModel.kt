package little.goose.account.ui.memorial

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import little.goose.account.common.receiver.DeleteItemBroadcastReceiver
import little.goose.account.logic.MemorialRepository
import little.goose.account.logic.data.entities.Memorial

class MemorialFragmentViewModel : ViewModel() {

    val memorials = MemorialRepository.getAllMemorialFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = listOf()
    )

    val topMemorial: StateFlow<Memorial?> = MemorialRepository.getMemorialAtTopFlow()
        .map { it.firstOrNull() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )

    val deleteReceiver = DeleteItemBroadcastReceiver<Memorial>()
}