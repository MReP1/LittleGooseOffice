package little.goose.account.ui.transaction

import little.goose.account.data.models.IconDisplayType
import little.goose.account.logic.MoneyCalculatorLogic
import java.util.Date

sealed class TransactionScreenIntent {

    sealed class TransactionOperation : TransactionScreenIntent() {
        data object Done : TransactionOperation()
        data object Again : TransactionOperation()
        data class AppendEnd(val char: Char) : TransactionOperation()
        data class ModifyOther(val logic: MoneyCalculatorLogic) : TransactionOperation()
    }

    open class ChangeTransaction : TransactionScreenIntent() {
        open val type: Int? = null
        open val content: String? = null
        open val time: Date? = null
        open val iconId: Int? = null

        data class Time(
            override val time: Date
        ) : ChangeTransaction()

        data class Icon(
            override val type: Int,
            override val iconId: Int,
            override val content: String
        ) : ChangeTransaction()

        operator fun plus(other: ChangeTransaction): ChangeTransaction {
            val thisChanged = this
            return object : ChangeTransaction() {
                override val type: Int? = other.type ?: thisChanged.type
                override val content: String? = other.content ?: thisChanged.content
                override val time: Date? = other.time ?: thisChanged.time
                override val iconId: Int? = other.iconId ?: thisChanged.iconId
            }
        }
    }

    data class ChangeIconDisplayType(
        val iconDisplayType: IconDisplayType
    ) : TransactionScreenIntent()

    data class ChangeIsEditDescription(
        val isEditDescription: Boolean
    ) : TransactionScreenIntent()
}