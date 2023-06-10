package little.goose.account.ui.analysis

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable
import little.goose.account.data.constants.MoneyType
import little.goose.common.utils.TimeType
import java.util.Date

const val ROUTE_ACCOUNT_ANALYSIS = "account_analysis"

fun NavController.navigateToAccountAnalysis() {
    navigate(ROUTE_ACCOUNT_ANALYSIS)
}

fun NavGraphBuilder.accountAnalysisRoute(
    onNavigateToTransactionExample: (
        time: Date, timeType: TimeType, moneyType: MoneyType, content: String?
    ) -> Unit,
    onBack: () -> Unit
) = composable(ROUTE_ACCOUNT_ANALYSIS) {
    AccountAnalysisRoute(
        modifier = Modifier,
        onNavigateToTransactionExample = onNavigateToTransactionExample,
        onBack = onBack
    )
}

@Composable
fun AccountAnalysisRoute(
    modifier: Modifier,
    onNavigateToTransactionExample: (
        time: Date, timeType: TimeType, moneyType: MoneyType, content: String?
    ) -> Unit,
    onBack: () -> Unit
) {
    val viewModel = hiltViewModel<TransactionAnalysisViewModel>()
    val topBarState by viewModel.topBarState.collectAsStateWithLifecycle()
    val contentState by viewModel.contentState.collectAsStateWithLifecycle()
    val bottomBarState by viewModel.bottomBarState.collectAsStateWithLifecycle()
    val timeSelectorState = viewModel.timeSelectorState
    val lifecycle = LocalLifecycleOwner.current

    DisposableEffect(Unit) {
        val observer = object : DefaultLifecycleObserver {
            var isFirstTime = true
            override fun onStart(owner: LifecycleOwner) {
                if (!isFirstTime) {
                    viewModel.updateData()
                } else {
                    isFirstTime = false
                }
            }
        }
        lifecycle.lifecycle.addObserver(observer)
        onDispose { lifecycle.lifecycle.removeObserver(observer) }
    }

    TransactionAnalysisScreen(
        modifier = modifier,
        topBarState = topBarState,
        contentState = contentState,
        bottomBarState = bottomBarState,
        timeSelectorState = timeSelectorState,
        onBack = onBack,
        onNavigateToTransactionExample = onNavigateToTransactionExample
    )
}