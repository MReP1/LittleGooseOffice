package little.goose.account.ui.transaction

import little.goose.account.data.entities.Transaction
import little.goose.account.data.models.IconDisplayType

sealed class TransactionScreenIntent {
    data class Done(val transaction: Transaction) : TransactionScreenIntent()
    data class Again(val transaction: Transaction) : TransactionScreenIntent()
    data class ChangeTransaction(val transaction: Transaction) : TransactionScreenIntent()
    data class ChangeIconDisplayType(val iconDisplayType: IconDisplayType) : TransactionScreenIntent()
}