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

    private val transactionDataHolder = MutableStateFlow<TransactionScreenDataHolder?>(null)

    private val calculator = MoneyCalculator()

    private val descriptionTextFieldState = TextFieldState(initialText = "")

    private var currentValidMoney: BigDecimal? = null

    internal val transactionScreenState = combine(
        transactionDataHolder.filterNotNull(),
        iconDisplayType,
        calculator.money,
        calculator.isContainOperator,
    ) { dataHolder, iconDisplayType, money, isContainOperator ->
        val isExpense = dataHolder.type == EXPENSE
        TransactionScreenState.Success(
            pageIndex = if (isExpense) 0 else 1,
            topBarState = TransactionScreenTopBarState(iconDisplayType = iconDisplayType),
            editSurfaceState = TransactionEditSurfaceState(
                money = money,
                content = dataHolder.content,
                icon = (if (isExpense) dataHolder.expenseIcon else dataHolder.incomeIcon).icon,
                time = dataHolder.time,
                isContainOperator = isContainOperator,
                descriptionTextFieldState = descriptionTextFieldState,
                isEditDescription = dataHolder.isEditDescription
            ),
            iconPagerState = TransactionScreenIconPagerState(
                iconDisplayType = iconDisplayType,
                expenseSelectedIcon = dataHolder.expenseIcon,
                incomeSelectedIcon = dataHolder.incomeIcon
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
            this@TransactionViewModel.transactionDataHolder.value =
                TransactionScreenDataHolder.fromTransaction(transaction)
            calculator.setMoney(transaction.money)
            descriptionTextFieldState.edit { insert(0, transaction.description) }

            launch {
                calculator.money.collect { money ->
                    runCatching { BigDecimal(money) }.getOrNull()?.let { currentValidMoney = it }
                }
            }
            launch {
                descriptionTextFieldState.textAsFlow().collect { description ->
                    val feedIndex = description.lastIndexOf('\n')
                    if (feedIndex >= 0) {
                        descriptionTextFieldState.edit {
                            delete(feedIndex, feedIndex + 1)
                        }
                        transactionDataHolder.update {
                            it!!.copy(isEditDescription = false)
                        }
                        return@collect
                    }
                }
            }
        }
    }

    internal fun action(intent: TransactionScreenIntent) {
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
                transactionDataHolder.update { currentDataHolder ->
                    currentDataHolder!!
                    val type = intent.type ?: currentDataHolder.type
                    currentDataHolder.copy(
                        expenseIcon = if (type == EXPENSE) intent.iconId?.let {
                            TransactionIconHelper.getTransactionIconOrDefault(type, it)
                        } ?: currentDataHolder.expenseIcon else currentDataHolder.expenseIcon,
                        incomeIcon = if (type == INCOME) intent.iconId?.let {
                            TransactionIconHelper.getTransactionIconOrDefault(type, it)
                        } ?: currentDataHolder.incomeIcon else currentDataHolder.incomeIcon,
                        type = intent.type ?: currentDataHolder.type,
                        content = intent.content ?: currentDataHolder.content,
                        time = intent.time ?: currentDataHolder.time
                    )
                }
            }

            is TransactionScreenIntent.ChangeIsEditDescription -> {
                transactionDataHolder.update {
                    it!!.copy(isEditDescription = intent.isEditDescription)
                }
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
                    transactionDataHolder.update {
                        TransactionScreenDataHolder.fromTransaction(generateDefaultTransaction())
                    }
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
        val dataHolder = transactionDataHolder.value ?: return
        val currentValidMoney = currentValidMoney ?: return
        val transaction = dataHolder.toTransaction(
            money = currentValidMoney,
            description = descriptionTextFieldState.text.toString()
        )
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