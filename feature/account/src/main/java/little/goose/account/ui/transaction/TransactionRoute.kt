package little.goose.account.ui.transaction

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.composable
import little.goose.common.constants.KEY_TIME
import java.util.Date

const val ROUTE_TRANSACTION = "transaction"

const val KEY_TRANSACTION_ID = "transaction_id"

class TransactionRouteArgs private constructor(
    val transactionId: Long? = null,
    val time: Long? = null
) {
    internal constructor(savedStateHandle: SavedStateHandle) : this(
        transactionId = savedStateHandle.get<Long>(KEY_TRANSACTION_ID)?.takeIf { it > 0 },
        time = savedStateHandle.get<Long>(KEY_TIME)?.takeIf { it > 0 }
    )
}

fun NavController.navigateToTransaction(time: Date) {
    navigate("$ROUTE_TRANSACTION/$KEY_TRANSACTION_ID=0/$KEY_TIME=${time.time}") {
        launchSingleTop = true
    }
}

fun NavController.navigateToTransaction(id: Long) {
    navigate("$ROUTE_TRANSACTION/$KEY_TRANSACTION_ID=${id}/$KEY_TIME=0") {
        launchSingleTop = true
    }
}

internal fun NavGraphBuilder.transactionRoute(onBack: () -> Unit) {
    composable(
        route = ROUTE_TRANSACTION +
                "/$KEY_TRANSACTION_ID={$KEY_TRANSACTION_ID}" +
                "/$KEY_TIME={$KEY_TIME}",
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