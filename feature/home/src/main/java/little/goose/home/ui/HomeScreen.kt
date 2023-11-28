package little.goose.home.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DonutSmall
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import little.goose.home.data.*
import little.goose.home.ui.component.IndexTopBar
import little.goose.memorial.ui.MemorialHome
import little.goose.memorial.ui.MemorialViewModel
import little.goose.note.ui.NotebookHome
import little.goose.note.ui.NotebookViewModel
import little.goose.search.SearchType
import java.util.*

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

                                HomePage.Account -> {
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

                                HomePage.Account -> {
                                    if (isMultiSelecting) {
                                        transactionColumnState.selectAllTransactions()
                                    } else {
                                        onNavigateToSearch(SearchType.Transaction)
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
                                HomePage.Account -> {
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
                                HomePage.Account -> {
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
                        for (homePage in HomePage.entries) {
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