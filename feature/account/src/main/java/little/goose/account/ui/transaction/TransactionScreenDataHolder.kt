package little.goose.account.ui.transaction

import little.goose.account.data.constants.AccountConstant.EXPENSE
import little.goose.account.data.constants.AccountConstant.INCOME
import little.goose.account.data.entities.Transaction
import little.goose.account.data.models.TransactionIcon
import little.goose.account.ui.transaction.icon.TransactionIconHelper
import java.math.BigDecimal
import java.util.Date

data class TransactionScreenDataHolder(
    val id: Long?,
    val expenseIcon: TransactionIcon,
    val incomeIcon: TransactionIcon,
    val content: String,
    val type: Int,
    val time: Date,
    val isEditDescription: Boolean = false
) {
    companion object {
        fun fromTransaction(transaction: Transaction): TransactionScreenDataHolder {
            return TransactionScreenDataHolder(
                id = transaction.id,
                expenseIcon = TransactionIconHelper.getTransactionIconOrDefault(
                    EXPENSE, transaction.icon_id
                ),
                incomeIcon = TransactionIconHelper.getTransactionIconOrDefault(
                    INCOME, transaction.icon_id
                ),
                content = transaction.content,
                type = transaction.type,
                time = transaction.time
            )
        }
    }

    fun toTransaction(money: BigDecimal, description: String): Transaction {
        return Transaction(
            id = id,
            type = type,
            money = money,
            content = content,
            description = description,
            time = time,
            icon_id = if (type == EXPENSE) expenseIcon.id else incomeIcon.id
        )
    }
}
