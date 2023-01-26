package little.goose.account.ui.account

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import little.goose.account.R
import little.goose.account.appContext
import little.goose.account.appScope
import little.goose.account.common.dialog.time.TimeType
import little.goose.account.common.viewModelInstance
import little.goose.account.logic.AccountRepository
import little.goose.account.logic.data.constant.*
import little.goose.account.ui.account.widget.TransactionCard
import little.goose.account.ui.base.BaseActivity
import little.goose.account.ui.theme.AccountTheme
import little.goose.account.ui.theme.Red200
import little.goose.account.utils.*
import java.io.Serializable
import java.util.*

class TransactionExampleActivity : BaseActivity() {

    private lateinit var viewModel: TransactionExampleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.deleteReceiver.register(lifecycle, NOTIFY_DELETE_TRANSACTION) { _, transaction ->
            lifecycleScope.launch { viewModel.deleteTransaction.emit(transaction) }
        }

        setContent {
            val time = intent.serializable<Date>(KEY_TIME) ?: run {
                finish()
                return@setContent
            }
            val content = intent.getStringExtra(KEY_CONTENT)
            val timeType = intent.parcelable<TimeType>(KEY_TIME_TYPE) ?: run {
                finish()
                return@setContent
            }
            val moneyType = intent.parcelable<MoneyType>(KEY_MONEY_TYPE) ?: run {
                finish()
                return@setContent
            }

            viewModel = viewModelInstance {
                TransactionExampleViewModel(time, content, timeType, moneyType)
            }

            val title = when (timeType) {
                TimeType.DATE -> {
                    time.toChineseYearMonthDay()
                }
                TimeType.YEAR_MONTH -> {
                    time.toChineseYearMonth()
                }
                TimeType.YEAR -> {
                    time.toChineseYear()
                }
                else -> "不支持"
            }

            AccountTheme {
                TransactionTimeScreen(
                    title = title,
                    fragmentManager = supportFragmentManager
                ) {
                    finish()
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
    title: String,
    fragmentManager: FragmentManager? = null,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()

    val viewModel = viewModel<TransactionExampleViewModel>()
    val transactions by viewModel.transactions.collectAsState(initial = emptyList())
    val deleteTrans by viewModel.deleteTransaction.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = { TitleBar(title, onBack) },
        snackbarHost = {
            DeleteSnackbarHost(snackbarHostState) {
                appScope.launch {
                    deleteTrans?.let { AccountRepository.addTransaction(it) }
                    viewModel.deleteTransaction.emit(null)
                }
                snackbarHostState.currentSnackbarData?.dismiss()
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues),
            contentPadding = PaddingValues(16.dp, 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(transactions, key = { it.id!! }) { transaction ->
                TransactionCard(transaction, fragmentManager)
            }
        }
    }

    deleteTrans?.let {
        scope.launch {
            snackbarHostState.showSnackbar(
                message = appContext.getString(R.string.deleted),
                actionLabel = appContext.getString(R.string.undo),
                1000L
            )
        }
    }
}

@Composable
private fun TitleBar(title: String, onBack: () -> Unit) {
    TopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            IconButton(onClick = { onBack() }) {
                Icon(painter = painterResource(id = R.drawable.icon_back), contentDescription = "")
            }
        }
    )
}

@Composable
fun DeleteSnackbarHost(
    snackbarHostState: SnackbarHostState,
    action: () -> Unit
) {
    SnackbarHost(hostState = snackbarHostState) { snackbarData ->
        Snackbar(modifier = Modifier.padding(12.dp), action = {
            snackbarData.visuals.actionLabel?.let { label ->
                TextButton(onClick = { action() }) {
                    Text(text = label, color = Red200)
                }
            }
        }) {
            Text(text = snackbarData.visuals.message, color = Color.White)
        } // Snackbar
    } // SnackbarHost
}