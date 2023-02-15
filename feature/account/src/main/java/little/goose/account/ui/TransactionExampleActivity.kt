package little.goose.account.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
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
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import little.goose.account.data.constants.MoneyType
import little.goose.common.dialog.time.TimeType
import little.goose.account.ui.transaction.TransactionDialogFragment
import little.goose.account.ui.widget.TransactionCard
import little.goose.common.constants.KEY_CONTENT
import little.goose.common.constants.KEY_MONEY_TYPE
import little.goose.common.constants.KEY_TIME
import little.goose.common.constants.KEY_TIME_TYPE
import little.goose.design.system.theme.AccountTheme
import little.goose.design.system.theme.Red200
import little.goose.common.utils.*
import java.io.Serializable
import java.util.*

@AndroidEntryPoint
class TransactionExampleActivity : AppCompatActivity() {

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
    onTransactionClick: (little.goose.account.data.entities.Transaction) -> Unit,
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
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(id = little.goose.common.R.drawable.icon_back),
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
            coroutineScope {
                launch {
                    val withDismissAction = context.getString(little.goose.common.R.string.undo).isNotEmpty()
                    snackbarHostState.showSnackbar(
                        context.getString(little.goose.common.R.string.deleted),
                        context.getString(little.goose.common.R.string.undo),
                        withDismissAction,
                        SnackbarDuration.Indefinite
                    )
                }
                launch {
                    delay(2000L)
                    snackbarHostState.currentSnackbarData?.dismiss()
                }
            }
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