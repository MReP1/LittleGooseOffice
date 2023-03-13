package little.goose.home.data

import little.goose.account.data.entities.Transaction
import little.goose.memorial.data.entities.Memorial
import little.goose.note.data.entities.Note
import little.goose.schedule.data.entities.Schedule
import java.math.BigDecimal

data class CalendarModel(
    val notes: List<Note> = listOf(),
    val memorials: List<Memorial> = listOf(),
    val schedules: List<Schedule> = listOf(),
    val transactions: List<Transaction> = listOf(),
    val expense: BigDecimal = BigDecimal(0),
    val income: BigDecimal = BigDecimal(0),
    val balance: BigDecimal = expense + income
) {
    val containSomething get() = memorials.isNotEmpty() || schedules.isNotEmpty() || notes.isNotEmpty()
}