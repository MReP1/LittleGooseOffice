package little.goose.account.ui.search

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import little.goose.account.common.receiver.DeleteItemBroadcastReceiver
import little.goose.account.common.receiver.NormalBroadcastReceiver
import little.goose.account.logic.AccountRepository
import little.goose.account.logic.MemorialRepository
import little.goose.account.logic.NoteRepository
import little.goose.account.logic.ScheduleRepository
import little.goose.account.logic.data.entities.Memorial
import little.goose.account.logic.data.entities.Note
import little.goose.account.logic.data.entities.Schedule
import little.goose.account.logic.data.entities.Transaction
import middle.goose.richtext.HtmlParser

class SearchActivityViewModel : ViewModel() {

    var type = 0

    var transactionDeleteReceiver: DeleteItemBroadcastReceiver<Transaction>? = null
    var scheduleDeleteReceiver: DeleteItemBroadcastReceiver<Schedule>? = null
    var memorialDeleteReceiver: DeleteItemBroadcastReceiver<Memorial>? = null

    var scheduleUpdateReceiver: NormalBroadcastReceiver? = null

    suspend fun searchTransactionList(keyWord: String): List<Transaction> {
        return keyWord.toDoubleOrNull()?.let {
            AccountRepository.searchTransactionByMoney(keyWord)
        } ?: run {
            AccountRepository.searchTransactionByText(keyWord)
        }
    }

    suspend fun searchScheduleList(keyWord: String): List<Schedule> {
        return ScheduleRepository.searchScheduleByText(keyWord)
    }

    suspend fun searchNoteList(keyWord: String): List<Note> {
        return withContext(Dispatchers.IO) {
            NoteRepository.getAllNote().filter {
                HtmlParser.fromHtml(it.content)?.contains(keyWord) == true
                        || it.title.contains(keyWord)
            }
        }
    }

    suspend fun searchMemorialList(keyWord: String): List<Memorial> {
        return MemorialRepository.searchMemorialByText(keyWord)
    }

}