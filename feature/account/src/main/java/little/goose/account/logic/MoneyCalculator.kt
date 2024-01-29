package little.goose.account.logic

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.math.BigDecimal

sealed class MoneyCalculatorLogic {
    data object DOT : MoneyCalculatorLogic()
    data object BACKSPACE : MoneyCalculatorLogic()

    sealed class Operator : MoneyCalculatorLogic() {
        data object PLUS : Operator()
        data object SUB : Operator()
        data object RESULT : Operator()
    }
}

class MoneyCalculator {

    private val moneySb = StringBuilder("0")

    private val _money = MutableStateFlow("0")
    val money: StateFlow<String> = _money

    private val _isContainOperator = MutableStateFlow(false)
    val isContainOperator = _isContainOperator.asStateFlow()

    fun appendMoneyEnd(num: Char) {
        if (moneySb.toString() != "0") {
            //如果不为0
            if (moneySb.contains('.')) {
                //如果有小数点
                val dotIndex = moneySb.lastIndexOf('.')
                if (moneySb.lastIndex != dotIndex + 2) {
                    //如果不在小数点后面两位
                    moneySb.append(num)
                } else {
                    if (moneySb.indexOfOperation() > dotIndex) {
                        //有一位小数，同时有操作符
                        moneySb.append(num)
                    }
                    //如果为操作符号
                    if (num.isOperation()) {
                        moneySb.append(num)
                    }
                }
            } else {
                //如果没小数点
                if (!num.isOperation() || !moneySb.last().isOperation()) {
                    //如果是操作符同时，最后一位是操作符
                    moneySb.append(num)
                }
            }
        } else {
            //如果为0
            if (num.isOperation() || num == '.') {
                //如果为操作符
                moneySb.append(num)
            } else {
                moneySb[0] = num
            }
        }
        updateData()
        _isContainOperator.value = moneySb.containsOperation()
    }

    fun modifyOther(logic: MoneyCalculatorLogic) {
        when (logic) {
            MoneyCalculatorLogic.BACKSPACE -> {
                backSpace()
                _isContainOperator.value = moneySb.containsOperation()
            }

            MoneyCalculatorLogic.DOT -> {
                if (moneySb.contains('.')) {
                    val indexOfDot = moneySb.indexOf('.')  //第一个小数点
                    if (moneySb.containsOperation()  //存在运算符
                        && indexOfDot == moneySb.lastIndexOf('.') //唯一一个小数点
                        && indexOfDot < moneySb.indexOfOperation() //这个小数点在运算符前面
                    ) {
                        appendMoneyEnd('.')
                    }
                } else {
                    appendMoneyEnd('.')
                }
            }

            MoneyCalculatorLogic.Operator.PLUS -> {
                preOperate(MoneyCalculatorLogic.Operator.PLUS)
            }

            MoneyCalculatorLogic.Operator.SUB -> {
                preOperate(MoneyCalculatorLogic.Operator.SUB)
            }

            MoneyCalculatorLogic.Operator.RESULT -> {
                operate()
                _isContainOperator.value = moneySb.containsOperation()
            }
        }
    }

    private fun preOperate(operate: MoneyCalculatorLogic.Operator) {
        if (moneySb.containsOperation()) {
            //包含运算符
            operate(operate)
        } else {
            //不包含运算符
            if (moneySb.last() == '.') {
                backSpace()
            }
            when (operate) {
                MoneyCalculatorLogic.Operator.PLUS -> appendMoneyEnd('+')
                MoneyCalculatorLogic.Operator.SUB -> appendMoneyEnd('-')
                MoneyCalculatorLogic.Operator.RESULT -> operate()
            }
        }
    }

    private fun backSpace() {
        moneySb.deleteCharAt(moneySb.lastIndex)
        if (moneySb.isEmpty() || moneySb.toString() == "-") {
            moneyCleanAndSet(BigDecimal(0))
        } else {
            updateData()
        }
    }

    fun operate() {
        if (moneySb.last() == '.') {
            backSpace()
        }
        var plusIndex = moneySb.indexOf('+')
        if (plusIndex == moneySb.lastIndex) {
            backSpace()
            plusIndex = -1
        }
        if (plusIndex > 0) {
            val preNum = BigDecimal(moneySb.substring(0, plusIndex))
            val endNum = BigDecimal(moneySb.substring(plusIndex + 1, moneySb.length))
            val sum = preNum + endNum
            moneyCleanAndSet(sum)
        }
        var subIndex = moneySb.lastIndexOf('-')
        if (subIndex == moneySb.lastIndex) {
            backSpace()
            subIndex = -1
        }
        if (subIndex > 0) {
            val preNum = BigDecimal(moneySb.substring(0, subIndex))
            val endNum = BigDecimal(moneySb.substring(subIndex + 1, moneySb.length))
            val sub = preNum - endNum
            moneyCleanAndSet(sub)
        }
    }

    private fun operate(operate: MoneyCalculatorLogic.Operator) {
        when (operate) {
            MoneyCalculatorLogic.Operator.PLUS -> {
                operate('+')
            }

            MoneyCalculatorLogic.Operator.SUB -> {
                operate('-')
            }

            MoneyCalculatorLogic.Operator.RESULT -> {
                operate()
            }
        }
    }

    private fun operate(append: Char) {
        operate()
        appendMoneyEnd(append)
        _isContainOperator.value = moneySb.containsOperation()
    }

    private fun moneyCleanAndSet(text: BigDecimal) {
        moneySb.clear()
        moneySb.append(text)
        updateData()
    }

    //字符是否操作符
    private fun Char.isOperation() = this == '+' || this == '-'

    //SB是否含有操作符
    private fun StringBuilder.containsOperation() = this.contains('+') || this.lastIndexOf('-') > 0

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

    private fun updateData() {
        _money.value = moneySb.toString()
    }

    fun setMoney(money: BigDecimal) {
        if (BigDecimal(this.money.value) == money) {
            return
        }
        moneyCleanAndSet(money)
    }
}