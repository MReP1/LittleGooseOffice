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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import little.goose.account.ui.AccountHomeState
import little.goose.design.system.theme.LocalWindowSizeClass
import little.goose.design.system.util.paddingCutout
import little.goose.home.data.HomePage
import little.goose.home.ui.index.IndexState
import little.goose.memorial.ui.MemorialHomeState
import little.goose.note.ui.notebook.NoteColumnState
import little.goose.search.SearchType
import java.time.format.TextStyle
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    modifier: Modifier,
    pagerState: PagerState,
    memorialHomeState: MemorialHomeState,
    noteColumnState: NoteColumnState,
    snackbarHostState: SnackbarHostState,
    indexState: IndexState,
    accountHomeState: AccountHomeState,
    onNavigateToSettings: () -> Unit,
    onNavigateToMemorialAdd: () -> Unit,
    onNavigateToMemorial: (memorialId: Long) -> Unit,
    onNavigateToTransactionScreen: (transactionId: Long) -> Unit,
    onNavigateToTransaction: (transactionId: Long?, date: Date?) -> Unit,
    onNavigateToNote: (noteId: Long?) -> Unit,
    onNavigateToSearch: (SearchType) -> Unit,
    onNavigateToAccountAnalysis: () -> Unit,
    noteAction: (little.goose.note.ui.notebook.NotebookIntent) -> Unit
) {
    val scope = rememberCoroutineScope()
    val currentHomePage = remember(pagerState.currentPage) {
        HomePage.fromPageIndex(pagerState.currentPage)
    }

    val windowSizeClass = LocalWindowSizeClass.current
    val isWindowWidthSizeCompat = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact
    Row(modifier = modifier.paddingCutout(!isWindowWidthSizeCompat)) {
        if (!isWindowWidthSizeCompat) {
            HomeNavigationRailBar(
                modifier = Modifier
                    .fillMaxHeight()
                    .wrapContentWidth(),
                currentHomePage = currentHomePage,
                currentDayText = indexState.indexTopBarState.currentDay.month.getDisplayName(
                    TextStyle.SHORT, Locale.CHINA
                ) + indexState.indexTopBarState.currentDay.dayOfMonth + "日",
                onAddClick = {
                    when (currentHomePage) {
                        HomePage.Home -> indexState.indexTopBarState.navigateToDate(indexState.indexTopBarState.today)
                        HomePage.Notebook -> onNavigateToNote(null)
                        HomePage.Account -> onNavigateToTransaction(null, Date())
                        HomePage.Memorial -> onNavigateToMemorialAdd()
                    }
                },
                todayText = indexState.indexTopBarState.today.dayOfMonth.toString(),
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
                if (isWindowWidthSizeCompat) {
                    HomeTopBar(
                        modifier = Modifier.fillMaxWidth(),
                        currentHomePage = currentHomePage,
                        indexTopBarState = indexState.indexTopBarState,
                        onNavigateToSettings = onNavigateToSettings
                    )
                }
            },
            content = { paddingValues ->
                HomePageContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    pagerState,
                    indexState.indexHomeState,
                    currentHomePage,
                    onNavigateToMemorialAdd,
                    onNavigateToMemorial,
                    onNavigateToTransactionScreen,
                    onNavigateToTransaction,
                    onNavigateToNote,
                    onNavigateToSearch,
                    onNavigateToAccountAnalysis,
                    noteColumnState,
                    memorialHomeState,
                    accountHomeState,
                    noteAction = noteAction
                )
            },
            bottomBar = {
                if (isWindowWidthSizeCompat) {
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