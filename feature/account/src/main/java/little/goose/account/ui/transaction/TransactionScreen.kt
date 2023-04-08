package little.goose.account.ui.transaction

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import little.goose.account.R
import little.goose.account.data.constants.AccountConstant.EXPENSE
import little.goose.account.data.constants.AccountConstant.INCOME
import little.goose.account.ui.component.IconsBoard
import little.goose.account.ui.component.TransactionEditSurface
import little.goose.account.ui.transaction.icon.TransactionIconHelper

@Composable
fun TransactionScreen(
    modifier: Modifier = Modifier,
    onFinished: () -> Unit
) {
    val viewModel: TransactionViewModel = hiltViewModel()
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(0)

    val transaction by viewModel.transaction.collectAsState()

    var expenseSelectedIcon by remember {
        mutableStateOf(
            if (transaction.type == EXPENSE)
                TransactionIconHelper.expenseIconList.find { it.id == transaction.icon_id }!!
            else TransactionIconHelper.expenseIconList.first()
        )
    }
    var incomeSelectedIcon by remember {
        mutableStateOf(
            if (transaction.type == INCOME)
                TransactionIconHelper.incomeIconList.find { it.id == transaction.icon_id }!!
            else TransactionIconHelper.incomeIconList.first()
        )
    }

    LaunchedEffect(transaction) {
        when (transaction.type) {
            EXPENSE -> {
                expenseSelectedIcon =
                    TransactionIconHelper.expenseIconList.find { it.id == transaction.icon_id }!!
                pagerState.animateScrollToPage(0)
            }
            INCOME -> {
                incomeSelectedIcon =
                    TransactionIconHelper.incomeIconList.find { it.id == transaction.icon_id }!!
                pagerState.animateScrollToPage(1)
            }
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect {
            if (it == 0) {
                viewModel.setTransaction(
                    transaction.copy(
                        type = EXPENSE,
                        content = expenseSelectedIcon.name,
                        icon_id = expenseSelectedIcon.id
                    )
                )
            } else {
                viewModel.setTransaction(
                    transaction.copy(
                        type = INCOME,
                        content = incomeSelectedIcon.name,
                        icon_id = incomeSelectedIcon.id
                    )
                )
            }
        }
    }

    LaunchedEffect(viewModel.event) {
        viewModel.event.collect { event ->
            when (event) {
                TransactionViewModel.Event.WriteSuccess -> {
                    onFinished()
                }
            }
        }
    }

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
                            Text(
                                text = stringResource(id = R.string.expense),
                                style = MaterialTheme.typography.titleMedium
                            )
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
                            Text(
                                text = stringResource(id = R.string.income),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onFinished) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                }
            )
        },
        content = {
            HorizontalPager(
                modifier = Modifier.padding(it),
                pageCount = 2,
                state = pagerState
            ) { page ->
                if (page == 0) {
                    IconsBoard(
                        modifier = Modifier.fillMaxSize(),
                        icons = TransactionIconHelper.expenseIconList,
                        onIconClick = { icon ->
                            expenseSelectedIcon = icon
                            viewModel.setTransaction(
                                transaction.copy(icon_id = icon.id, content = icon.name)
                            )
                        },
                        selectedIcon = expenseSelectedIcon
                    )
                } else {
                    IconsBoard(
                        modifier = Modifier.fillMaxSize(),
                        icons = TransactionIconHelper.incomeIconList,
                        onIconClick = { icon ->
                            incomeSelectedIcon = icon
                            viewModel.setTransaction(
                                transaction.copy(icon_id = icon.id, content = icon.name)
                            )
                        },
                        selectedIcon = incomeSelectedIcon
                    )
                }
            }
        },
        bottomBar = {
            TransactionEditSurface(
                modifier = Modifier.navigationBarsPadding(),
                transaction = transaction,
                onTransactionChange = viewModel::setTransaction,
                onAgainClick = { viewModel.writeDatabase(it, isAgain = true) },
                onDoneClick = { viewModel.writeDatabase(it, isAgain = false) }
            )
        }
    )
}