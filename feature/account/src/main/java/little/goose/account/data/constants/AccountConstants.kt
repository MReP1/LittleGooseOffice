package little.goose.account.data.constants

const val TABLE_TRANSACTION = "transactions"

object AccountConstant {
    const val EXPENSE = 0 //消费
    const val INCOME = 1 //收入
    const val TIME = 2 //时间
}

enum class MoneyType {
    BALANCE, EXPENSE, INCOME
}