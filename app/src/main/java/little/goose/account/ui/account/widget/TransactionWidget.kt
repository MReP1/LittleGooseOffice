package little.goose.account.ui.account.widget

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentManager
import little.goose.account.R
import little.goose.account.logic.data.constant.AccountConstant.EXPENSE
import little.goose.account.logic.data.constant.KEY_TRANSACTION
import little.goose.account.logic.data.entities.Transaction
import little.goose.account.ui.account.transaction.TransactionDialogFragment
import little.goose.account.ui.account.transaction.icon.TransactionIconHelper
import little.goose.account.ui.theme.AccountTheme
import little.goose.account.ui.widget.card.ShadowCard
import little.goose.account.utils.toSignString
import java.math.BigDecimal
import java.util.*

@Composable
fun TransactionCard(
    transaction: Transaction, fragmentManager: FragmentManager? = null
) {
    ShadowCard(
        onClick = {
            fragmentManager?.let {
                TransactionDialogFragment
                    .newInstance(transaction)
                    .showNow(it, KEY_TRANSACTION)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(66.dp),
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.padding(18.dp, 0.dp, 12.dp, 0.dp),
                painter = painterResource(id = TransactionIconHelper.getIconPath(transaction.icon_id)),
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
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Preview
@Composable
private fun DefaultPreview() {
    AccountTheme {
        TransactionCard(
            transaction = Transaction(
                null, EXPENSE, BigDecimal(71),
                "消费", "null", Date(), R.drawable.icon_book
            ), null
        )
    }
}