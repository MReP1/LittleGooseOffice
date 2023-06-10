package little.goose.account

import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import little.goose.account.data.constants.MoneyType
import little.goose.account.ui.analysis.accountAnalysisRoute
import little.goose.account.ui.transaction.ROUTE_TRANSACTION
import little.goose.account.ui.transaction.transactionRoute
import little.goose.account.ui.transactionExampleRoute
import little.goose.common.utils.TimeType
import java.util.Date

const val ROUTE_GRAPH_ACCOUNT = "account"

fun NavGraphBuilder.accountGraph(
    onNavigateToTransaction: (id: Long?, time: Date?) -> Unit,
    onNavigateToTransactionExample: (
        time: Date, timeType: TimeType, moneyType: MoneyType, content: String?
    ) -> Unit,
    onBack: () -> Unit
) = navigation(
    startDestination = ROUTE_TRANSACTION,
    route = ROUTE_GRAPH_ACCOUNT
) {
    transactionRoute(onBack)
    transactionExampleRoute(
        onNavigateToTransaction = onNavigateToTransaction,
        onBack = onBack
    )
    accountAnalysisRoute(
        onNavigateToTransactionExample = onNavigateToTransactionExample,
        onBack = onBack
    )
}