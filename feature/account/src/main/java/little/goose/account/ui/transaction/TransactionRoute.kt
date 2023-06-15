package little.goose.account.ui.transaction

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.google.accompanist.navigation.animation.composable
import little.goose.account.ROUTE_GRAPH_ACCOUNT
import little.goose.common.constants.KEY_TIME
import java.util.Date

const val ROUTE_TRANSACTION = "transaction"

const val KEY_TRANSACTION_ID = "transaction_id"

const val DEEP_LINK_URI_PATTERN_TRANSACTION =
    "little-goose://office/$ROUTE_GRAPH_ACCOUNT/$ROUTE_TRANSACTION" +
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
        route = ROUTE_TRANSACTION +
                "?$KEY_TRANSACTION_ID={$KEY_TRANSACTION_ID}" +
                "?$KEY_TIME={$KEY_TIME}",
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
        TransactionScreen(
            modifier = Modifier.fillMaxSize(),
            onFinished = onBack
        )
    }
}