package little.goose.account.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import little.goose.account.data.entities.Transaction
import little.goose.account.logic.MoneyCalculator
import little.goose.account.ui.transaction.icon.TransactionIconHelper
import little.goose.common.utils.TimeType
import little.goose.common.utils.toChineseMonthDayTime
import little.goose.design.system.component.dialog.InputDialog
import little.goose.design.system.component.dialog.TimeSelectorBottomDialog
import little.goose.design.system.component.dialog.rememberBottomSheetDialogState
import java.math.BigDecimal

@Composable
internal fun TransactionEditSurface(
    modifier: Modifier = Modifier,
    transaction: Transaction,
    onTransactionChange: (Transaction) -> Unit,
    onAgainClick: (Transaction) -> Unit,
    onDoneClick: (Transaction) -> Unit
) {
    val scope = rememberCoroutineScope()

    val moneyCalculator = remember { MoneyCalculator(transaction.money) }
    val currentTransaction by rememberUpdatedState(newValue = transaction)
    val isContainOperator by moneyCalculator.isContainOperator.collectAsState()
    val money by moneyCalculator.money.collectAsState()

    val timeSelectorDialogState = rememberBottomSheetDialogState()
    TimeSelectorBottomDialog(
        state = timeSelectorDialogState,
        initTime = transaction.time,
        type = TimeType.DATE_TIME,
        onConfirm = { onTransactionChange(transaction.copy(time = it)) }
    )

    val inputDialogState = rememberBottomSheetDialogState()
    InputDialog(
        state = inputDialogState,
        text = transaction.description,
        onConfirm = {
            onTransactionChange(transaction.copy(description = it))
        }
    )

    LaunchedEffect(moneyCalculator) {
        moneyCalculator.money.collect { moneyStr ->
            runCatching {
                BigDecimal(moneyStr)
            }.getOrNull()?.let { money ->
                onTransactionChange(currentTransaction.copy(money = money))
            }
        }
    }

    Column(modifier = modifier) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(start = 16.dp, end = 24.dp, top = 12.dp, bottom = 12.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.size(32.dp),
                    painter = painterResource(
                        id = TransactionIconHelper.getIconPath(transaction.icon_id)
                    ),
                    contentDescription = transaction.content
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = transaction.content, modifier = Modifier.weight(1F))
                Spacer(modifier = Modifier.weight(1f))
                Text(text = money, style = MaterialTheme.typography.titleLarge)
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp)
        ) {
            Surface(
                modifier = Modifier.weight(1F),
                onClick = {
                    scope.launch(Dispatchers.Main.immediate) {
                        if (timeSelectorDialogState.isClosed) {
                            timeSelectorDialogState.open()
                        } else {
                            timeSelectorDialogState.close()
                        }
                    }
                }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 8.dp, bottom = 8.dp, start = 20.dp, end = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Rounded.CalendarToday, contentDescription = "Calendar")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = transaction.time.toChineseMonthDayTime(),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Surface(
                modifier = Modifier.weight(1F),
                onClick = {
                    scope.launch(Dispatchers.Main.immediate) {
                        inputDialogState.open()
                    }
                }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 8.dp, bottom = 8.dp, start = 16.dp, end = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = transaction.description.ifBlank { "Description..." },
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        Calculator(
            modifier = Modifier
                .fillMaxWidth()
                .height(288.dp),
            onNumClick = {
                moneyCalculator.appendMoneyEnd(it.digitToChar())
            },
            onAgainClick = {
                moneyCalculator.operate()
                onAgainClick(transaction.copy(money = BigDecimal(moneyCalculator.money.value)))
            },
            onDoneClick = {
                moneyCalculator.operate()
                onDoneClick(transaction.copy(money = BigDecimal(moneyCalculator.money.value)))
            },
            onOperatorClick = moneyCalculator::modifyOther,
            isContainOperator = isContainOperator
        )
    }
}

@Preview(device = "spec:width=380dp,height=480dp,dpi=440")
@Composable
fun PreviewTransactionEditSurface() {
    TransactionEditSurface(
        transaction = Transaction(description = "description", content = "content"),
        onTransactionChange = {},
        onAgainClick = {},
        onDoneClick = {}
    )
}