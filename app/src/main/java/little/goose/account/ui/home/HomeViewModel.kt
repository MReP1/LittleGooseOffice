package little.goose.account.ui.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import little.goose.common.receiver.DeleteItemBroadcastReceiver
import little.goose.account.logic.AccountRepository
import little.goose.memorial.logic.MemorialRepository
import little.goose.account.logic.ScheduleRepository
import little.goose.memorial.data.entities.Memorial
import little.goose.account.logic.data.entities.Schedule
import little.goose.account.logic.data.entities.Transaction
import little.goose.account.utils.getDate
import little.goose.account.utils.getMonth
import little.goose.account.utils.getYear
import java.util.*

class HomeViewModel : ViewModel() {

    var transactionListFlow: Flow<List<Transaction>>

    var scheduleListFlow: Flow<List<Schedule>>

    var memorialListFlow: Flow<List<Memorial>>

    fun updateOneDayTransactionListFlow(year: Int, month: Int, date: Int) {
        transactionListFlow = AccountRepository.getTransactionByDateFlow(year, month, date)
    }

    fun updateOneDayScheduleListFlow(year: Int, month: Int, date: Int) {
        scheduleListFlow = ScheduleRepository.getScheduleByDateFlow(year, month, date)
    }

    fun updateOneDayMemorialListFLow(year: Int, month: Int, date: Int) {
        memorialListFlow = MemorialRepository.getMemorialsByDateFlow(year, month, date)
    }

    val transactionDeleteReceiver = DeleteItemBroadcastReceiver<Transaction>()
    val scheduleDeleteReceiver = DeleteItemBroadcastReceiver<Schedule>()
    var memorialDeleteReceiver = DeleteItemBroadcastReceiver<Memorial>()

    init {
        Calendar.getInstance().apply {
            val year = getYear()
            val month = getMonth()
            val date = getDate()
            transactionListFlow = AccountRepository.getTransactionByDateFlow(year, month, date)
            scheduleListFlow = ScheduleRepository.getScheduleByDateFlow(year, month, date)
            memorialListFlow = MemorialRepository.getMemorialsByDateFlow(year, month, date)
        }
    }
}