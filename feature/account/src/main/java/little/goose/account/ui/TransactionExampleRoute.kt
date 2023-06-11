package little.goose.account.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DoneAll
import androidx.compose.material.icons.rounded.RemoveDone
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.composable
import little.goose.account.data.constants.MoneyType
import little.goose.account.data.entities.Transaction
import little.goose.account.ui.component.TransactionColumn
import little.goose.account.ui.component.TransactionColumnState
import little.goose.common.constants.KEY_CONTENT
import little.goose.common.constants.KEY_MONEY_TYPE
import little.goose.common.constants.KEY_TIME
import little.goose.common.constants.KEY_TIME_TYPE
import little.goose.common.utils.TimeType
import little.goose.design.system.component.MovableActionButton
import little.goose.design.system.component.MovableActionButtonState
import java.util.Date

const val ROUTE_TRANSACTION_EXAMPLE = "transaction_example"

internal class TransactionExampleRouteArgs private constructor(
    val time: Date,
    val timeType: TimeType,
    val moneyType: MoneyType,
    val keyContent: String?
) {
    internal constructor(savedStateHandle: SavedStateHandle) : this(
        time = Date(savedStateHandle.get<Long>(KEY_TIME)!!),
        timeType = TimeType.valueOf(savedStateHandle[KEY_TIME_TYPE]!!),
        moneyType = MoneyType.valueOf(savedStateHandle[KEY_MONEY_TYPE]!!),
        keyContent = savedStateHandle[KEY_CONTENT]
    )
}

fun NavController.navigateToTransactionExample(
    time: Date,
    timeType: TimeType,
    moneyType: MoneyType = MoneyType.BALANCE,
    keyContent: String? = null
) {
    navigate(
        route = ROUTE_TRANSACTION_EXAMPLE +
                "/$KEY_TIME=${time.time}" +
                "/$KEY_TIME_TYPE=$timeType" +
                "/$KEY_MONEY_TYPE=$moneyType" +
                if (keyContent != null) "?$KEY_CONTENT=$KEY_CONTENT" else "",
    ) {
        launchSingleTop = true
    }
}

fun NavGraphBuilder.transactionExampleRoute(
    onNavigateToTransactionDialog: (transactionId: Long) -> Unit,
    onBack: () -> Unit
) = composable(
    route = ROUTE_TRANSACTION_EXAMPLE +
            "/$KEY_TIME={$KEY_TIME}" +
            "/$KEY_TIME_TYPE={$KEY_TIME_TYPE}" +
            "/$KEY_MONEY_TYPE={$KEY_MONEY_TYPE}" +
            "?$KEY_CONTENT={$KEY_CONTENT}",
    arguments = listOf(
        navArgument(KEY_TIME) {
            type = NavType.LongType
        },
        navArgument(KEY_TIME_TYPE) {
            type = NavType.StringType
            defaultValue = TimeType.DATE.toString()
        },
        navArgument(KEY_MONEY_TYPE) {
            type = NavType.StringType
            defaultValue = MoneyType.BALANCE.toString()
        },
        navArgument(KEY_CONTENT) {
            type = NavType.StringType
            nullable = true
        }
    )
) {
    TransactionRoute(
        modifier = Modifier.fillMaxSize(),
        onNavigateToTransactionDialog = onNavigateToTransactionDialog,
        onBack = onBack
    )
}

@Composable
private fun TransactionRoute(
    modifier: Modifier,
    onNavigateToTransactionDialog: (transactionId: Long) -> Unit,
    onBack: () -> Unit
) {
    val viewModel = hiltViewModel<TransactionExampleViewModel>()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val transactionColumnState by viewModel.transactionColumnState.collectAsState()

    TransactionTimeScreen(
        modifier = modifier.fillMaxSize(),
        title = viewModel.title,
        onTransactionClick = { transaction ->
            transaction.id?.run(onNavigateToTransactionDialog)
        },
        snackbarHostState = snackbarHostState,
        transactionColumnState = transactionColumnState,
        onBack = onBack
    )

    if (transactionColumnState.isMultiSelecting) {
        val buttonState = remember { MovableActionButtonState() }
        LaunchedEffect(buttonState) { buttonState.expend() }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
        ) {
            MovableActionButton(
                modifier = Modifier.align(Alignment.BottomEnd),
                state = buttonState,
                mainButtonContent = {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = "Delete"
                    )
                },
                onMainButtonClick = {
                    transactionColumnState.deleteTransactions(
                        transactionColumnState.multiSelectedTransactions.toList()
                    )
                },
                topSubButtonContent = {
                    Icon(
                        imageVector = Icons.Rounded.DoneAll,
                        contentDescription = "SelectAll"
                    )
                },
                onTopSubButtonClick = {
                    transactionColumnState.selectAllTransactions()
                },
                bottomSubButtonContent = {
                    Icon(
                        imageVector = Icons.Rounded.RemoveDone,
                        contentDescription = "RemoveDone"
                    )
                },
                onBottomSubButtonClick = {
                    transactionColumnState.cancelMultiSelecting()
                }
            )
        }
    }

    LaunchedEffect(viewModel.event) {
        viewModel.event.collect { event ->
            when (event) {
                is TransactionExampleViewModel.Event.DeleteTransactions -> {
                    snackbarHostState.showSnackbar(
                        message = context.getString(little.goose.account.R.string.deleted),
                        withDismissAction = true,
                    )
                }
            }
        }
    }
}

@Composable
private fun TransactionTimeScreen(
    modifier: Modifier = Modifier,
    title: String,
    transactionColumnState: TransactionColumnState,
    snackbarHostState: SnackbarHostState,
    onTransactionClick: (Transaction) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                modifier = Modifier,
                title = { Text(text = title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = ""
                        )
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { snackbarData ->
                Snackbar(snackbarData)
            }
        }
    ) { paddingValues ->
        TransactionColumn(
            modifier = Modifier.padding(paddingValues),
            state = transactionColumnState,
            onTransactionClick = onTransactionClick
        )
    }
}