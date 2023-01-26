package little.goose.account.ui.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.stateIn
import little.goose.account.common.receiver.DeleteItemBroadcastReceiver
import little.goose.account.common.receiver.NormalBroadcastReceiver
import little.goose.account.logic.ScheduleRepository
import little.goose.account.logic.data.entities.Schedule

class ScheduleViewModel : ViewModel() {

    suspend fun getAllScheduleState() =
        ScheduleRepository.getAllScheduleFlow().stateIn(viewModelScope)

    var deleteReceiver = DeleteItemBroadcastReceiver<Schedule>()
    var updateReceiver: NormalBroadcastReceiver = NormalBroadcastReceiver()

}