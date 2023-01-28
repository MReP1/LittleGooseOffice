package little.goose.account.ui.account

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import little.goose.account.R
import little.goose.account.common.dialog.time.TimeType
import little.goose.account.logic.data.constant.*
import little.goose.account.logic.data.entities.Transaction
import little.goose.account.ui.account.transaction.TransactionDialogFragment
import little.goose.account.ui.account.widget.TransactionCard
import little.goose.account.ui.base.BaseActivity
import little.goose.account.ui.theme.AccountTheme
import little.goose.account.ui.theme.Red200
import little.goose.account.utils.*
import java.io.Serializable
import java.util.*

@AndroidEntryPoint
class TransactionExampleActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val title = remember {
                val time = intent.serializable<Date>(KEY_TIME)!!
                when (intent.parcelable<TimeType>(KEY_TIME_TYPE)!!) {
                    TimeType.DATE -> time.toChineseYearMonthDay()
                    TimeType.YEAR_MONTH -> time.toChineseYearMonth()
                    TimeType.YEAR -> time.toChineseYear()
                    else -> throw IllegalArgumentException()
                }
            }
            AccountTheme {
                TransactionTimeScreen(
                    modifier = Modifier.fillMaxSize(),
                    title = title,
                    onTransactionClick = { transaction ->
                        TransactionDialogFragment.showNow(transaction, supportFragmentManager)
                    },
                    onBack = {
                        finish()
                    }
                )
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
    onTransactionClick: (Transaction) -> Unit,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val viewModel = hiltViewModel<TransactionExampleViewModel>()

    val transactions by viewModel.transactions.collectAsState(initial = emptyList())
    val deleteTransaction by viewModel.deleteTransaction.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                modifier = Modifier,
                title = { Text(text = title) },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.icon_back),
                            contentDescription = ""
                        )
                    }
                }
            )
        },
        snackbarHost = {
            DeleteSnackbarHost(
                action = {
                    scope.launch {
                        viewModel.undo()
                        snackbarHostState.currentSnackbarData?.dismiss()
                    }
                },
                snackbarHostState = snackbarHostState
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues),
            contentPadding = PaddingValues(16.dp, 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = transactions,
                key = { it.id!! }
            ) { transaction ->
                TransactionCard(
                    transaction = transaction,
                    onTransactionClick = onTransactionClick
                )
            }
        }
    }

    LaunchedEffect(deleteTransaction) {
        if (deleteTransaction != null) {
            snackbarHostState.showSnackbar(
                message = context.getString(R.string.deleted),
                actionLabel = context.getString(R.string.undo),
                duration = 2000L
            )
        }
    }
}


@Composable
fun DeleteSnackbarHost(
    modifier: Modifier = Modifier,
    action: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    SnackbarHost(hostState = snackbarHostState) { snackbarData ->
        Snackbar(modifier = modifier.padding(12.dp), action = {
            snackbarData.visuals.actionLabel?.let { label ->
                TextButton(onClick = action) {
                    Text(text = label, color = Red200)
                }
            }
        }) {
            Text(text = snackbarData.visuals.message, color = Color.White)
        } // Snackbar
    } // SnackbarHost
}