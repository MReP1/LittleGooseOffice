package little.goose.account.ui.transaction

import androidx.compose.foundation.text2.input.TextFieldState
import androidx.compose.foundation.text2.input.delete
import androidx.compose.foundation.text2.input.insert
import androidx.compose.foundation.text2.input.textAsFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import little.goose.account.data.constants.AccountConstant.EXPENSE
import little.goose.account.data.constants.AccountConstant.INCOME
import little.goose.account.data.entities.Transaction
import little.goose.account.data.holder.AccountConfigDataHolder
import little.goose.account.data.models.IconDisplayType
import little.goose.account.data.models.TransactionIcon
import little.goose.account.logic.GetTransactionByIdFlowUseCase
import little.goose.account.logic.InsertTransactionUseCase
import little.goose.account.logic.MoneyCalculator
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

sealed class TransactionEvent {
    data object WriteSuccess : TransactionEvent()
    data object CantBeZero : TransactionEvent()
}

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

    private val _event: MutableSharedFlow<TransactionEvent> = MutableSharedFlow()
    val event = _event.asSharedFlow()

    private val transaction: MutableStateFlow<Transaction?> = MutableStateFlow(null)

    private val expenseIcon = this.transaction.filterNotNull()
        .filter { it.type == EXPENSE }
        .map { transaction ->
            TransactionIconHelper.expenseIconList.find { it.id == transaction.icon_id }
        }.filterNotNull().stateIn(
            scope = viewModelScope, started = SharingStarted.Eagerly,
            initialValue = TransactionIconHelper.expenseIconList.first()
        )

    private val incomeIcon = this.transaction.filterNotNull()
        .filter { it.type == INCOME }
        .map { transaction ->
            TransactionIconHelper.incomeIconList.find { it.id == transaction.icon_id }
        }.filterNotNull().stateIn(
            scope = viewModelScope, started = SharingStarted.Eagerly,
            initialValue = TransactionIconHelper.incomeIconList.first()
        )

    private val calculator = MoneyCalculator()

    private val descriptionTextFieldState = TextFieldState(initialText = "")

    private val isEditDescription = MutableStateFlow(false)

    internal val transactionScreenState = combine(
        this.transaction.filterNotNull(),
        iconDisplayType,
        expenseIcon, incomeIcon,
        calculator.money, calculator.isContainOperator,
        isEditDescription
    ) {
        val transaction = it[0] as Transaction
        val iconDisplayType = it[1] as IconDisplayType
        val expenseIcon = it[2] as TransactionIcon
        val incomeIcon = it[3] as TransactionIcon
        val money = it[4] as String
        val isContainOperator = it[5] as Boolean
        val isEditDescription = it[6] as Boolean

        val isExpense = transaction.type == EXPENSE
        TransactionScreenState.Success(
            pageIndex = if (isExpense) 0 else 1,
            topBarState = TransactionScreenTopBarState(iconDisplayType = iconDisplayType),
            editSurfaceState = TransactionEditSurfaceState(
                money = money,
                content = transaction.content,
                iconId = (if (isExpense) expenseIcon else incomeIcon).iconResId,
                time = transaction.time,
                isContainOperator = isContainOperator,
                descriptionTextFieldState = descriptionTextFieldState,
                isEditDescription = isEditDescription
            ),
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
            val transaction = args.transactionId?.let { id ->
                getTransactionByIdFlowUseCase(id).map {
                    if (it.type == EXPENSE) it.copy(money = it.money.abs()) else it
                }.first()
            } ?: generateDefaultTransaction()
            calculator.setMoney(transaction.money)
            descriptionTextFieldState.edit { insert(0, transaction.description) }
            this@TransactionViewModel.transaction.value = transaction

            launch {
                calculator.money.collect { money ->
                    runCatching { BigDecimal(money) }.getOrNull()?.let { bd ->
                        this@TransactionViewModel.transaction.update { it!!.copy(money = bd) }
                    }
                }
            }
            launch {
                descriptionTextFieldState.textAsFlow().collect { description ->
                    val feedIndex = description.lastIndexOf('\n')
                    if (feedIndex >= 0) {
                        descriptionTextFieldState.edit {
                            delete(feedIndex, feedIndex + 1)
                        }
                        isEditDescription.value = false
                        return@collect
                    }
                    this@TransactionViewModel.transaction.update {
                        it!!.copy(description = description.toString())
                    }
                }
            }
        }
    }

    internal fun action(intent: TransactionScreenIntent) {
        val currentTransaction = this.transaction.value ?: return
        when (intent) {
            is TransactionScreenIntent.TransactionOperation -> {
                handleOperation(intent)
            }

            is TransactionScreenIntent.ChangeIconDisplayType -> {
                viewModelScope.launch {
                    accountConfigDataHolder.setIconDisplayType(intent.iconDisplayType)
                }
            }

            is TransactionScreenIntent.ChangeTransaction -> {
                this.transaction.value = currentTransaction.copy(
                    type = intent.type ?: currentTransaction.type,
                    icon_id = intent.iconId ?: currentTransaction.icon_id,
                    content = intent.content ?: currentTransaction.content,
                    money = intent.money ?: currentTransaction.money,
                    description = intent.description ?: currentTransaction.description,
                    time = intent.time ?: currentTransaction.time
                )
            }

            is TransactionScreenIntent.ChangeIsEditDescription -> {
                isEditDescription.value = intent.isEditDescription
            }
        }
    }

    private fun handleOperation(operation: TransactionScreenIntent.TransactionOperation) {
        when (operation) {
            TransactionScreenIntent.TransactionOperation.Done -> {
                calculator.operate()
                withWriteDatabase {
                    // 若点击完成，则结束记账
                    _event.emit(TransactionEvent.WriteSuccess)
                }
            }

            TransactionScreenIntent.TransactionOperation.Again -> {
                calculator.operate()
                withWriteDatabase {
                    this.transaction.value = generateDefaultTransaction()
                    calculator.setMoney(BigDecimal(0))
                    descriptionTextFieldState.edit { delete(0, length) }
                }
            }

            is TransactionScreenIntent.TransactionOperation.AppendEnd -> {
                calculator.appendMoneyEnd(operation.char)
            }

            is TransactionScreenIntent.TransactionOperation.ModifyOther -> {
                calculator.modifyOther(operation.logic)
            }
        }
    }

    private var writeJob: Job? = null

    private fun withWriteDatabase(action: suspend () -> Unit) {
        val currentTransaction = this.transaction.value ?: return
        val transaction = currentTransaction.copy(money = BigDecimal(calculator.money.value))
        writeJob?.takeUnless(Job::isCompleted)?.let { return }
        writeJob = viewModelScope.launch {
            withContext(NonCancellable) { writeDatabase(transaction) }
            ensureActive()
            // 若点击完成，则结束记账
            action()
            delay(1000L)
        }
    }

    private suspend fun writeDatabase(transaction: Transaction) {
        // 检查金额是否为空
        if (transaction.money == BigDecimal.ZERO) {
            _event.emit(TransactionEvent.CantBeZero)
            return
        }

        // 根据类型将金额调整为对应正负，支出对应负值，收入对应正值
        val tra = if (transaction.type == INCOME && transaction.money.signum() == -1) {
            transaction.copy(money = transaction.money.abs())
        } else if (transaction.type == EXPENSE && transaction.money.signum() == 1) {
            transaction.copy(money = transaction.money.negate())
        } else transaction

        // 写入数据库
        if (tra.id == null) {
            insertTransactionUseCase(tra)
        } else {
            updateTransactionUseCase(tra)
        }
    }

    private fun generateDefaultTransaction(): Transaction {
        return Transaction(
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
    }
}