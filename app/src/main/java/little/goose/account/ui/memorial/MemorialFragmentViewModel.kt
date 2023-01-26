package little.goose.account.ui.memorial

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import little.goose.account.common.receiver.DeleteItemBroadcastReceiver
import little.goose.account.logic.MemorialRepository
import little.goose.account.logic.data.entities.Memorial

class MemorialFragmentViewModel : ViewModel() {

    val allMemorialFlow = MemorialRepository.getAllMemorialFlow()

    val topMemorial: StateFlow<Memorial?> = MemorialRepository.getMemorialAtTop()
        .map { it.firstOrNull() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = null
        )

    val deleteReceiver = DeleteItemBroadcastReceiver<Memorial>()
}