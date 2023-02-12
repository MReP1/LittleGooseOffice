package little.goose.account.ui.search
//
//import androidx.lifecycle.ViewModel
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//import little.goose.common.receiver.DeleteItemBroadcastReceiver
//import little.goose.common.receiver.NormalBroadcastReceiver
//import little.goose.account.logic.AccountRepository
//import little.goose.memorial.logic.MemorialRepository
//import little.goose.account.logic.NoteRepository
//import little.goose.schedule.logic.ScheduleRepository
//import little.goose.memorial.data.entities.Memorial
//import little.goose.account.logic.data.entities.Note
//import little.goose.schedule.data.entities.Schedule
//import little.goose.account.logic.data.entities.Transaction
//import middle.goose.richtext.HtmlParser
//
//class SearchActivityViewModel : ViewModel() {
//
//    var type = 0
//
//    val transactionDeleteReceiver = DeleteItemBroadcastReceiver<Transaction>()
//    val scheduleDeleteReceiver = DeleteItemBroadcastReceiver<Schedule>()
//    val memorialDeleteReceiver = DeleteItemBroadcastReceiver<Memorial>()
//
//    val scheduleUpdateReceiver: NormalBroadcastReceiver = NormalBroadcastReceiver()
//
//    suspend fun searchTransactionList(keyWord: String): List<Transaction> {
//        return keyWord.toDoubleOrNull()?.let {
//            AccountRepository.searchTransactionByMoney(keyWord)
//        } ?: run {
//            AccountRepository.searchTransactionByText(keyWord)
//        }
//    }
//
//    suspend fun searchScheduleList(keyWord: String): List<Schedule> {
//        return ScheduleRepository.searchScheduleByText(keyWord)
//    }
//
//    suspend fun searchNoteList(keyWord: String): List<Note> {
//        return withContext(Dispatchers.IO) {
//            NoteRepository.getAllNote().filter {
//                HtmlParser.fromHtml(it.content)?.contains(keyWord) == true
//                        || it.title.contains(keyWord)
//            }
//        }
//    }
//
//    suspend fun searchMemorialList(keyWord: String): List<Memorial> {
//        return MemorialRepository.searchMemorialByText(keyWord)
//    }
//
//}