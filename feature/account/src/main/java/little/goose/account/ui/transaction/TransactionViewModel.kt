package little.goose.account.ui.transaction

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import little.goose.account.data.constants.AccountConstant.EXPENSE
import little.goose.account.data.constants.AccountConstant.INCOME
import little.goose.account.data.entities.Transaction
import little.goose.account.data.holder.AccountConfigDataHolder
import little.goose.account.data.models.IconDisplayType
import little.goose.account.logic.GetTransactionByIdFlowUseCase
import little.goose.account.logic.InsertTransactionUseCase
import little.goose.account.logic.UpdateTransactionUseCase
import little.goose.account.ui.component.TransactionEditSurfaceState
import little.goose.account.ui.transaction.icon.TransactionIconHelper
import little.goose.common.utils.getDate
import little.goose.common.utils.getMonth
import little.goose.common.utils.getYear
import little.goose.common.utils.setDate
import little.goose.common.utils.setMonth
import little.goose.common.utils.setYear
import java.math.BigDecimal
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val accountConfigDataHolder: AccountConfigDataHolder,
    private val getTransactionByIdFlowUseCase: GetTransactionByIdFlowUseCase,
    private val insertTransactionUseCase: InsertTransactionUseCase,
    private val updateTransactionUseCase: UpdateTransactionUseCase
) : ViewModel() {

    private val args = TransactionRouteArgs(savedStateHandle)

    val iconDisplayType = accountConfigDataHolder.accountConfig
        .map { it.transactionConfig.iconDisplayType }
        .stateIn(scope = viewModelScope, SharingStarted.Eagerly, IconDisplayType.ICON_ONLY)

    private val defaultTransaction
        get() = Transaction(
            time = args.time?.let {
                val time = Calendar.getInstance().apply { timeInMillis = it }
                Calendar.getInstance().apply {
                    setYear(time.getYear())
                    setMonth(time.getMonth())
                    setDate(time.getDate())
                }.time
            } ?: Date(),
            icon_id = TransactionIconHelper.expenseIconList.first().id,
            content = TransactionIconHelper.expenseIconList.first().name
        )

    enum class Event {
        WriteSuccess, CantBeZero
    }

    private val _event: MutableSharedFlow<Event> = MutableSharedFlow()
    val event = _event.asSharedFlow()

    private val _transaction: MutableStateFlow<Transaction?> = MutableStateFlow(null)
    val transaction = _transaction.asStateFlow()

    internal val transactionScreenState = combine(
        transaction.filterNotNull(),
        iconDisplayType,
        transaction.filterNotNull().filter { it.type == EXPENSE }.map { transaction ->
            TransactionIconHelper.expenseIconList.find { it.id == transaction.icon_id }
        }.filterNotNull().stateIn(
            viewModelScope, SharingStarted.Eagerly,
            initialValue = TransactionIconHelper.expenseIconList.first()
        ),
        transaction.filterNotNull().filter { it.type == INCOME }.map { transaction ->
            TransactionIconHelper.incomeIconList.find { it.id == transaction.icon_id }
        }.filterNotNull().stateIn(
            viewModelScope, SharingStarted.Eagerly,
            initialValue = TransactionIconHelper.incomeIconList.first()
        ),
    ) { transaction, iconDisplayType, expenseIcon, incomeIcon ->
        TransactionScreenState.Success(
            pageIndex = if (transaction.type == EXPENSE) 0 else 1,
            topBarState = TransactionScreenTopBarState(iconDisplayType = iconDisplayType),
            editSurfaceState = TransactionEditSurfaceState(transaction = transaction),
            iconPagerState = TransactionScreenIconPagerState(
                iconDisplayType = iconDisplayType,
                expenseSelectedIcon = expenseIcon,
                incomeSelectedIcon = incomeIcon
            )
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = TransactionScreenState.Loading
    )

    init {
        viewModelScope.launch {
            _transaction.value = args.transactionId?.let { id ->
                getTransactionByIdFlowUseCase(id).map {
                    if (it.type == EXPENSE) it.copy(money = it.money.abs()) else it
                }.first()
            } ?: defaultTransaction
        }
    }

    internal fun intent(intent: TransactionScreenIntent) {
        val currentTransaction = _transaction.value ?: return
        when (intent) {
            is TransactionScreenIntent.TransactionOperation.Done -> {
                val transaction = currentTransaction.copy(money = intent.money)
                writeDatabase(transaction, false)
            }

            is TransactionScreenIntent.TransactionOperation.Again -> {
                val transaction = currentTransaction.copy(money = intent.money)
                writeDatabase(transaction, true)
            }

            is TransactionScreenIntent.ChangeIconDisplayType -> {
                viewModelScope.launch {
                    accountConfigDataHolder.setIconDisplayType(intent.iconDisplayType)
                }
            }

            is TransactionScreenIntent.ChangeTransaction -> {
                _transaction.value = currentTransaction.copy(
                    type = intent.type ?: currentTransaction.type,
                    icon_id = intent.iconId ?: currentTransaction.icon_id,
                    content = intent.content ?: currentTransaction.content,
                    money = intent.money ?: currentTransaction.money,
                    description = intent.description ?: currentTransaction.description,
                    time = intent.time ?: currentTransaction.time
                )
            }
        }
    }

    private fun writeDatabase(transaction: Transaction, isAgain: Boolean) {
        // 检查金额是否为空
        if (transaction.money == BigDecimal.ZERO) {
            viewModelScope.launch { _event.emit(Event.CantBeZero) }
            return
        }

        // 根据类型将金额调整为对应正负，支出对应负值，收入对应正值
        val tra = if (transaction.type == INCOME && transaction.money.signum() == -1) {
            transaction.copy(money = transaction.money.abs())
        } else if (transaction.type == EXPENSE && transaction.money.signum() == 1) {
            transaction.copy(money = transaction.money.negate())
        } else transaction

        viewModelScope.launch(NonCancellable) {
            // 写入数据库
            if (tra.id == null) {
                insertTransactionUseCase(tra)
            } else {
                updateTransactionUseCase(tra)
            }

            if (!isAgain) {
                // 若点击完成，则结束记账
                _event.emit(Event.WriteSuccess)
            } else {
                // 若点击下一笔，则需要重置Transaction
                _transaction.value = defaultTransaction
            }
        }
    }
}