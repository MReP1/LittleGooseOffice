package little.goose.account.logic

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import little.goose.account.data.constants.MoneyType
import little.goose.account.data.entities.Transaction
import java.math.BigDecimal

class InsertTransactionUseCase(
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(transaction: Transaction) {
        accountRepository.insertTransaction(transaction)
    }
}

class GetTransactionByIdFlowUseCase(
    private val accountRepository: AccountRepository
) {
    operator fun invoke(id: Long): Flow<Transaction> {
        return accountRepository.getTransactionByIdFlow(id)
    }
}

class UpdateTransactionUseCase(
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(transaction: Transaction) {
        accountRepository.updateTransaction(transaction)
    }
}

class DeleteTransactionsUseCase(
    private val accountRepository: AccountRepository
) {
    private val _deleteTransactionEvent = MutableSharedFlow<List<Transaction>>()
    val deleteTransactionEvent = _deleteTransactionEvent.asSharedFlow()

    suspend operator fun invoke(transactions: List<Transaction>) {
        if (transactions.size == 1) {
            accountRepository.deleteTransaction(transactions[0])
        } else {
            accountRepository.deleteTransactions(transactions)
        }
        _deleteTransactionEvent.emit(transactions)
    }
}

class DeleteTransactionsEventUseCase(
    private val accountRepository: AccountRepository
) {
    operator fun invoke(): SharedFlow<List<Transaction>> {
        return accountRepository.deleteTransactionsEvent
    }
}

class GetTransactionsFlowByYearAndMonthUseCase(
    private val accountRepository: AccountRepository
) {
    operator fun invoke(year: Int, month: Int): Flow<List<Transaction>> {
        return accountRepository.getTransactionsFlowByYearAndMonth(year, month)
    }
}

class GetExpenseSumFlowByYearMonthUseCase(
    private val accountRepository: AccountRepository
) {
    operator fun invoke(year: Int, month: Int): Flow<Double> {
        return accountRepository.getExpenseSumFlowByYearMonth(year, month)
    }
}

class GetIncomeSumFlowByYearMonthUseCase(
    private val accountRepository: AccountRepository
) {
    operator fun invoke(year: Int, month: Int): Flow<Double> {
        return accountRepository.getIncomeSumFlowByYearMonth(year, month)
    }
}

class GetTransactionsFlowByYearUseCase(
    private val accountRepository: AccountRepository
) {
    operator fun invoke(year: Int): Flow<List<Transaction>> {
        return accountRepository.getTransactionByYearFlow(year)
    }
}

class GetExpenseSumFlowByYearUseCase(
    private val accountRepository: AccountRepository
) {
    operator fun invoke(year: Int): Flow<Double> {
        return accountRepository.getExpenseSumFlowByYear(year)
    }
}

class GetIncomeSumFlowByYearUseCase(
    private val accountRepository: AccountRepository
) {
    operator fun invoke(year: Int): Flow<Double> {
        return accountRepository.getIncomeSumFlowByYear(year)
    }
}

class GetTransactionByDateFlowUseCase(
    private val accountRepository: AccountRepository
) {
    operator fun invoke(
        year: Int, month: Int, date: Int, moneyType: MoneyType = MoneyType.BALANCE
    ): Flow<List<Transaction>> {
        return accountRepository.getTransactionByDateFlow(year, month, date, moneyType)
    }
}

class GetTransactionByYearMonthFlowUseCase(
    private val accountRepository: AccountRepository
) {
    operator fun invoke(
        year: Int, month: Int, moneyType: MoneyType = MoneyType.BALANCE
    ): Flow<List<Transaction>> {
        return accountRepository.getTransactionByYearMonthFlow(year, month, moneyType)
    }
}

class GetTransactionByYearFlowWithKeyContentUseCase(
    private val accountRepository: AccountRepository
) {
    operator fun invoke(
        year: Int, keyContent: String
    ): Flow<List<Transaction>> {
        return accountRepository.getTransactionByYearFlowWithKeyContent(year, keyContent)
    }
}

class GetTransactionByYearMonthFlowWithKeyContentUseCase(
    private val accountRepository: AccountRepository
) {
    operator fun invoke(
        year: Int, month: Int, keyContent: String
    ): Flow<List<Transaction>> {
        return accountRepository.getTransactionByYearMonthFlowWithKeyContent(
            year,
            month,
            keyContent
        )
    }
}

class GetAllTransactionExpenseSumFlowUseCase(
    private val accountRepository: AccountRepository
) {
    operator fun invoke(): Flow<BigDecimal> {
        return accountRepository.getAllTransactionExpenseSumFlow()
    }
}

class GetAllTransactionIncomeSumFlowUseCase(
    private val accountRepository: AccountRepository
) {
    operator fun invoke(): Flow<BigDecimal> {
        return accountRepository.getAllTransactionIncomeSumFlow()
    }
}

class GetAllTransactionFlowUseCase(
    private val accountRepository: AccountRepository
) {
    operator fun invoke(): Flow<List<Transaction>> {
        return accountRepository.getAllTransactionFlow()
    }
}

class SearchTransactionByMoneyFlowUseCase(
    private val accountRepository: AccountRepository
) {
    operator fun invoke(money: String): Flow<List<Transaction>> {
        return accountRepository.searchTransactionByMoneyFlow(money)
    }
}

class SearchTransactionByTextFlowUseCase(
    private val accountRepository: AccountRepository
) {
    operator fun invoke(text: String): Flow<List<Transaction>> {
        return accountRepository.searchTransactionByTextFlow(text)
    }
}