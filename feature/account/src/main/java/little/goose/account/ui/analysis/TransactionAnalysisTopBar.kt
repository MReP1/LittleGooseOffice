package little.goose.account.ui.analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import little.goose.account.R
import java.math.BigDecimal

data class TransactionAnalysisTopBarState(
    val expenseSum: BigDecimal = BigDecimal(0),
    val incomeSum: BigDecimal = BigDecimal(0),
    val balance: BigDecimal = BigDecimal(0)
)

@Composable
fun TransactionAnalysisTopBar(
    modifier: Modifier = Modifier,
    state: TransactionAnalysisTopBarState,
    selectedTabIndex: Int,
    onTabClick: (index: Int) -> Unit,
    onBack: () -> Unit
) {
    val shape = remember { RoundedCornerShape(24.dp) }
    CenterAlignedTopAppBar(
        modifier = modifier.fillMaxWidth(),
        title = {
            TabRow(
                modifier = Modifier.padding(horizontal = 36.dp),
                selectedTabIndex = selectedTabIndex,
                divider = {},
                indicator = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .zIndex(-1F),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .tabIndicatorOffset(it[selectedTabIndex])
                                .clip(shape)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .width(12.dp)
                                .height(58.dp)
                        )
                    }
                }
            ) {
                Tab(
                    modifier = Modifier.clip(shape),
                    selected = selectedTabIndex == 0,
                    onClick = { onTabClick(0) }
                ) {
                    Text(
                        text = stringResource(id = R.string.expense),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = state.expenseSum.toPlainString(),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Tab(
                    modifier = Modifier.clip(shape),
                    selected = selectedTabIndex == 1,
                    onClick = { onTabClick(1) }
                ) {
                    Text(
                        text = stringResource(id = R.string.income),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = state.incomeSum.toPlainString(),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Tab(
                    modifier = Modifier.clip(shape),
                    selected = selectedTabIndex == 2,
                    onClick = { onTabClick(2) }
                ) {
                    Text(
                        text = stringResource(id = R.string.balance),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = state.balance.toPlainString(),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = stringResource(id = R.string.back)
                )
            }
        }
    )
}