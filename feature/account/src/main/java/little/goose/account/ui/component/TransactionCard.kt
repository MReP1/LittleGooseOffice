package little.goose.account.ui.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import little.goose.account.R
import little.goose.account.data.constants.AccountConstant.EXPENSE
import little.goose.account.data.entities.Transaction
import little.goose.account.ui.transaction.icon.TransactionIconHelper
import little.goose.common.utils.toChineseMonthDayTime
import little.goose.common.utils.toSignString
import little.goose.design.system.theme.AccountTheme
import java.math.BigDecimal
import java.util.Date

@Composable
fun TransactionCard(
    modifier: Modifier = Modifier,
    transaction: Transaction,
    isExpended: Boolean = false,
    isMultiSelecting: Boolean = false,
    selected: Boolean = false,
    onTransactionClick: (Transaction) -> Unit,
    onTransactionEdit: (Transaction) -> Unit,
    onTransactionDelete: (Transaction) -> Unit,
    onSelectTransaction: (transaction: Transaction, selected: Boolean) -> Unit
) {
    Card(modifier = modifier) {
        Box(
            modifier = Modifier
                .animateContentSize()
                .combinedClickable(
                    onClick = {
                        if (isMultiSelecting) {
                            onSelectTransaction(transaction, !selected)
                        } else {
                            onTransactionClick(transaction)
                        }
                    },
                    onLongClick = { onSelectTransaction(transaction, !selected) }
                ),
            contentAlignment = Alignment.Center
        ) {
            Column {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.padding(18.dp, 0.dp, 12.dp, 0.dp),
                        painter = painterResource(id = TransactionIconHelper.getIconPath(transaction.icon_id)),
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = null
                    )
                    Text(
                        text = transaction.content,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.weight(1F))
                    Text(
                        modifier = Modifier.padding(20.dp, 0.dp),
                        text = transaction.money.toSignString(),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                if (isExpended) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.width(20.dp))
                        Column {
                            if (transaction.description.isNotBlank()) {
                                Text(
                                    text = transaction.description,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Text(
                                text = transaction.time.toChineseMonthDayTime(),
                                style = MaterialTheme.typography.labelSmall,
                            )
                        }
                        Spacer(modifier = Modifier.weight(1F))
                        IconButton(onClick = { onTransactionEdit(transaction) }) {
                            Icon(imageVector = Icons.Rounded.Edit, contentDescription = "Edit")
                        }
                        IconButton(onClick = { onTransactionDelete(transaction) }) {
                            Icon(imageVector = Icons.Rounded.Delete, contentDescription = "Delete")
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                } else {
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
            if (selected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    tint = MaterialTheme.colorScheme.tertiary,
                    contentDescription = "selected",
                    modifier = Modifier
                        .matchParentSize()
                        .alpha(0.5F)
                )
            }
        }
    }
}

@Preview
@Composable
private fun ExpendedPreview() = AccountTheme {
    TransactionCard(
        transaction = Transaction(
            null, EXPENSE, BigDecimal(71),
            "消费", "null", Date(), R.drawable.icon_book
        ),
        isExpended = true,
        onTransactionClick = {},
        onTransactionEdit = {},
        onTransactionDelete = {},
        onSelectTransaction = { _, _ -> }
    )
}

@Preview
@Composable
private fun DefaultPreview() {
    AccountTheme {
        TransactionCard(
            transaction = Transaction(
                null, EXPENSE, BigDecimal(71),
                "消费", "null", Date(), R.drawable.icon_book
            ),
            selected = true,
            onTransactionClick = {},
            onTransactionEdit = {},
            onTransactionDelete = {},
            onSelectTransaction = { _, _ -> }
        )
    }
}