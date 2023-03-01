package little.goose.account.ui.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import little.goose.account.data.constants.AccountConstant.EXPENSE
import little.goose.account.data.constants.AccountConstant.INCOME
import little.goose.account.data.entities.Transaction
import little.goose.account.logic.AccountRepository
import little.goose.account.ui.transaction.icon.TransactionIconHelper
import little.goose.account.utils.getAbs
import little.goose.account.utils.getNegative
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val accountRepository: AccountRepository
) : ViewModel() {

    private val moneyCalculator = MoneyCalculator(BigDecimal(0))

    private val _moneyStateFlow = MutableStateFlow("0")
    val moneyStateFlow: StateFlow<String> = _moneyStateFlow

    private val _expenseIconStateFlow = MutableStateFlow(1)
    val expenseIconStateFlow: StateFlow<Int> = _expenseIconStateFlow
    private val _incomeIconStateFlow = MutableStateFlow(11)
    val incomeIconStateFlow: StateFlow<Int> = _incomeIconStateFlow

    var position = 0 //用来定位在哪个页面，帮助加入数据库的时候使用哪个Transaction
    var type = ADD

    private val money = StringBuilder("0")

    var listTransaction: List<Transaction> = listOf(
        Transaction(
            null, EXPENSE, BigDecimal("0"),
            TransactionIconHelper.getIconName(1), "", Date(), 1
        ),
        Transaction(
            null, INCOME, BigDecimal("0"),
            TransactionIconHelper.getIconName(11), "", Date(), 11
        )
    )

    fun appendMoneyEnd(num: Char) = moneyCalculator.appendMoneyEnd(num)

    fun modifyOther(logic: MoneyCalculatorLogic) = moneyCalculator.modifyOther(logic)

    fun done() {

    }

    fun again() {

    }

    //最后将数字1.00，1.0之类的后面无意义的小数去掉，只在数据中去掉，视图不显示。
    fun filtrateZero(money: String): String {
        val lastIndex = money.lastIndex
        if (money.length >= 3) {
            if (money[lastIndex] == '0' && money[lastIndex - 1] == '0' && money[lastIndex - 2] == '.') {
                return (money.substring(0, lastIndex - 2))
            } else if (money[lastIndex] == '0' && money[lastIndex - 1] == '.') {
                return (money.substring(0, lastIndex - 1))
            }
        }
        return money
    }

    fun updateDatabase(action: (() -> Unit)? = null) {
        viewModelScope.launch(NonCancellable) {
            when (type) {
                ADD -> {
                    when (position) {
                        EXPENSE -> accountRepository.insertTransaction(
                            listTransaction[position].getNegative()
                        )
                        INCOME -> accountRepository.insertTransaction(
                            listTransaction[position].getAbs()
                        )
                    }
                }
                EDIT -> {
                    when (position) {
                        EXPENSE -> accountRepository.updateTransaction(
                            listTransaction[position].getNegative()
                        )
                        INCOME -> accountRepository.updateTransaction(
                            listTransaction[position].getAbs()
                        )
                    }
                }
            }
            launch(Dispatchers.Main.immediate) {
                action?.invoke()
            }
        }
    }

    private var buttonCallback: ButtonCallback? = null

    fun setCallback(buttonCallback: ButtonCallback) {
        this.buttonCallback = buttonCallback
    }

    interface ButtonCallback {
        fun doneCallback()
        fun allButtonCallback()
        fun againCallback(isZero: Boolean)
    }

    fun setIconSelectedId(iconId: Int, type: Int) {
        when (type) {
            EXPENSE -> _expenseIconStateFlow.value = iconId
            INCOME -> _incomeIconStateFlow.value = iconId
        }
    }

    companion object {
        const val DOT = 0
        const val PLUS = 1
        const val SUB = 2
        const val BACKSPACE = 3
        const val AGAIN = 4
        const val DONE = 5

        const val ADD = 0
        const val EDIT = 1
    }

}