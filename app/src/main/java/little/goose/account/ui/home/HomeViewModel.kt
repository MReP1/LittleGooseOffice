package little.goose.account.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import little.goose.common.receiver.DeleteItemBroadcastReceiver
import little.goose.account.logic.AccountRepository
import little.goose.memorial.logic.MemorialRepository
import little.goose.memorial.data.entities.Memorial
import little.goose.account.logic.data.entities.Transaction
import little.goose.common.utils.getDate
import little.goose.common.utils.getMonth
import little.goose.common.utils.getYear
import little.goose.schedule.data.entities.Schedule
import little.goose.schedule.logic.ScheduleRepository
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val memorialRepository: MemorialRepository,
    private val scheduleRepository: ScheduleRepository
) : ViewModel() {

    var transactionListFlow: Flow<List<Transaction>>

    var scheduleListFlow: Flow<List<Schedule>>

    var memorialListFlow: Flow<List<Memorial>>

    fun updateOneDayTransactionListFlow(year: Int, month: Int, date: Int) {
        transactionListFlow = AccountRepository.getTransactionByDateFlow(year, month, date)
    }

    fun updateOneDayScheduleListFlow(year: Int, month: Int, date: Int) {
        scheduleListFlow = scheduleRepository.getScheduleByDateFlow(year, month, date)
    }

    fun updateOneDayMemorialListFLow(year: Int, month: Int, date: Int) {
        memorialListFlow = memorialRepository.getMemorialsByDateFlow(year, month, date)
    }

    val transactionDeleteReceiver = DeleteItemBroadcastReceiver<Transaction>()
    val scheduleDeleteReceiver = DeleteItemBroadcastReceiver<Schedule>()
    var memorialDeleteReceiver = DeleteItemBroadcastReceiver<Memorial>()

    fun addMemorial(memorial: Memorial) {
        viewModelScope.launch(Dispatchers.IO) {
            memorialRepository.addMemorial(memorial)
        }
    }

    fun addSchedule(schedule: Schedule) {
        viewModelScope.launch(Dispatchers.IO) {
            scheduleRepository.addSchedule(schedule)
        }
    }

    fun updateSchedule(schedule: Schedule) {
        viewModelScope.launch(Dispatchers.IO) {
            scheduleRepository.updateSchedule(schedule)
        }
    }

    suspend fun getScheduleByYearMonth(year: Int, month: Int): List<Schedule> {
        return scheduleRepository.getScheduleByYearMonth(year, month)
    }

    suspend fun getMemorialsByYearMonth(year: Int, month: Int): List<Memorial> {
        return memorialRepository.getMemorialsByYearMonth(year, month)
    }

    init {
        Calendar.getInstance().apply {
            val year = getYear()
            val month = getMonth()
            val date = getDate()
            transactionListFlow = AccountRepository.getTransactionByDateFlow(year, month, date)
            scheduleListFlow = scheduleRepository.getScheduleByDateFlow(year, month, date)
            memorialListFlow = memorialRepository.getMemorialsByDateFlow(year, month, date)
        }
    }
}