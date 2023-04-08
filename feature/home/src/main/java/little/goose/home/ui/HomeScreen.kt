package little.goose.home.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.datastore.preferences.core.edit
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import little.goose.account.ui.AccountHome
import little.goose.account.ui.AccountHomeViewModel
import little.goose.account.ui.analysis.AccountAnalysisActivity
import little.goose.account.ui.transaction.TransactionActivity
import little.goose.account.ui.transaction.TransactionDialog
import little.goose.account.ui.transaction.rememberTransactionDialogState
import little.goose.design.system.component.MovableActionButton
import little.goose.design.system.component.MovableActionButtonState
import little.goose.design.system.component.dialog.DeleteDialog
import little.goose.design.system.component.dialog.DeleteDialogState
import little.goose.home.R
import little.goose.home.data.*
import little.goose.home.ui.component.IndexTopBar
import little.goose.home.utils.KEY_PREF_PAGER
import little.goose.home.utils.homeDataStore
import little.goose.memorial.ui.*
import little.goose.note.ui.NotebookHome
import little.goose.note.ui.NotebookViewModel
import little.goose.note.ui.note.NoteActivity
import little.goose.schedule.data.entities.Schedule
import little.goose.schedule.ui.ScheduleDialog
import little.goose.schedule.ui.ScheduleHome
import little.goose.schedule.ui.ScheduleViewModel
import little.goose.schedule.ui.rememberScheduleDialogState
import little.goose.search.SearchActivity
import little.goose.search.SearchType

@Composable
fun HomeScreen(
    modifier: Modifier,
    initPage: Int
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(initPage)
    val scrollable by remember { derivedStateOf { pagerState.currentPage != HOME } }
    val currentHomePage = remember(pagerState.currentPage) {
        HomePage.fromPageIndex(pagerState.currentPage)
    }

    val scheduleViewModel = hiltViewModel<ScheduleViewModel>()
    val indexViewModel = hiltViewModel<IndexViewModel>()
    val accountViewModel = hiltViewModel<AccountHomeViewModel>()
    val memorialViewModel = hiltViewModel<MemorialViewModel>()
    val notebookViewModel = hiltViewModel<NotebookViewModel>()

    val indexScreenState by indexViewModel.indexScreenState.collectAsState()
    val indexTopBarState by indexViewModel.indexTopBarState.collectAsState()

    val buttonState = remember { MovableActionButtonState() }
    val scheduleDialogState = rememberScheduleDialogState()
    val transactionDialogState = rememberTransactionDialogState()
    val memorialDialogState = rememberMemorialDialogState()
    val deleteDialogState = remember { DeleteDialogState() }

    val snackbarHostState = remember { SnackbarHostState() }

    val transactionColumnState by accountViewModel.transactionColumnState.collectAsState()
    val memorialColumnState by memorialViewModel.memorialColumnState.collectAsState()
    val scheduleColumnState by scheduleViewModel.scheduleColumnState.collectAsState()
    val noteGridState by notebookViewModel.noteGridState.collectAsState()

    val isMultiSelecting = when (currentHomePage) {
        HomePage.Notebook -> noteGridState.isMultiSelecting
        HomePage.ACCOUNT -> transactionColumnState.isMultiSelecting
        HomePage.Schedule -> scheduleColumnState.isMultiSelecting
        HomePage.Memorial -> memorialColumnState.isMultiSelecting
        else -> false
    }

    LaunchedEffect(pagerState.currentPage) {
        context.homeDataStore.edit { preferences ->
            preferences[KEY_PREF_PAGER] = pagerState.currentPage
        }
    }

    LaunchedEffect(isMultiSelecting) {
        if (isMultiSelecting) {
            buttonState.expend()
        } else {
            buttonState.fold()
        }
    }

    LaunchedEffect(scheduleViewModel.event) {
        scheduleViewModel.event.collect { event ->
            when (event) {
                is ScheduleViewModel.Event.DeleteSchedules -> {
                    snackbarHostState.showSnackbar(
                        message = context.getString(R.string.deleted),
                        withDismissAction = true
                    )
                }
            }
        }
    }

    LaunchedEffect(accountViewModel.event) {
        accountViewModel.event.collect { event ->
            when (event) {
                is AccountHomeViewModel.Event.DeleteTransactions -> {
                    snackbarHostState.showSnackbar(
                        message = context.getString(R.string.deleted),
                        withDismissAction = true
                    )
                }
            }
        }
    }

    LaunchedEffect(memorialViewModel.event) {
        memorialViewModel.event.collect { event ->
            when (event) {
                is MemorialViewModel.Event.DeleteMemorials -> {
                    snackbarHostState.showSnackbar(
                        message = context.getString(R.string.deleted),
                        withDismissAction = true
                    )
                }
            }
        }
    }

    LaunchedEffect(notebookViewModel.event) {
        notebookViewModel.event.collect { event ->
            when (event) {
                is NotebookViewModel.Event.DeleteNotes -> {
                    snackbarHostState.showSnackbar(
                        message = context.getString(R.string.deleted),
                        withDismissAction = true
                    )
                }
            }
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) {
                Snackbar(snackbarData = it)
            }
        },
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
                    pageCount = 5,
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
                                onScheduleClick = scheduleDialogState::show,
                                onTransactionClick = transactionDialogState::show,
                                onMemorialClick = memorialDialogState::show
                            )
                        }
                        NOTEBOOK -> {
                            NotebookHome(
                                modifier = Modifier.fillMaxSize(),
                                noteGridState = noteGridState,
                            )
                        }
                        ACCOUNT -> {
                            val accountTitleState by accountViewModel.accountTitleState.collectAsState()
                            val monthSelectorState by accountViewModel.monthSelectorState.collectAsState()
                            AccountHome(
                                modifier = Modifier.fillMaxSize(),
                                transactionColumnState = transactionColumnState,
                                accountTitleState = accountTitleState,
                                monthSelectorState = monthSelectorState,
                                deleteTransaction = accountViewModel::deleteTransaction
                            )
                        }
                        SCHEDULE -> {
                            ScheduleHome(
                                modifier = Modifier.fillMaxSize(),
                                scheduleColumnState = scheduleColumnState,
                                deleteSchedule = scheduleViewModel::deleteSchedule,
                                addSchedule = scheduleViewModel::addSchedule,
                                modifySchedule = scheduleViewModel::updateSchedule
                            )
                        }
                        MEMORIAL -> {
                            val topMemorial by memorialViewModel.topMemorial.collectAsState()
                            MemorialHome(
                                modifier = Modifier.fillMaxSize(),
                                topMemorial = topMemorial,
                                memorialColumnState = memorialColumnState,
                                deleteMemorial = memorialViewModel::deleteMemorial
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
                                    if (isMultiSelecting) {
                                        deleteDialogState.show(onConfirm = {
                                            noteGridState.deleteNotes(
                                                noteGridState.multiSelectedNotes.toList()
                                            )
                                            noteGridState.cancelMultiSelecting()
                                        })
                                    } else {
                                        NoteActivity.openAdd(context)
                                    }
                                }
                                HomePage.ACCOUNT -> {
                                    if (isMultiSelecting) {
                                        deleteDialogState.show(onConfirm = {
                                            transactionColumnState.deleteTransactions(
                                                transactionColumnState
                                                    .multiSelectedTransactions.toList()
                                            )
                                            transactionColumnState.cancelMultiSelecting()
                                        })
                                    } else {
                                        TransactionActivity.openAdd(context)
                                    }
                                }
                                HomePage.Schedule -> {
                                    if (isMultiSelecting) {
                                        deleteDialogState.show(onConfirm = {
                                            scheduleColumnState.deleteSchedules(
                                                scheduleColumnState.multiSelectedSchedules.toList()
                                            )
                                            scheduleColumnState.cancelMultiSelecting()
                                        })
                                    } else {
                                        scheduleDialogState.show(Schedule())
                                    }
                                }
                                HomePage.Memorial -> {
                                    if (isMultiSelecting) {
                                        deleteDialogState.show(onConfirm = {
                                            memorialColumnState.deleteMemorials(
                                                memorialColumnState.multiSelectedMemorials.toList()
                                            )
                                            memorialColumnState.cancelMultiSelecting()
                                        })
                                    } else {
                                        MemorialActivity.openAdd(context)
                                    }
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
                                },
                                contentDescription = if (isMultiSelecting) {
                                    "Select all"
                                } else {
                                    "Search"
                                }
                            )
                        },
                        onTopSubButtonClick = {
                            when (currentHomePage) {
                                HomePage.Notebook -> {
                                    if (isMultiSelecting) {
                                        noteGridState.selectAllNotes()
                                    } else {
                                        SearchActivity.open(context, SearchType.Note)
                                    }
                                }
                                HomePage.ACCOUNT -> {
                                    if (isMultiSelecting) {
                                        transactionColumnState.selectAllTransactions()
                                    } else {
                                        SearchActivity.open(context, SearchType.Transaction)
                                    }
                                }
                                HomePage.Schedule -> {
                                    if (isMultiSelecting) {
                                        scheduleColumnState.selectAllSchedules()
                                    } else {
                                        SearchActivity.open(context, SearchType.Schedule)
                                    }
                                }
                                HomePage.Memorial -> {
                                    if (isMultiSelecting) {
                                        memorialColumnState.selectAllMemorial()
                                    } else {
                                        SearchActivity.open(context, SearchType.Memorial)
                                    }
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
                                        transactionColumnState.cancelMultiSelecting()
                                    } else {
                                        AccountAnalysisActivity.open(context)
                                    }
                                }
                                HomePage.Memorial -> {
                                    if (isMultiSelecting) {
                                        memorialColumnState.cancelMultiSelecting()
                                    } else scope.launch {
                                        buttonState.fold()
                                    }
                                }
                                HomePage.Schedule -> {
                                    if (isMultiSelecting) {
                                        scheduleColumnState.cancelMultiSelecting()
                                    } else scope.launch {
                                        buttonState.fold()
                                    }
                                }
                                HomePage.Notebook -> {
                                    if (isMultiSelecting) {
                                        noteGridState.cancelMultiSelecting()
                                    } else scope.launch {
                                        buttonState.fold()
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
                                        modifier = Modifier.size(32.dp),
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

    TransactionDialog(
        state = transactionDialogState,
        onDelete = accountViewModel::deleteTransaction
    )

    MemorialDialog(
        state = memorialDialogState,
        onDelete = memorialViewModel::deleteMemorial
    )
}