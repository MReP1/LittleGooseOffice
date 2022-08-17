package little.goose.account.common

import little.goose.account.appContext
import little.goose.account.ui.home.widget.AccountMonthView
import little.goose.account.ui.home.widget.AccountWeekView

class WeakQuote {
    val monthView get() = AccountMonthView(appContext)
    val weekView get() = AccountWeekView(appContext)
}

val dummyFun: () -> Unit = {}
