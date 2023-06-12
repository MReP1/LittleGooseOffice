package little.goose.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.edit
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable
import little.goose.common.utils.getDataOrDefault
import little.goose.home.data.HOME
import little.goose.home.ui.HomeScreen
import little.goose.home.utils.KEY_PREF_PAGER
import little.goose.home.utils.homeDataStore
import little.goose.search.SearchType
import java.util.Date

var isHomePageInit = false

var homePage by mutableStateOf(-1)

const val ROUTE_HOME = "home"

fun NavGraphBuilder.homeRoute(
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

sealed interface HomeRouteState {
    object Loading : HomeRouteState
    data class Success(val page: Int) : HomeRouteState
}

@Composable
fun HomeRoute(
    modifier: Modifier = Modifier,
    onNavigateToMemorialAdd: () -> Unit,
    onNavigateToMemorialDialog: (memorialId: Long) -> Unit,
    onNavigateToTransactionDialog: (transactionId: Long) -> Unit,
    onNavigateToTransaction: (id: Long?, time: Date?) -> Unit,
    onNavigateToNote: (noteId: Long?) -> Unit,
    onNavigateToSearch: (SearchType) -> Unit,
    onNavigateToAccountAnalysis: () -> Unit,
    onNavigateToScheduleDialog: (Long?) -> Unit
) {
    val context = LocalContext.current
    val homeState = if (homePage >= 0) remember {
        mutableStateOf(HomeRouteState.Success(homePage))
    } else produceState<HomeRouteState>(initialValue = HomeRouteState.Loading) {
        val page = context.homeDataStore.getDataOrDefault(KEY_PREF_PAGER, HOME)
        isHomePageInit = true
        value = HomeRouteState.Success(page)
    }

    when (val state = homeState.value) {
        HomeRouteState.Loading -> {
            // TODO
        }

        is HomeRouteState.Success -> {
            val pagerState = rememberPagerState(initialPage = state.page)
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
            LaunchedEffect(pagerState.currentPage) {
                homePage = pagerState.currentPage
                context.homeDataStore.edit { preferences ->
                    preferences[KEY_PREF_PAGER] = pagerState.currentPage
                }
            }
        }
    }

}