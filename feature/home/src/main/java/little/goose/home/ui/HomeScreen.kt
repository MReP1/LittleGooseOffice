package little.goose.home.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DonutSmall
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DoneAll
import androidx.compose.material.icons.rounded.RemoveDone
import androidx.compose.material.icons.rounded.Settings
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
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.merge
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
import little.goose.home.ui.component.IndexTopBar
import little.goose.memorial.ui.MemorialHome
import little.goose.memorial.ui.MemorialViewModel
import little.goose.note.ui.NotebookHome
import little.goose.note.ui.NotebookViewModel
import little.goose.search.SearchType
import java.util.Date

@Composable
fun HomeScreen(
    modifier: Modifier,
    pagerState: PagerState,
    onNavigateToSettings: () -> Unit,
    onNavigateToMemorialAdd: () -> Unit,
    onNavigateToMemorial: (memorialId: Long) -> Unit,
    onNavigateToTransactionScreen: (transactionId: Long) -> Unit,
    onNavigateToTransaction: (transactionId: Long?, date: Date?) -> Unit,
    onNavigateToNote: (noteId: Long?) -> Unit,
    onNavigateToSearch: (SearchType) -> Unit,
    onNavigateToAccountAnalysis: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val currentHomePage = remember(pagerState.currentPage) {
        HomePage.fromPageIndex(pagerState.currentPage)
    }

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
    val noteColumnState by notebookViewModel.noteColumnState.collectAsState()

    val isMultiSelecting = when (currentHomePage) {
        HomePage.Notebook -> noteColumnState.isMultiSelecting
        HomePage.Account -> transactionColumnState.isMultiSelecting
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

    LaunchedEffect(accountViewModel.event, memorialViewModel.event, notebookViewModel.event) {
        merge(
            accountViewModel.event, memorialViewModel.event, notebookViewModel.event
        ).collect { event ->
            when (event) {
                is AccountHomeViewModel.Event.DeleteTransactions,
                is MemorialViewModel.Event.DeleteMemorials,
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
                        if (currentHomePage == HomePage.Account) {
                            IconButton(onClick = onNavigateToAccountAnalysis) {
                                Icon(
                                    imageVector = Icons.Outlined.DonutSmall,
                                    contentDescription = "Analysis"
                                )
                            }
                        }
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
                    modifier = Modifier.fillMaxSize(),
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
                                onTransactionClick = { transaction ->
                                    transaction.id?.run(onNavigateToTransactionScreen)
                                },
                                onMemorialClick = { memorial ->
                                    memorial.id?.let(onNavigateToMemorial)
                                }
                            )
                        }

                        NOTEBOOK -> {
                            NotebookHome(
                                modifier = Modifier.fillMaxSize(),
                                noteColumnState = noteColumnState,
                                onNavigateToNote = onNavigateToNote,
                                onNavigateToSearch = { onNavigateToSearch(SearchType.Note) }
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
                                onNavigateToTransactionScreen = onNavigateToTransactionScreen,
                                onNavigateToSearch = { onNavigateToSearch(SearchType.Transaction) },
                                onNavigateToAccountAnalysis = onNavigateToAccountAnalysis
                            )
                        }

                        MEMORIAL -> {
                            val topMemorial by memorialViewModel.topMemorial.collectAsState()
                            MemorialHome(
                                modifier = Modifier.fillMaxSize(),
                                topMemorial = topMemorial,
                                memorialColumnState = memorialColumnState,
                                onNavigateToMemorial = onNavigateToMemorial,
                                onNavigateToSearch = { onNavigateToSearch(SearchType.Memorial) }
                            )
                        }
                    }
                }
                if (currentHomePage != HomePage.Home) {
                    MovableActionButton(
                        modifier = Modifier.align(Alignment.BottomEnd),
                        state = buttonState,
                        needToExpand = isMultiSelecting,
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
                            when {
                                currentHomePage == HomePage.Notebook && isMultiSelecting -> {
                                    deleteDialogState.show(onConfirm = {
                                        noteColumnState.deleteNotes(
                                            noteColumnState.multiSelectedNotes.toList()
                                        )
                                        noteColumnState.cancelMultiSelecting()
                                    })
                                }

                                currentHomePage == HomePage.Notebook && !isMultiSelecting -> {
                                    onNavigateToNote(null)
                                }

                                currentHomePage == HomePage.Account && isMultiSelecting -> {
                                    deleteDialogState.show(onConfirm = {
                                        transactionColumnState.deleteTransactions(
                                            transactionColumnState
                                                .multiSelectedTransactions.toList()
                                        )
                                        transactionColumnState.cancelMultiSelecting()
                                    })
                                }

                                currentHomePage == HomePage.Account && !isMultiSelecting -> {
                                    onNavigateToTransaction(null, Date())
                                }

                                currentHomePage == HomePage.Memorial && isMultiSelecting -> {
                                    deleteDialogState.show(onConfirm = {
                                        memorialColumnState.deleteMemorials(
                                            memorialColumnState.multiSelectedMemorials.toList()
                                        )
                                        memorialColumnState.cancelMultiSelecting()
                                    })
                                }

                                currentHomePage == HomePage.Memorial && !isMultiSelecting -> {
                                    onNavigateToMemorialAdd()
                                }

                                else -> {}
                            }
                        },
                        topSubButtonContent = {
                            Icon(
                                imageVector = Icons.Rounded.DoneAll,
                                contentDescription = "Select all"
                            )
                        },
                        onTopSubButtonClick = {
                            when (currentHomePage) {
                                HomePage.Notebook -> noteColumnState.selectAllNotes()
                                HomePage.Account -> transactionColumnState.selectAllTransactions()
                                HomePage.Memorial -> memorialColumnState.selectAllMemorial()
                                else -> {}
                            }
                        },
                        bottomSubButtonContent = {
                            Icon(
                                imageVector = Icons.Rounded.RemoveDone,
                                contentDescription = "Remove done"
                            )
                        },
                        onBottomSubButtonClick = {
                            when (currentHomePage) {
                                HomePage.Account -> transactionColumnState.cancelMultiSelecting()
                                HomePage.Memorial -> memorialColumnState.cancelMultiSelecting()
                                HomePage.Notebook -> noteColumnState.cancelMultiSelecting()
                                else -> scope.launch { buttonState.fold() }
                            }
                        }
                    )
                }
            }
        },
        bottomBar = {
            HomeBottomBar(
                currentHomePage = currentHomePage,
                homePageList = HomePage.entries,
                onHomePageClick = { homePage ->
                    scope.launch(Dispatchers.Main.immediate) {
                        pagerState.scrollToPage(homePage.index)
                    }
                }
            )
        }
    )

    DeleteDialog(state = deleteDialogState)
}

@Composable
private fun HomeBottomBar(
    currentHomePage: HomePage,
    homePageList: List<HomePage>,
    onHomePageClick: (HomePage) -> Unit
) {
    BottomAppBar(
        actions = {
            for (homePage in homePageList) {
                NavigationBarItem(
                    selected = homePage == currentHomePage,
                    onClick = { onHomePageClick(homePage) },
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
}