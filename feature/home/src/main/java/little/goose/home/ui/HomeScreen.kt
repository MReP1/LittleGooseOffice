package little.goose.home.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import little.goose.account.ui.AccountHome
import little.goose.account.ui.AccountHomeViewModel
import little.goose.account.ui.analysis.AccountAnalysisActivity
import little.goose.account.ui.transaction.TransactionActivity
import little.goose.design.system.component.MovableActionButton
import little.goose.design.system.component.MovableActionButtonState
import little.goose.design.system.component.dialog.DeleteDialog
import little.goose.design.system.component.dialog.DeleteDialogState
import little.goose.home.data.*
import little.goose.home.ui.component.IndexTopBar
import little.goose.memorial.ui.*
import little.goose.note.ui.NotebookHome
import little.goose.note.ui.note.NoteActivity
import little.goose.schedule.data.entities.Schedule
import little.goose.schedule.ui.ScheduleDialog
import little.goose.schedule.ui.ScheduleHome
import little.goose.schedule.ui.ScheduleViewModel
import little.goose.schedule.ui.rememberScheduleDialogState
import little.goose.search.SearchActivity
import little.goose.search.SearchType
import java.util.*

@OptIn(ExperimentalPagerApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState()
    val scrollable by remember { derivedStateOf { pagerState.currentPage != HOME } }
    val currentHomePage = remember(pagerState.currentPage) {
        HomePage.fromPageIndex(pagerState.currentPage)
    }

    val scheduleViewModel = viewModel<ScheduleViewModel>()
    val indexViewModel = viewModel<IndexViewModel>()
    val accountViewModel = viewModel<AccountHomeViewModel>()

    val indexScreenState by indexViewModel.indexScreenState.collectAsState()
    val indexTopBarState by indexViewModel.indexTopBarState.collectAsState()
    val buttonState = remember { MovableActionButtonState() }
    val scheduleDialogState = rememberScheduleDialogState()
    val deleteDialogState = remember { DeleteDialogState() }

    val isMultiSelecting = when (currentHomePage) {
        HomePage.Notebook -> false
        HomePage.ACCOUNT -> {
            accountViewModel.isMultiSelecting.collectAsState().value
        }
        HomePage.Schedule -> false
        HomePage.Memorial -> false
        else -> false
    }

    LaunchedEffect(isMultiSelecting) {
        if (isMultiSelecting) {
            buttonState.expend()
        } else {
            buttonState.fold()
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            if (currentHomePage != HomePage.Home) {
                CenterAlignedTopAppBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    title = {
                        Text(text = stringResource(id = currentHomePage.labelRes))
                    }
                )
            } else {
                IndexTopBar(
                    modifier = Modifier.fillMaxWidth(),
                    state = indexTopBarState
                )
            }
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                HorizontalPager(
                    count = 5,
                    modifier = Modifier
                        .fillMaxSize()
                        .zIndex(-1F),
                    state = pagerState,
                    userScrollEnabled = scrollable
                ) { index ->
                    when (index) {
                        HOME -> {
                            IndexHome(
                                modifier = Modifier.fillMaxSize(),
                                state = indexScreenState,
                                onScheduleClick = { scheduleDialogState.show(it) }
                            )
                        }
                        NOTEBOOK -> {
                            NotebookHome(
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        ACCOUNT -> {
                            AccountHome(
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        SCHEDULE -> {
                            ScheduleHome(
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        MEMORIAL -> {
                            MemorialHome(
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
                if (currentHomePage != HomePage.Home) {
                    MovableActionButton(
                        modifier = Modifier.align(Alignment.BottomEnd),
                        state = buttonState,
                        mainButtonContent = {
                            Icon(
                                imageVector = if (isMultiSelecting) {
                                    Icons.Rounded.Delete
                                } else {
                                    Icons.Rounded.Add
                                },
                                contentDescription = "More"
                            )
                        },
                        onMainButtonClick = {
                            when (currentHomePage) {
                                HomePage.Notebook -> {
                                    NoteActivity.openAdd(context)
                                }
                                HomePage.ACCOUNT -> {
                                    if (isMultiSelecting) {
                                        deleteDialogState.show(onConfirm = {
                                            accountViewModel.transactionColumnState.value
                                                .deleteTransactions(
                                                    accountViewModel.transactionColumnState
                                                        .value.multiSelectedTransactions.toList()
                                                )
                                        })
                                    } else {
                                        TransactionActivity.openAdd(context)
                                    }
                                }
                                HomePage.Schedule -> {
                                    scheduleDialogState.show(Schedule())
                                }
                                HomePage.Memorial -> {
                                    MemorialActivity.openAdd(context)
                                }
                                else -> {}
                            }
                        },
                        topSubButtonContent = {
                            Icon(
                                imageVector = if (isMultiSelecting) {
                                    Icons.Rounded.DoneAll
                                } else {
                                    Icons.Rounded.Search
                                }, contentDescription = "search"
                            )
                        },
                        onTopSubButtonClick = {
                            when (currentHomePage) {
                                HomePage.Notebook -> {
                                    SearchActivity.open(context, SearchType.Note)
                                }
                                HomePage.ACCOUNT -> {
                                    if (isMultiSelecting) {
                                        accountViewModel.transactionColumnState
                                            .value.selectAllTransactions()
                                    } else {
                                        SearchActivity.open(context, SearchType.Transaction)
                                    }
                                }
                                HomePage.Schedule -> {
                                    SearchActivity.open(context, SearchType.Schedule)
                                }
                                HomePage.Memorial -> {
                                    SearchActivity.open(context, SearchType.Memorial)
                                }
                                else -> {}
                            }
                        },
                        bottomSubButtonContent = {
                            if (isMultiSelecting) {
                                Icon(
                                    imageVector = Icons.Rounded.RemoveDone,
                                    contentDescription = "Remove done"
                                )
                            } else when (currentHomePage) {
                                HomePage.ACCOUNT -> {
                                    Icon(
                                        imageVector = Icons.Rounded.DonutSmall,
                                        contentDescription = "Analysis"
                                    )
                                }
                                else -> {
                                    Icon(
                                        imageVector = Icons.Rounded.SubdirectoryArrowRight,
                                        contentDescription = "fold"
                                    )
                                }
                            }
                        },
                        onBottomSubButtonClick = {
                            when (currentHomePage) {
                                HomePage.ACCOUNT -> {
                                    if (isMultiSelecting) {
                                        accountViewModel.transactionColumnState
                                            .value.cancelMultiSelecting()
                                    } else {
                                        AccountAnalysisActivity.open(context)
                                    }
                                }
                                else -> scope.launch {
                                    buttonState.fold()
                                }
                            }
                        }
                    )

                    Box(modifier = Modifier.fillMaxSize().zIndex(-0.5F).run {
                        if (buttonState.isExpended.value && !isMultiSelecting) {
                            clickable(indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) { scope.launch { buttonState.fold() } }
                        } else this
                    })
                }
            }
        },
        bottomBar = {
            Box {
                BottomAppBar(
                    actions = {
                        for (homePage in HomePage.values()) {
                            NavigationBarItem(
                                selected = homePage == currentHomePage,
                                onClick = {
                                    scope.launch(Dispatchers.Main.immediate) {
                                        pagerState.scrollToPage(homePage.index)
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = homePage.icon,
                                        contentDescription = stringResource(id = homePage.labelRes)
                                    )
                                }
                            )
                        }
                    }
                )
                Box(modifier = Modifier.matchParentSize().zIndex(1F).run {
                    if (buttonState.isExpended.value && !isMultiSelecting) {
                        clickable(indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { scope.launch { buttonState.fold() } }
                    } else this
                })
            }
        }
    )

    DeleteDialog(state = deleteDialogState)

    ScheduleDialog(
        state = scheduleDialogState,
        onDelete = scheduleViewModel::deleteSchedule,
        onAdd = scheduleViewModel::addSchedule,
        onModify = scheduleViewModel::updateSchedule
    )
}