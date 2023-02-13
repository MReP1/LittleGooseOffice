@file:Suppress("NOTHING_TO_INLINE")
package little.goose.account.ui.analysis

import little.goose.account.data.constants.AccountConstant.EXPENSE
import little.goose.account.data.constants.AccountConstant.INCOME
import little.goose.account.ui.analysis.AnalysisFragmentViewModel.Companion.MONTH
import little.goose.account.ui.analysis.AnalysisFragmentViewModel.Companion.YEAR
import little.goose.account.utils.*
import little.goose.common.utils.*
import java.math.BigDecimal
import java.util.*
import kotlin.math.min

class AnalysisHelper {
    var mapExpensePercent: HashMap<Int, little.goose.account.data.models.TransactionPercent> = HashMap()
    var mapIncomePercent: HashMap<Int, little.goose.account.data.models.TransactionPercent> = HashMap()
    var mapBalance: HashMap<Int, little.goose.account.data.models.TransactionBalance> = HashMap()

    private val calendar = Calendar.getInstance()

    val timeExpenseList = ArrayList<little.goose.account.data.models.TimeMoney>()
    val timeIncomeList = ArrayList<little.goose.account.data.models.TimeMoney>()
    val timeBalanceList = ArrayList<little.goose.account.data.models.TimeMoney>()

    var expenseSum = BigDecimal(0)
    var incomeSum = BigDecimal(0)
    var balance = BigDecimal(0)

    fun analyseTransactionList(
        list: List<little.goose.account.data.entities.Transaction>,
        expenseSum: Double,
        incomeSum: Double,
        year: Int,
        month: Int,
        type: Int
    ) {
        mapExpensePercent.clear()
        mapIncomePercent.clear()
        mapBalance.clear()
        timeExpenseList.clear()
        timeIncomeList.clear()
        timeBalanceList.clear()

        when (type) {
            YEAR -> {
                initTimeListYear(year)
                dealWithListYear(list)
            }
            MONTH -> {
                initTimeListMonth(year, month)
                dealWithListMonth(list)
            }
        }

        //将balanceList的balance通过 expense + income 算出来
        for (value in mapExpensePercent) {
            value.value.also { transPercent ->
                transPercent.percent = transPercent.money.toDouble() / expenseSum
            }
        }
        for (value in mapIncomePercent) {
            value.value.also { transPercent ->
                transPercent.percent = transPercent.money.toDouble() / incomeSum
            }
        }
        for (value in mapBalance) {
            value.value.also { transactionBalance ->
                transactionBalance.balance =
                    transactionBalance.expense + transactionBalance.income
            }
        }
        this.expenseSum = BigDecimal(expenseSum).getRoundTwo()
        this.incomeSum = BigDecimal(incomeSum).getRoundTwo()
        this.balance = this.expenseSum + this.incomeSum

        //算出balanceTimeList的money
        for (index in 0 until min(timeExpenseList.size, timeIncomeList.size)) {
            timeBalanceList[index].money = timeBalanceList[index].money +
                    timeExpenseList[index].money + timeIncomeList[index].money
        }
    }

    private inline fun initTimeListMonth(year: Int, month: Int) {
        calendar.apply {
            clear()
            setYear(year)
            setMonth(month)
            val days = DateTimeUtils.getDaysByCalendar(calendar)
            for (day in 1..days) {
                setDate(day)
                val time = calendar.time
                timeExpenseList.add(little.goose.account.data.models.TimeMoney(time, BigDecimal(0)))
                timeIncomeList.add(little.goose.account.data.models.TimeMoney(time, BigDecimal(0)))
                timeBalanceList.add(little.goose.account.data.models.TimeMoney(time, BigDecimal(0)))
            }
        }
    }

    private inline fun initTimeListYear(year: Int) {
        calendar.apply {
            clear()
            setYear(year)
            for (month in 1..12) {
                setMonth(month)
                val time = calendar.time
                timeExpenseList.add(little.goose.account.data.models.TimeMoney(time, BigDecimal(0)))
                timeIncomeList.add(little.goose.account.data.models.TimeMoney(time, BigDecimal(0)))
                timeBalanceList.add(little.goose.account.data.models.TimeMoney(time, BigDecimal(0)))
            }
        }
    }

    private inline fun dealWithListYear(list: List<little.goose.account.data.entities.Transaction>) {
        for (value in list) {
            when (value.type) {
                EXPENSE -> {
                    //处理TransactionPercent
                    dealWithExpense(value)
                    //处理TransactionBalance
                    dealWithBalanceOfExpenseYear(value)
                }
                INCOME -> {
                    //处理TransactionPercent
                    dealWithIncome(value)
                    //处理TransactionBalance
                    dealWithBalanceOfIncomeYear(value)
                }
            }
        }
    }

    private inline fun dealWithListMonth(list: List<little.goose.account.data.entities.Transaction>) {
        for (value in list) {
            when (value.type) {
                EXPENSE -> {
                    //处理TransactionPercent
                    dealWithExpense(value)
                    //处理TransactionBalance
                    dealWithBalanceOfExpenseMonth(value)
                }
                INCOME -> {
                    //处理TransactionPercent
                    dealWithIncome(value)
                    //处理TransactionBalance
                    dealWithBalanceOfIncomeMonth(value)
                }
            }
        }
    }

    private inline fun dealWithBalanceOfExpenseMonth(value: little.goose.account.data.entities.Transaction) {
        val date = value.time.getRealDate()
        val transactionBalance = mapBalance[date]
        if (transactionBalance != null) {
            transactionBalance.expense += value.money
        } else {
            mapBalance[date] = little.goose.account.data.models.TransactionBalance(
                value.time, value.money, BigDecimal(0), BigDecimal(0)
            )
        }
        timeExpenseList[date - 1].money += value.money
    }

    private inline fun dealWithBalanceOfExpenseYear(value: little.goose.account.data.entities.Transaction) {
        val month = value.time.getRealMonth()
        val transactionBalance = mapBalance[month]
        if (transactionBalance != null) {
            transactionBalance.expense += value.money
        } else {
            mapBalance[month] = little.goose.account.data.models.TransactionBalance(
                value.time, value.money, BigDecimal(0), BigDecimal(0)
            )
        }
        timeExpenseList[month - 1].money += value.money
    }

    private inline fun dealWithBalanceOfIncomeMonth(value: little.goose.account.data.entities.Transaction) {
        val date = value.time.getRealDate()
        val transactionBalance = mapBalance[date]
        if (transactionBalance != null) {
            transactionBalance.income += value.money
        } else {
            mapBalance[date] = little.goose.account.data.models.TransactionBalance(
                value.time, BigDecimal(0), value.money, BigDecimal(0)
            )
        }
        timeIncomeList[date - 1].money += value.money
    }

    private inline fun dealWithBalanceOfIncomeYear(value: little.goose.account.data.entities.Transaction) {
        val month = value.time.getRealMonth()
        val transactionBalance = mapBalance[month]
        if (transactionBalance != null) {
            transactionBalance.income += value.money
        } else {
            mapBalance[month] = little.goose.account.data.models.TransactionBalance(
                value.time, BigDecimal(0), value.money, BigDecimal(0)
            )
        }
        timeIncomeList[month - 1].money += value.money
    }

    private inline fun dealWithExpense(value: little.goose.account.data.entities.Transaction) {
        val expensePercent = mapExpensePercent[value.icon_id]
        if (expensePercent != null) {
            expensePercent.money += value.money
        } else {
            mapExpensePercent[value.icon_id] =
                little.goose.account.data.models.TransactionPercent(
                    value.icon_id,
                    value.content,
                    value.money,
                    0.0
                )
        }
    }

    private inline fun dealWithIncome(value: little.goose.account.data.entities.Transaction) {
        val incomePercent = mapIncomePercent[value.icon_id]
        if (incomePercent != null) {
            incomePercent.money += value.money
        } else {
            mapIncomePercent[value.icon_id] =
                little.goose.account.data.models.TransactionPercent(
                    value.icon_id,
                    value.content,
                    value.money,
                    0.0
                )
        }
    }

}