package little.goose.account.ui.transaction

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import little.goose.account.R
import little.goose.account.ROUTE_GRAPH_ACCOUNT
import little.goose.common.constants.DEEP_LINK_THEME_AND_HOST
import little.goose.common.constants.KEY_TIME
import java.util.Date

const val ROUTE_TRANSACTION = "transaction"

const val KEY_TRANSACTION_ID = "transaction_id"

const val FULL_ROUTE_TRANSACTION = ROUTE_TRANSACTION +
        "?$KEY_TRANSACTION_ID={$KEY_TRANSACTION_ID}" +
        "?$KEY_TIME={$KEY_TIME}"

private const val DEEP_LINK_URI_PATTERN_TRANSACTION =
    "$DEEP_LINK_THEME_AND_HOST/$ROUTE_GRAPH_ACCOUNT/$ROUTE_TRANSACTION" +
            "?${KEY_TRANSACTION_ID}={$KEY_TRANSACTION_ID}" +
            "?${KEY_TIME}={$KEY_TIME}"

internal class TransactionRouteArgs private constructor(
    val transactionId: Long? = null,
    val time: Long? = null
) {
    internal constructor(savedStateHandle: SavedStateHandle) : this(
        transactionId = savedStateHandle.get<Long>(KEY_TRANSACTION_ID)?.takeIf { it > 0 },
        time = if (savedStateHandle.get<Long>(KEY_TIME)?.takeIf { it > 0 } != null) {
            savedStateHandle.get<Long>(KEY_TIME)
        } else if (savedStateHandle.get<Long>(KEY_TRANSACTION_ID)?.takeIf { it > 0 } == null) {
            Date().time
        } else null
    )
}

fun NavController.navigateToTransaction(time: Date) {
    navigate("$ROUTE_TRANSACTION?$KEY_TRANSACTION_ID=0?$KEY_TIME=${time.time}") {
        launchSingleTop = true
    }
}

fun NavController.navigateToTransaction(id: Long) {
    navigate("$ROUTE_TRANSACTION?$KEY_TRANSACTION_ID=${id}?$KEY_TIME=0") {
        launchSingleTop = true
    }
}

internal fun NavGraphBuilder.transactionRoute(onBack: () -> Unit) {
    composable(
        route = FULL_ROUTE_TRANSACTION,
        deepLinks = listOf(
            navDeepLink {
                uriPattern = DEEP_LINK_URI_PATTERN_TRANSACTION
            }
        ),
        arguments = listOf(
            navArgument(KEY_TRANSACTION_ID) {
                type = NavType.LongType
                defaultValue = 0
            },
            navArgument(KEY_TIME) {
                type = NavType.LongType
                defaultValue = 0
            }
        )
    ) {
        TransactionRoute(
            modifier = Modifier
                .fillMaxSize()
                .shadow(36.dp, clip = false),
            onBack = onBack
        )
    }
}

@Composable
fun TransactionRoute(
    modifier: Modifier,
    onBack: () -> Unit
) {
    val viewModel: TransactionViewModel = hiltViewModel()
    val transaction by viewModel.transaction.collectAsStateWithLifecycle()
    val iconDisplayType by viewModel.iconDisplayType.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    TransactionScreen(
        modifier = modifier,
        transaction = transaction,
        snackbarHostState = snackbarHostState,
        onFinished = viewModel::writeDatabase,
        onTransactionChange = viewModel::setTransaction,
        onIconDisplayTypeChange = viewModel::setIconDisplayType,
        iconDisplayType = iconDisplayType,
        onBack = onBack
    )

    val context = LocalContext.current
    LaunchedEffect(viewModel.event) {
        viewModel.event.collect { event ->
            when (event) {
                TransactionViewModel.Event.WriteSuccess -> {
                    onBack()
                }

                TransactionViewModel.Event.CantBeZero -> {
                    snackbarHostState.showSnackbar(
                        message = context.getString(R.string.money_cant_be_zero),
                        withDismissAction = true,
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }
}