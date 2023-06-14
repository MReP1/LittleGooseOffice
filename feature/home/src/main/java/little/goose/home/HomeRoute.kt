package little.goose.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable
import little.goose.home.ui.HomeScreen
import little.goose.search.SearchType
import java.util.Date

const val ROUTE_HOME = "home"

fun NavGraphBuilder.homeRoute(
    homePage: Int,
    onHomePageUpdate: (Int) -> Unit,
    onNavigateToMemorialAdd: () -> Unit,
    onNavigateToMemorialDialog: (memorialId: Long) -> Unit,
    onNavigateToNote: (noteId: Long?) -> Unit,
    onNavigateToSearch: (SearchType) -> Unit,
    onNavigateToTransactionDialog: (transactionId: Long) -> Unit,
    onNavigateToTransaction: (id: Long?, time: Date?) -> Unit,
    onNavigateToAccountAnalysis: () -> Unit,
    onNavigateToScheduleDialog: (Long?) -> Unit
) {
    composable(ROUTE_HOME) {
        HomeRoute(
            modifier = Modifier.fillMaxSize(),
            homePage = homePage,
            onHomePageUpdate = onHomePageUpdate,
            onNavigateToMemorialAdd = onNavigateToMemorialAdd,
            onNavigateToMemorialDialog = onNavigateToMemorialDialog,
            onNavigateToNote = onNavigateToNote,
            onNavigateToSearch = onNavigateToSearch,
            onNavigateToTransactionDialog = onNavigateToTransactionDialog,
            onNavigateToTransaction = onNavigateToTransaction,
            onNavigateToAccountAnalysis = onNavigateToAccountAnalysis,
            onNavigateToScheduleDialog = onNavigateToScheduleDialog
        )
    }
}

@Composable
fun HomeRoute(
    modifier: Modifier = Modifier,
    homePage: Int,
    onHomePageUpdate: (Int) -> Unit,
    onNavigateToMemorialAdd: () -> Unit,
    onNavigateToMemorialDialog: (memorialId: Long) -> Unit,
    onNavigateToTransactionDialog: (transactionId: Long) -> Unit,
    onNavigateToTransaction: (id: Long?, time: Date?) -> Unit,
    onNavigateToNote: (noteId: Long?) -> Unit,
    onNavigateToSearch: (SearchType) -> Unit,
    onNavigateToAccountAnalysis: () -> Unit,
    onNavigateToScheduleDialog: (Long?) -> Unit
) {
    val pagerState = rememberPagerState(initialPage = homePage)
    HomeScreen(
        modifier = modifier.fillMaxSize(),
        pagerState = pagerState,
        onNavigateToNote = onNavigateToNote,
        onNavigateToSearch = onNavigateToSearch,
        onNavigateToTransaction = onNavigateToTransaction,
        onNavigateToMemorialAdd = onNavigateToMemorialAdd,
        onNavigateToMemorialDialog = onNavigateToMemorialDialog,
        onNavigateToAccountAnalysis = onNavigateToAccountAnalysis,
        onNavigateToTransactionDialog = onNavigateToTransactionDialog,
        onNavigateToScheduleDialog = onNavigateToScheduleDialog
    )

    LaunchedEffect(homePage) {
        if (pagerState.currentPage != homePage) {
            pagerState.scrollToPage(homePage)
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        if (homePage != pagerState.currentPage) {
            onHomePageUpdate(pagerState.currentPage)
        }
    }

}