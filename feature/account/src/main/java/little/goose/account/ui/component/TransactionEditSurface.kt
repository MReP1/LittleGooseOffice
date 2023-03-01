package little.goose.account.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import little.goose.account.data.entities.Transaction
import little.goose.account.ui.transaction.MoneyCalculator
import little.goose.account.ui.transaction.icon.TransactionIconHelper
import little.goose.common.utils.toChineseYearMonDayWeek
import java.math.BigDecimal

@Composable
internal fun TransactionEditSurface(
    modifier: Modifier = Modifier,
    transaction: Transaction,
    onTransactionChange: (Transaction) -> Unit,
    onAgainClick: (Transaction) -> Unit,
    onDoneClick: (Transaction) -> Unit
) {
    val context = LocalContext.current

    val moneyCalculator = remember { MoneyCalculator(transaction.money) }
    val currentTransaction by rememberUpdatedState(newValue = transaction)
    val isContainOperator by moneyCalculator.isContainOperator.collectAsState()
    val money by moneyCalculator.money.collectAsState()

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
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp)
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
                Text(text = money)
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp)
        ) {
            Surface(modifier = Modifier.weight(1F)) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = transaction.time.toChineseYearMonDayWeek(context))
                }
            }
            Surface(modifier = Modifier.weight(1F)) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = transaction.description)
                }
            }
        }

        Calculator(
            modifier = Modifier.fillMaxWidth(),
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