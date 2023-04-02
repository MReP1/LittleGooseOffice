package little.goose.account.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DoneAll
import androidx.compose.material.icons.rounded.RemoveDone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import little.goose.account.data.constants.MoneyType
import little.goose.account.data.entities.Transaction
import little.goose.account.ui.component.TransactionColumn
import little.goose.account.ui.component.TransactionColumnState
import little.goose.account.ui.transaction.TransactionDialog
import little.goose.account.ui.transaction.rememberTransactionDialogState
import little.goose.common.constants.KEY_CONTENT
import little.goose.common.constants.KEY_MONEY_TYPE
import little.goose.common.constants.KEY_TIME
import little.goose.common.constants.KEY_TIME_TYPE
import little.goose.common.utils.TimeType
import little.goose.design.system.component.MovableActionButton
import little.goose.design.system.component.MovableActionButtonState
import little.goose.design.system.theme.AccountTheme
import java.io.Serializable
import java.util.*

@AndroidEntryPoint
class TransactionExampleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AccountTheme {
                val viewModel = hiltViewModel<TransactionExampleViewModel>()
                val transactionDialogState = rememberTransactionDialogState()
                val snackbarHostState = remember { SnackbarHostState() }
                val transactionColumnState by viewModel.transactionColumnState.collectAsState()

                TransactionTimeScreen(
                    modifier = Modifier.fillMaxSize(),
                    title = viewModel.title,
                    onTransactionClick = transactionDialogState::show,
                    snackbarHostState = snackbarHostState,
                    transactionColumnState = transactionColumnState,
                    onBack = ::finish
                )

                TransactionDialog(
                    state = transactionDialogState,
                    onDelete = viewModel::deleteTransaction
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
                                    message = getString(little.goose.account.R.string.deleted),
                                    withDismissAction = true,
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    companion object {
        fun open(
            context: Context,
            time: Date,
            timeType: TimeType,
            moneyType: MoneyType = MoneyType.BALANCE,
            keyContent: String? = null
        ) {
            val intent = Intent(context, TransactionExampleActivity::class.java).apply {
                putExtra(KEY_TIME, time as Serializable)
                putExtra(KEY_TIME_TYPE, timeType as Parcelable)
                putExtra(KEY_MONEY_TYPE, moneyType as Parcelable)
                putExtra(KEY_CONTENT, keyContent)
            }
            context.startActivity(intent)
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