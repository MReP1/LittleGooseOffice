package little.goose.home.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import little.goose.account.ui.AccountHomeViewModel
import little.goose.design.system.theme.LocalWindowSizeClass
import little.goose.home.data.HomePage
import little.goose.home.ui.index.IndexViewModel
import little.goose.memorial.ui.MemorialViewModel
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

    val snackbarHostState = remember { SnackbarHostState() }

    val transactionColumnState by accountViewModel.transactionColumnState.collectAsState()
    val memorialColumnState by memorialViewModel.memorialColumnState.collectAsState()
    val noteColumnState by notebookViewModel.noteColumnState.collectAsState()

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

    val windowSizeClass = LocalWindowSizeClass.current
    val isWindowSizeCompat = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact
    Row(modifier = modifier) {
        if (!isWindowSizeCompat) {
            HomeNavigationRailBar(
                modifier = Modifier
                    .fillMaxHeight()
                    .wrapContentWidth(),
                currentHomePage = currentHomePage,
                onHomePageClick = { homePage ->
                    scope.launch(Dispatchers.Main.immediate) {
                        pagerState.scrollToPage(homePage.index)
                    }
                },
                onNavigateToAccountAnalysis = onNavigateToAccountAnalysis,
                onNavigateToSettings = onNavigateToSettings
            )
        }
        Scaffold(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1F),
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState) {
                    Snackbar(snackbarData = it)
                }
            },
            topBar = {
                if (isWindowSizeCompat) {
                    val indexTopBarState by indexViewModel.indexTopBarState.collectAsState()
                    HomeTopBar(
                        modifier = Modifier.fillMaxWidth(),
                        currentHomePage = currentHomePage,
                        indexTopBarState = indexTopBarState,
                        onNavigateToSettings = onNavigateToSettings,
                        onNavigateToAccountAnalysis = onNavigateToAccountAnalysis
                    )
                }
            },
            content = { paddingValues ->
                val indexHomeState by indexViewModel.indexHomeState.collectAsState()
                val accountTitleState by accountViewModel.accountTitleState.collectAsState()
                val monthSelectorState by accountViewModel.monthSelectorState.collectAsState()
                val topMemorial by memorialViewModel.topMemorial.collectAsState()
                HomePageContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    pagerState,
                    indexHomeState,
                    currentHomePage,
                    onNavigateToMemorialAdd,
                    onNavigateToMemorial,
                    onNavigateToTransactionScreen,
                    onNavigateToTransaction,
                    onNavigateToNote,
                    onNavigateToSearch,
                    onNavigateToAccountAnalysis,
                    noteColumnState,
                    transactionColumnState,
                    memorialColumnState,
                    topMemorial,
                    accountTitleState,
                    monthSelectorState
                )
            },
            bottomBar = {
                if (isWindowSizeCompat) {
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
            }
        )
    }
}