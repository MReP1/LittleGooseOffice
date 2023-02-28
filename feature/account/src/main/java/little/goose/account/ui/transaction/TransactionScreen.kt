package little.goose.account.ui.transaction

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import little.goose.account.ui.component.IconsBoard
import little.goose.account.ui.component.TransactionEditSurface
import little.goose.account.ui.transaction.icon.TransactionIconHelper

@OptIn(ExperimentalPagerApi::class)
@Composable
fun TransactionScreen(
    modifier: Modifier = Modifier
) {
    val viewModel: TransactionScreenViewModel = viewModel()
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(0)

    val transaction by viewModel.transaction.collectAsState()
    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = {
                    TabRow(
                        modifier = Modifier.width(120.dp),
                        selectedTabIndex = pagerState.currentPage,
                        divider = {}
                    ) {
                        Tab(
                            selected = pagerState.currentPage == 0,
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(0)
                                }
                            },
                            modifier = Modifier.height(42.dp)
                        ) {
                            Text(text = "支出", style = MaterialTheme.typography.titleMedium)
                        }
                        Tab(
                            selected = pagerState.currentPage == 1,
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(1)
                                }
                            },
                            modifier = Modifier.height(42.dp)
                        ) {
                            Text(text = "收入", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            )
        },
        content = {
            HorizontalPager(
                modifier = Modifier.padding(it),
                count = 2,
                state = pagerState
            ) { page ->
                if (page == 0) {
                    var selectedIcon by remember {
                        mutableStateOf(TransactionIconHelper.expenseIconList.first())
                    }
                    IconsBoard(
                        modifier = Modifier.fillMaxSize(),
                        icons = TransactionIconHelper.expenseIconList,
                        onIconClick = { icon ->
                            selectedIcon = icon
                            viewModel.setTransaction(
                                transaction.copy(icon_id = icon.id, content = icon.name)
                            )
                        },
                        selectedIcon = selectedIcon
                    )
                } else {
                    var selectedIcon by remember {
                        mutableStateOf(TransactionIconHelper.incomeIconList.first())
                    }
                    IconsBoard(
                        modifier = Modifier.fillMaxSize(),
                        icons = TransactionIconHelper.incomeIconList,
                        onIconClick = { icon ->
                            selectedIcon = icon
                            viewModel.setTransaction(
                                transaction.copy(icon_id = icon.id, content = icon.name)
                            )
                        },
                        selectedIcon = selectedIcon
                    )
                }
            }
        },
        bottomBar = {
            TransactionEditSurface(
                transaction = transaction,
                onTransactionChange = viewModel::setTransaction,
                onAgainClick = {
//                               todo
//                    viewModel.writeDatabase(it)
                },
                onDoneClick = {
                    viewModel.writeDatabase(it)
                }
            )
        }
    )
}