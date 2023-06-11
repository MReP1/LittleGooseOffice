package little.goose.account

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import little.goose.account.logic.AccountRepository
import little.goose.account.logic.DeleteTransactionUseCase
import little.goose.account.logic.DeleteTransactionsUseCase
import little.goose.account.logic.GetAllTransactionExpenseSumFlowUseCase
import little.goose.account.logic.GetAllTransactionFlowUseCase
import little.goose.account.logic.GetAllTransactionIncomeSumFlowUseCase
import little.goose.account.logic.GetExpenseSumFlowByYearMonthUseCase
import little.goose.account.logic.GetExpenseSumFlowByYearUseCase
import little.goose.account.logic.GetIncomeSumFlowByYearMonthUseCase
import little.goose.account.logic.GetIncomeSumFlowByYearUseCase
import little.goose.account.logic.GetTransactionByDateFlowUseCase
import little.goose.account.logic.GetTransactionByIdFlowUseCase
import little.goose.account.logic.GetTransactionByYearFlowWithKeyContentUseCase
import little.goose.account.logic.GetTransactionByYearMonthFlowUseCase
import little.goose.account.logic.GetTransactionByYearMonthFlowWithKeyContentUseCase
import little.goose.account.logic.GetTransactionsFlowByYearAndMonthUseCase
import little.goose.account.logic.GetTransactionsFlowByYearUseCase
import little.goose.account.logic.InsertTransactionUseCase
import little.goose.account.logic.SearchTransactionByMoneyFlowUseCase
import little.goose.account.logic.SearchTransactionByTextFlowUseCase
import little.goose.account.logic.UpdateTransactionUseCase
import little.goose.account.ui.analysis.AnalysisHelper
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AccountModule {

    @Provides
    @Singleton
    fun provideAccountRepository(application: Application): AccountRepository {
        return AccountRepository(application)
    }

}

@Module
@InstallIn(ViewModelComponent::class)
class AccountViewModelModule {

    @Provides
    @ViewModelScoped
    fun provideAnalysisHelper(
        getTransactionsFlowByYearUseCase: GetTransactionsFlowByYearUseCase,
        getExpenseSumFlowByYearUseCase: GetExpenseSumFlowByYearUseCase,
        getIncomeSumFlowByYearUseCase: GetIncomeSumFlowByYearUseCase,
        getTransactionsFlowByYearAndMonthUseCase: GetTransactionsFlowByYearAndMonthUseCase,
        getExpenseSumFlowByYearMonthUseCase: GetExpenseSumFlowByYearMonthUseCase,
        getIncomeSumFlowByYearMonthUseCase: GetIncomeSumFlowByYearMonthUseCase
    ): AnalysisHelper {
        return AnalysisHelper(
            getTransactionsFlowByYearUseCase,
            getExpenseSumFlowByYearUseCase,
            getIncomeSumFlowByYearUseCase,
            getTransactionsFlowByYearAndMonthUseCase,
            getExpenseSumFlowByYearMonthUseCase,
            getIncomeSumFlowByYearMonthUseCase
        )
    }

    @Provides
    @ViewModelScoped
    fun provideInsertTransactionUseCase(
        accountRepository: AccountRepository
    ): InsertTransactionUseCase {
        return InsertTransactionUseCase(accountRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetTransactionByIdFlowUseCase(
        accountRepository: AccountRepository
    ): GetTransactionByIdFlowUseCase {
        return GetTransactionByIdFlowUseCase(accountRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideUpdateTransactionUseCase(
        accountRepository: AccountRepository
    ): UpdateTransactionUseCase {
        return UpdateTransactionUseCase(accountRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideDeleteTransactionUseCase(
        accountRepository: AccountRepository
    ): DeleteTransactionUseCase {
        return DeleteTransactionUseCase(accountRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideDeleteTransactionsUseCase(
        accountRepository: AccountRepository
    ): DeleteTransactionsUseCase {
        return DeleteTransactionsUseCase(accountRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetTransactionsByYearAndMonthUseCase(
        accountRepository: AccountRepository
    ): GetTransactionsFlowByYearAndMonthUseCase {
        return GetTransactionsFlowByYearAndMonthUseCase(accountRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetExpenseSumByYearMonthUseCase(
        accountRepository: AccountRepository
    ): GetExpenseSumFlowByYearMonthUseCase {
        return GetExpenseSumFlowByYearMonthUseCase(accountRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetIncomeSumByYearMonthUseCase(
        accountRepository: AccountRepository
    ): GetIncomeSumFlowByYearMonthUseCase {
        return GetIncomeSumFlowByYearMonthUseCase(accountRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetTransactionByYearUseCase(
        accountRepository: AccountRepository
    ): GetTransactionsFlowByYearUseCase {
        return GetTransactionsFlowByYearUseCase(accountRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetExpenseSumByYearUseCase(
        accountRepository: AccountRepository
    ): GetExpenseSumFlowByYearUseCase {
        return GetExpenseSumFlowByYearUseCase(accountRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetIncomeSumByYearUseCase(
        accountRepository: AccountRepository
    ): GetIncomeSumFlowByYearUseCase {
        return GetIncomeSumFlowByYearUseCase(accountRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetTransactionByDateFlowUseCase(
        accountRepository: AccountRepository
    ): GetTransactionByDateFlowUseCase {
        return GetTransactionByDateFlowUseCase(accountRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetTransactionByYearMonthFlowUseCase(
        accountRepository: AccountRepository
    ): GetTransactionByYearMonthFlowUseCase {
        return GetTransactionByYearMonthFlowUseCase(accountRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetTransactionByYearFlowWithKeyContentUseCase(
        accountRepository: AccountRepository
    ): GetTransactionByYearFlowWithKeyContentUseCase {
        return GetTransactionByYearFlowWithKeyContentUseCase(accountRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetTransactionByYearMonthFlowWithKeyContentUseCase(
        accountRepository: AccountRepository
    ): GetTransactionByYearMonthFlowWithKeyContentUseCase {
        return GetTransactionByYearMonthFlowWithKeyContentUseCase(accountRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetAllTransactionExpenseSumFlowUseCase(
        accountRepository: AccountRepository
    ): GetAllTransactionExpenseSumFlowUseCase {
        return GetAllTransactionExpenseSumFlowUseCase(accountRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetAllTransactionIncomeSumFlowUseCase(
        accountRepository: AccountRepository
    ): GetAllTransactionIncomeSumFlowUseCase {
        return GetAllTransactionIncomeSumFlowUseCase(accountRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetAllTransactionFlowUseCase(
        accountRepository: AccountRepository
    ): GetAllTransactionFlowUseCase {
        return GetAllTransactionFlowUseCase(accountRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideSearchTransactionByMoneyFlowUseCase(
        accountRepository: AccountRepository
    ): SearchTransactionByMoneyFlowUseCase {
        return SearchTransactionByMoneyFlowUseCase(accountRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideSearchTransactionByTextFlowUseCase(
        accountRepository: AccountRepository
    ): SearchTransactionByTextFlowUseCase {
        return SearchTransactionByTextFlowUseCase(accountRepository)
    }

}