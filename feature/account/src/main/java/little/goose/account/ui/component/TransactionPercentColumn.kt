package little.goose.account.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import little.goose.account.data.models.TransactionPercent
import little.goose.account.ui.transaction.icon.TransactionIconHelper
import little.goose.common.utils.toSignString
import little.goose.design.system.util.Display
import java.math.BigDecimal
import kotlin.math.abs

@Composable
fun TransactionPercentColumn(
    modifier: Modifier = Modifier,
    transactionPercents: List<TransactionPercent>,
    onTransactionPercentClick: (TransactionPercent) -> Unit,
    colors: List<Pair<Color, Color>>
) {
    Column(modifier = modifier) {
        for (index in transactionPercents.indices) {
            key(transactionPercents[index].icon_id) {
                TransactionPercentCard(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .height(54.dp)
                        .fillMaxWidth(),
                    transactionPercent = transactionPercents[index],
                    onTransactionPercentClick = onTransactionPercentClick,
                    colors = colors[index]
                )
            }
        }
    }
}

@Composable
fun TransactionPercentCard(
    modifier: Modifier,
    transactionPercent: TransactionPercent,
    onTransactionPercentClick: (TransactionPercent) -> Unit,
    colors: Pair<Color, Color>,
) {
    Surface(
        modifier = modifier,
        tonalElevation = 3.dp,
        shape = MaterialTheme.shapes.large,
        onClick = {
            onTransactionPercentClick(transactionPercent)
        }
    ) {
        Box(modifier = Modifier) {
            Row(modifier = Modifier.fillMaxSize()) {
                Surface(
                    modifier = Modifier
                        .weight(abs(transactionPercent.percent.toFloat()))
                        .fillMaxHeight(),
                    color = colors.first,
                    tonalElevation = 6.dp,
                    shape = RoundedCornerShape(12.dp)
                ) {}
                val spaceWeight = abs(1 - transactionPercent.percent.toFloat())
                if (spaceWeight > 0) {
                    Spacer(modifier = Modifier.weight(spaceWeight))
                }
            }
            Row(
                modifier = Modifier.matchParentSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(12.dp))
                TransactionIconHelper.getIcon(transactionPercent.icon_id).Display(
                    modifier = Modifier.size(32.dp),
                    tint = colors.second,
                    contentDescription = transactionPercent.content
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = transactionPercent.content, color = colors.second)
                Spacer(modifier = Modifier.weight(1F))
                Text(text = transactionPercent.money.toSignString(), color = colors.second)
                Spacer(modifier = Modifier.width(16.dp))
            }
        }
    }
}

@Preview(widthDp = 380, heightDp = 54)
@Composable
fun PreviewTransactionPercentCard(
) {
    TransactionPercentCard(
        modifier = Modifier.fillMaxSize(),
        transactionPercent = TransactionPercent(
            1, "饮食", BigDecimal(24), 0.34
        ),
        onTransactionPercentClick = {},
        colors = MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
    )
}