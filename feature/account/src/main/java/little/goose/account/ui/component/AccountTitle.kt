package little.goose.account.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DonutSmall
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import little.goose.design.system.component.AutoResizableText
import java.math.BigDecimal

@Stable
data class AccountTitleState(
    val curMonthExpenseSum: BigDecimal = BigDecimal(0),
    val curMonthIncomeSum: BigDecimal = BigDecimal(0),
    val curMonthBalance: BigDecimal = BigDecimal(0),
    val totalExpenseSum: BigDecimal = BigDecimal(0),
    val totalIncomeSum: BigDecimal = BigDecimal(0),
    val totalBalance: BigDecimal = BigDecimal(0),
)

@Composable
fun AccountTitle(
    modifier: Modifier,
    accountTitleState: AccountTitleState,
    onNavigateToAnalysis: () -> Unit
) {
    var showTotal by remember { mutableStateOf(false) }

    val incomeMoney = remember(showTotal, accountTitleState) {
        if (showTotal) accountTitleState.totalIncomeSum.toPlainString()
        else accountTitleState.curMonthIncomeSum.toPlainString()
    }

    val expenseMoney = remember(showTotal, accountTitleState) {
        if (showTotal) accountTitleState.totalExpenseSum.toPlainString()
        else accountTitleState.curMonthExpenseSum.toPlainString()
    }

    val balanceMoney = remember(showTotal, accountTitleState) {
        if (showTotal) accountTitleState.totalBalance.toPlainString()
        else accountTitleState.curMonthBalance.toPlainString()
    }

    val textMeasurer = rememberTextMeasurer()

    Surface(
        onClick = { showTotal = !showTotal },
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.8.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Box(modifier = Modifier.fillMaxSize()) {

            Surface(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(6.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4F),
                shape = MaterialTheme.shapes.large
            ) {
                Text(
                    text = if (showTotal) "总" else "当月",
                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 8.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Row(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .weight(3F)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(text = "结余")
                    AutoResizableText(
                        text = balanceMoney,
                        style = MaterialTheme.typography.headlineSmall,
                        textAlignment = Alignment.CenterHorizontally
                    )

                }

                Column(
                    modifier = Modifier
                        .weight(2F)
                        .fillMaxHeight()
                        .clip(MaterialTheme.shapes.large)
                        .background(MaterialTheme.colorScheme.surfaceColorAtElevation(3.6.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(MaterialTheme.shapes.large)
                            .background(MaterialTheme.colorScheme.tertiaryContainer)
                            .clickable(onClick = onNavigateToAnalysis),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.DonutSmall,
                            contentDescription = "analysis",
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.Start)
                        ) {
                            Text(
                                text = "收入",
                                style = MaterialTheme.typography.labelSmall
                            )
                            AutoResizableText(
                                text = incomeMoney,
                                style = MaterialTheme.typography.labelSmall,
                                textAlignment = Alignment.Start,
                                textMeasurer = textMeasurer
                            )
                        }
                        Divider(
                            modifier = Modifier.padding(start = 8.dp, end = 16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.Start)
                        ) {
                            Text(
                                text = "支出",
                                style = MaterialTheme.typography.labelSmall
                            )
                            AutoResizableText(
                                text = expenseMoney,
                                style = MaterialTheme.typography.labelSmall,
                                textAlignment = Alignment.Start,
                                textMeasurer = textMeasurer
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewAccountTitle2() {
    AccountTitle(
        modifier = Modifier
            .fillMaxWidth()
            .height(118.dp),
        accountTitleState = AccountTitleState(
            BigDecimal(5125), BigDecimal(1234), BigDecimal(8512),
            BigDecimal(23), BigDecimal(512293), BigDecimal(2144)
        ),
        onNavigateToAnalysis = {}
    )
}