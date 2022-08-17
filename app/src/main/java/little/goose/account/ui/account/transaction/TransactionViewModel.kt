package little.goose.account.ui.account.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import little.goose.account.logic.AccountRepository
import little.goose.account.logic.data.constant.AccountConstant.EXPENSE
import little.goose.account.logic.data.constant.AccountConstant.INCOME
import little.goose.account.logic.data.entities.Transaction
import little.goose.account.ui.account.transaction.icon.TransactionIconHelper
import little.goose.account.utils.getAbs
import little.goose.account.utils.getNegative
import java.math.BigDecimal
import java.util.*

class TransactionViewModel : ViewModel() {

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

    fun appendMoneyEnd(num: Char) {
        if (money.toString() != "0") {
            //如果不为0
            if (money.contains('.')) {
                //如果有小数点
                val dotIndex = money.lastIndexOf('.')
                if (money.lastIndex != dotIndex + 2) {
                    //如果不在小数点后面两位
                    money.append(num)
                } else {
                    if (money.indexOfOperation() > dotIndex) {
                        //有一位小数，同时有操作符
                        money.append(num)
                    }
                    //如果为操作符号
                    if (num.isOperation()) {
                        money.append(num)
                    }
                }
            } else {
                //如果没小数点
                if (!num.isOperation() || !money.last().isOperation()) {
                    //如果是操作符同时，最后一位是操作符
                    money.append(num)
                }
            }
        } else {
            //如果为0
            if (num.isOperation() || num == '.') {
                //如果为操作符
                money.append(num)
            } else {
                money[0] = num
            }
        }
        updateData()
        buttonCallback?.allButtonCallback()
    }

    fun modifyOther(type: Int) {
        when (type) {
            DOT -> {
                if (money.contains('.')) {
                    val indexOfDot = money.indexOf('.')  //第一个小数点
                    if (money.containsOperation()  //存在运算符
                        && indexOfDot == money.lastIndexOf('.') //唯一一个小数点
                        && indexOfDot < money.indexOfOperation() //这个小数点在运算符前面
                    ) {
                        appendMoneyEnd('.')
                    }
                } else {
                    appendMoneyEnd('.')
                }
            }
            PLUS -> preOperate(PLUS)
            SUB -> preOperate(SUB)
            BACKSPACE -> {
                backSpace()
                buttonCallback?.allButtonCallback()
            }
            AGAIN -> {
                resetData()
            }
            DONE -> {
                done()
            }
        }
    }

    private fun preOperate(operate: Int) {
        if (money.containsOperation()) {
            //包含运算符
            operation(operate)
        } else {
            //不包含运算符
            if (money.last() == '.') {
                backSpace()
            }
            when (operate) {
                PLUS -> appendMoneyEnd('+')
                SUB -> appendMoneyEnd('-')
            }
        }
    }

    private fun operation(operate: Int) {
        when (operate) {
            PLUS -> {
                done('+')
            }
            SUB -> {
                done('-')
            }
        }
    }

    private fun done(append: Char? = null) {
        operate()
        append?.let {
            appendMoneyEnd(it)
        } ?: run {
            buttonCallback?.doneCallback()
        }
        buttonCallback?.allButtonCallback()
    }

    private fun resetData() {
        viewModelScope.launch(Dispatchers.Main) {
            operate()
            buttonCallback?.allButtonCallback()
            if (listTransaction[0].money == BigDecimal(0)) {
                buttonCallback?.againCallback(true)
            } else {
                val time = listTransaction.firstOrNull()?.time ?: Date()
                updateDatabase()
                moneyCleanAndSet("0")
                listTransaction = listOf(
                    Transaction(
                        null, EXPENSE, BigDecimal("0"),
                        TransactionIconHelper.getIconName(1), "", time, 1
                    ),
                    Transaction(
                        null, INCOME, BigDecimal("0"),
                        TransactionIconHelper.getIconName(11), "", time, 11
                    )
                )
                type = ADD
                buttonCallback?.againCallback(false)
            }
        }
    }

    private fun operate() {
        if (money.last() == '.') {
            backSpace()
        }
        var plusIndex = money.indexOf('+')
        if (plusIndex == money.lastIndex) {
            backSpace()
            plusIndex = -1
        }
        if (plusIndex > 0) {
            val preNum = BigDecimal(money.substring(0, plusIndex))
            val endNum = BigDecimal(money.substring(plusIndex + 1, money.length))
            val sum = preNum + endNum
            moneyCleanAndSet(sum)
        }
        var subIndex = money.lastIndexOf('-')
        if (subIndex == money.lastIndex) {
            backSpace()
            subIndex = -1
        }
        if (subIndex > 0) {
            val preNum = BigDecimal(money.substring(0, subIndex))
            val endNum = BigDecimal(money.substring(subIndex + 1, money.length))
            val sub = preNum - endNum
            moneyCleanAndSet(sub)
        }
    }

    fun moneyCleanAndSet(text: BigDecimal) {
        money.clear()
        money.append(text)
        updateData()
    }

    fun moneyCleanAndSet(text: String) {
        money.clear()
        money.append(text)
        updateData()
    }

    private fun backSpace() {
        money.deleteCharAt(money.lastIndex)
        if (money.isEmpty() || money.toString() == "-") {
            moneyCleanAndSet("0")
        } else {
            updateData()
        }
    }

    private fun updateData() {
        _moneyStateFlow.value = money.toString()
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

    //字符是否操作符
    private fun Char.isOperation() = this == '+' || this == '-'

    //SB是否含有操作符
    private fun StringBuilder.containsOperation() = this.contains('+') || this.lastIndexOf('-') > 0
    fun isContainsOperation() = money.containsOperation()

    //SB的操作符位置
    private fun StringBuilder.indexOfOperation(): Int {
        if (this.contains('+')) {
            return this.indexOf('+')
        } else {
            val indexOfSub = this.lastIndexOf('-')
            if (indexOfSub > 0) {
                return indexOfSub
            }
        }
        return -1
    }

    suspend fun updateDatabase() {
        withContext(Dispatchers.IO) {
            when (type) {
                ADD -> {
                    when (position) {
                        EXPENSE -> AccountRepository.addTransaction(
                            listTransaction[position].getNegative()
                        )
                        INCOME -> AccountRepository.addTransaction(
                            listTransaction[position].getAbs()
                        )
                    }
                }
                EDIT -> {
                    when (position) {
                        EXPENSE -> AccountRepository.updateTransaction(
                            listTransaction[position].getNegative()
                        )
                        INCOME -> AccountRepository.updateTransaction(
                            listTransaction[position].getAbs()
                        )
                    }
                }
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