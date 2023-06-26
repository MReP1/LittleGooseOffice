package little.goose.home.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DoneAll
import androidx.compose.material.icons.rounded.DonutSmall
import androidx.compose.material.icons.rounded.RemoveDone
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.SubdirectoryArrowRight
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import little.goose.account.ui.AccountHome
import little.goose.account.ui.AccountHomeViewModel
import little.goose.design.system.component.MovableActionButton
import little.goose.design.system.component.MovableActionButtonState
import little.goose.design.system.component.dialog.DeleteDialog
import little.goose.design.system.component.dialog.DeleteDialogState
import little.goose.home.data.ACCOUNT
import little.goose.home.data.HOME
import little.goose.home.data.HomePage
import little.goose.home.data.MEMORIAL
import little.goose.home.data.NOTEBOOK
import little.goose.home.data.SCHEDULE
import little.goose.home.ui.component.IndexTopBar
import little.goose.memorial.ui.MemorialHome
import little.goose.memorial.ui.MemorialViewModel
import little.goose.note.ui.NotebookHome
import little.goose.note.ui.NotebookViewModel
import little.goose.schedule.ui.ScheduleHome
import little.goose.schedule.ui.ScheduleHomeViewModel
import little.goose.search.SearchType
import java.util.Date

@Composable
fun HomeScreen(
    modifier: Modifier,
    pagerState: PagerState,
    onNavigateToSettings: () -> Unit,
    onNavigateToMemorialAdd: () -> Unit,
    onNavigateToMemorialShow: (memorialId: Long) -> Unit,
    onNavigateToTransactionScreen: (transactionId: Long) -> Unit,
    onNavigateToTransaction: (transactionId: Long?, date: Date?) -> Unit,
    onNavigateToNote: (noteId: Long?) -> Unit,
    onNavigateToSearch: (SearchType) -> Unit,
    onNavigateToAccountAnalysis: () -> Unit,
    onNavigateToScheduleDialog: (Long?) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val currentHomePage = remember(pagerState.currentPage) {
        HomePage.fromPageIndex(pagerState.currentPage)
    }

    val scheduleHomeViewModel = hiltViewModel<ScheduleHomeViewModel>()
    val indexViewModel = hiltViewModel<IndexViewModel>()
    val accountViewModel = hiltViewModel<AccountHomeViewModel>()
    val memorialViewModel = hiltViewModel<MemorialViewModel>()
    val notebookViewModel = hiltViewModel<NotebookViewModel>()

    val indexScreenState by indexViewModel.indexScreenState.collectAsState()
    val indexTopBarState by indexViewModel.indexTopBarState.collectAsState()

    val buttonState = remember { MovableActionButtonState() }
    val deleteDialogState = remember { DeleteDialogState() }

    val snackbarHostState = remember { SnackbarHostState() }

    val transactionColumnState by accountViewModel.transactionColumnState.collectAsState()
    val memorialColumnState by memorialViewModel.memorialColumnState.collectAsState()
    val scheduleColumnState by scheduleHomeViewModel.scheduleColumnState.collectAsState()
    val noteColumnState by notebookViewModel.noteColumnState.collectAsState()

    val isMultiSelecting = when (currentHomePage) {
        HomePage.Notebook -> noteColumnState.isMultiSelecting
        HomePage.ACCOUNT -> transactionColumnState.isMultiSelecting
        HomePage.Schedule -> scheduleColumnState.isMultiSelecting
        HomePage.Memorial -> memorialColumnState.isMultiSelecting
        else -> false
    }

    LaunchedEffect(isMultiSelecting) {
        if (isMultiSelecting) {
            buttonState.expend()
        } else {
            buttonState.fold()
        }
    }

    LaunchedEffect(scheduleHomeViewModel.event) {
        scheduleHomeViewModel.event.collect { event ->
            when (event) {
                is ScheduleHomeViewModel.Event.DeleteSchedules -> {
                    snackbarHostState.showSnackbar(
                        message = context.getString(little.goose.common.R.string.deleted),
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
                        message = context.getString(little.goose.common.R.string.deleted),
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
                        message = context.getString(little.goose.common.R.string.deleted),
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
                        message = context.getString(little.goose.common.R.string.deleted),
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
                    },
                    actions = {
                        IconButton(onClick = onNavigateToSettings) {
                            Icon(
                                imageVector = Icons.Rounded.Settings,
                                contentDescription = stringResource(
                                    id = little.goose.settings.R.string.settings
                                )
                            )
                        }
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
                    userScrollEnabled = true
                ) { index ->
                    when (index) {
                        HOME -> {
                            IndexHome(
                                modifier = Modifier.fillMaxSize(),
                                state = indexScreenState,
                                onTransactionAdd = { time ->
                                    onNavigateToTransaction(null, time)
                                },
                                onScheduleClick = { schedule ->
                                    onNavigateToScheduleDialog(schedule.id)
                                },
                                onTransactionClick = { transaction ->
                                    transaction.id?.run(onNavigateToTransactionScreen)
                                },
                                onMemorialClick = { memorial ->
                                    memorial.id?.let(onNavigateToMemorialShow)
                                }
                            )
                        }

                        NOTEBOOK -> {
                            NotebookHome(
                                modifier = Modifier.fillMaxSize(),
                                noteColumnState = noteColumnState,
                                onNavigateToNote = onNavigateToNote,
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
                                onNavigateToTransactionScreen = onNavigateToTransactionScreen
                            )
                        }

                        SCHEDULE -> {
                            ScheduleHome(
                                modifier = Modifier.fillMaxSize(),
                                scheduleColumnState = scheduleColumnState,
                                onNavigateToScheduleDialog = onNavigateToScheduleDialog
                            )
                        }

                        MEMORIAL -> {
                            val topMemorial by memorialViewModel.topMemorial.collectAsState()
                            MemorialHome(
                                modifier = Modifier.fillMaxSize(),
                                topMemorial = topMemorial,
                                memorialColumnState = memorialColumnState,
                                onNavigateToMemorialShow = onNavigateToMemorialShow
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
                                            noteColumnState.deleteNotes(
                                                noteColumnState.multiSelectedNotes.toList()
                                            )
                                            noteColumnState.cancelMultiSelecting()
                                        })
                                    } else {
                                        onNavigateToNote(null)
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
                                        onNavigateToTransaction(null, Date())
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
                                        onNavigateToScheduleDialog(null)
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
                                        onNavigateToMemorialAdd()
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
                                        noteColumnState.selectAllNotes()
                                    } else {
                                        onNavigateToSearch(SearchType.Note)
                                    }
                                }

                                HomePage.ACCOUNT -> {
                                    if (isMultiSelecting) {
                                        transactionColumnState.selectAllTransactions()
                                    } else {
                                        onNavigateToSearch(SearchType.Transaction)
                                    }
                                }

                                HomePage.Schedule -> {
                                    if (isMultiSelecting) {
                                        scheduleColumnState.selectAllSchedules()
                                    } else {
                                        onNavigateToSearch(SearchType.Schedule)
                                    }
                                }

                                HomePage.Memorial -> {
                                    if (isMultiSelecting) {
                                        memorialColumnState.selectAllMemorial()
                                    } else {
                                        onNavigateToSearch(SearchType.Memorial)
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
                                        onNavigateToAccountAnalysis()
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
                                        noteColumnState.cancelMultiSelecting()
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
}