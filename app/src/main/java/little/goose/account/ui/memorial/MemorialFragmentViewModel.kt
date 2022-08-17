package little.goose.account.ui.memorial

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.launch
import little.goose.account.common.receiver.DeleteItemBroadcastReceiver
import little.goose.account.logic.MemorialRepository
import little.goose.account.logic.data.entities.Memorial
import little.goose.account.superScope

class MemorialFragmentViewModel : ViewModel() {
    val allMemorialFlow = MemorialRepository.getAllMemorialFlow()

    var deleteReceiver: DeleteItemBroadcastReceiver<Memorial>? = null
}