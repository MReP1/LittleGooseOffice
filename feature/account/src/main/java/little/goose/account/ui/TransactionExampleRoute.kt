package little.goose.account.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DoneAll
import androidx.compose.material.icons.rounded.RemoveDone
import androidx.compose.material3.BottomAppBarDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import little.goose.account.data.constants.MoneyType
import little.goose.account.data.entities.Transaction
import little.goose.account.ui.component.TransactionColumn
import little.goose.account.ui.component.TransactionColumnState
import little.goose.common.constants.KEY_CONTENT
import little.goose.common.constants.KEY_ICON_ID
import little.goose.common.constants.KEY_MONEY_TYPE
import little.goose.common.constants.KEY_TIME
import little.goose.common.constants.KEY_TIME_TYPE
import little.goose.common.utils.TimeType
import little.goose.design.system.component.MovableActionButton
import little.goose.design.system.component.MovableActionButtonState
import little.goose.design.system.component.dialog.DeleteDialog
import little.goose.design.system.component.dialog.DeleteDialogState
import java.util.Date

const val ROUTE_TRANSACTION_EXAMPLE = "transaction_example"

internal class TransactionExampleRouteArgs private constructor(
    val time: Date,
    val timeType: TimeType,
    val moneyType: MoneyType,
    val keyContent: String?,
    val iconId: Int?
) {
    internal constructor(savedStateHandle: SavedStateHandle) : this(
        time = Date(savedStateHandle.get<Long>(KEY_TIME)!!),
        timeType = TimeType.valueOf(savedStateHandle[KEY_TIME_TYPE]!!),
        moneyType = MoneyType.valueOf(savedStateHandle[KEY_MONEY_TYPE]!!),
        keyContent = savedStateHandle[KEY_CONTENT],
        iconId = savedStateHandle.get<Int>(KEY_ICON_ID)?.takeIf { it != -1 }
    )
}

fun NavController.navigateToTransactionExample(
    time: Date,
    timeType: TimeType,
    moneyType: MoneyType = MoneyType.BALANCE,
    iconId: Int? = -1,
    keyContent: String? = null
) {
    navigate(
        route = ROUTE_TRANSACTION_EXAMPLE +
                "/$KEY_TIME=${time.time}" +
                "/$KEY_TIME_TYPE=$timeType" +
                "/$KEY_MONEY_TYPE=$moneyType" +
                "/$KEY_ICON_ID=${iconId ?: -1}" +
                if (keyContent != null) "?$KEY_CONTENT=$KEY_CONTENT" else "",
    ) {
        launchSingleTop = true
    }
}

fun NavGraphBuilder.transactionExampleRoute(
    onNavigateToTransactionScreen: (transactionId: Long) -> Unit,
    onBack: () -> Unit
) = composable(
    route = ROUTE_TRANSACTION_EXAMPLE +
            "/$KEY_TIME={$KEY_TIME}" +
            "/$KEY_TIME_TYPE={$KEY_TIME_TYPE}" +
            "/$KEY_MONEY_TYPE={$KEY_MONEY_TYPE}" +
            "/$KEY_ICON_ID={$KEY_ICON_ID}" +
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
        },
        navArgument(KEY_ICON_ID) {
            type = NavType.IntType
            defaultValue = -1
        }
    )
) {
    TransactionRoute(
        modifier = Modifier
            .fillMaxSize()
            .shadow(36.dp, clip = false),
        onNavigateToTransactionScreen = onNavigateToTransactionScreen,
        onBack = onBack
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TransactionRoute(
    modifier: Modifier,
    onNavigateToTransactionScreen: (transactionId: Long) -> Unit,
    onBack: () -> Unit
) {
    val viewModel = hiltViewModel<TransactionExampleViewModel>()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val transactionColumnState by viewModel.transactionColumnState.collectAsState()

    TransactionTimeScreen(
        modifier = modifier.fillMaxSize(),
        title = viewModel.title,
        onTransactionEdit = { transaction ->
            transaction.id?.run(onNavigateToTransactionScreen)
        },
        snackbarHostState = snackbarHostState,
        transactionColumnState = transactionColumnState,
        onBack = onBack
    )

    val deleteDialogState = remember { DeleteDialogState() }

    if (transactionColumnState.isMultiSelecting) {
        val buttonState = remember { MovableActionButtonState() }
        LaunchedEffect(buttonState) { buttonState.expend() }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(
                    if (WindowInsets.isImeVisible) {
                        WindowInsets.ime.union(BottomAppBarDefaults.windowInsets)
                    } else {
                        BottomAppBarDefaults.windowInsets
                    }
                )
        ) {
            MovableActionButton(
                modifier = Modifier.align(Alignment.BottomEnd),
                state = buttonState,
                needToExpand = true,
                mainButtonContent = {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = "Delete"
                    )
                },
                onMainButtonClick = {
                    deleteDialogState.show(onConfirm = {
                        transactionColumnState.deleteTransactions(
                            transactionColumnState.multiSelectedTransactions.toList()
                        )
                    })
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

    DeleteDialog(state = deleteDialogState)

    LaunchedEffect(viewModel.event) {
        viewModel.event.collect { event ->
            when (event) {
                is TransactionExampleViewModel.Event.DeleteTransactions -> {
                    snackbarHostState.showSnackbar(
                        message = context.getString(little.goose.common.R.string.deleted),
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
    onTransactionEdit: (Transaction) -> Unit,
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
            onTransactionEdit = onTransactionEdit
        )
    }
}