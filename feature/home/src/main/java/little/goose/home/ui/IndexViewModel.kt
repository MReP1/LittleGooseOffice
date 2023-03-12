package little.goose.home.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import little.goose.account.logic.AccountRepository
import little.goose.memorial.logic.MemorialRepository
import little.goose.note.logic.NoteRepository
import little.goose.schedule.logic.ScheduleRepository
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class IndexViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val scheduleRepository: ScheduleRepository,
    private val noteRepository: NoteRepository,
    private val memorialRepository: MemorialRepository
) : ViewModel() {

    private val firstVisibleMonth = MutableStateFlow(YearMonth.now())
    private val lastVisibleMonth = MutableStateFlow(YearMonth.now().plusMonths(1))

    fun updateTime(
        firstVisibleMonth: YearMonth,
        lastVisibleMonth: YearMonth
    ) {
        this.firstVisibleMonth.value = firstVisibleMonth
        this.lastVisibleMonth.value = lastVisibleMonth
    }

}