package little.goose.home

import android.app.Activity
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.util.Consumer
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.metrics.performance.PerformanceMetricsState
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import little.goose.common.constants.KEY_HOME_PAGE
import little.goose.home.ui.HomeScreen
import little.goose.home.ui.HomeViewModel
import little.goose.search.SearchType
import little.goose.ui.screen.LittleGooseEmptyScreen
import java.util.*

const val ROUTE_HOME = "home"

const val KEY_INIT_HOME_PAGE = "init_home_page"

fun NavController.navigateToHome(initPage: Int) {
    navigate("$ROUTE_HOME/$initPage") {
        popUpTo("$ROUTE_HOME/{$KEY_INIT_HOME_PAGE}")
        launchSingleTop = true
    }
}

fun NavGraphBuilder.homeRoute(
    onNavigateToSettings: () -> Unit,
    onNavigateToMemorialAdd: () -> Unit,
    onNavigateToMemorial: (memorialId: Long) -> Unit,
    onNavigateToNote: (noteId: Long?) -> Unit,
    onNavigateToSearch: (SearchType) -> Unit,
    onNavigateToTransactionScreen: (transactionId: Long) -> Unit,
    onNavigateToTransaction: (id: Long?, time: Date?) -> Unit,
    onNavigateToAccountAnalysis: () -> Unit
) {
    composable(
        route = "$ROUTE_HOME/{$KEY_INIT_HOME_PAGE}",
        arguments = listOf(
            navArgument(KEY_INIT_HOME_PAGE) {
                type = NavType.IntType
                defaultValue = -1
            }
        )
    ) {
        val context = LocalContext.current
        val initHomePage = it.arguments?.getInt(KEY_INIT_HOME_PAGE, -1)
            .takeIf { hp -> hp != -1 } // 1. 从路由参数中获取
            ?: (context as? Activity)?.intent?.getIntExtra(KEY_HOME_PAGE, -1) // 2. 从 intent 中获取
            ?: -1 // 3. 默认值
        HomeRoute(
            modifier = Modifier.fillMaxSize(),
            initHomePage = initHomePage,
            onNavigateToSettings = onNavigateToSettings,
            onNavigateToMemorialAdd = onNavigateToMemorialAdd,
            onNavigateToMemorial = onNavigateToMemorial,
            onNavigateToNote = onNavigateToNote,
            onNavigateToSearch = onNavigateToSearch,
            onNavigateToTransactionScreen = onNavigateToTransactionScreen,
            onNavigateToTransaction = onNavigateToTransaction,
            onNavigateToAccountAnalysis = onNavigateToAccountAnalysis
        )
    }
}

@Composable
fun HomeRoute(
    modifier: Modifier = Modifier,
    initHomePage: Int,
    onNavigateToSettings: () -> Unit,
    onNavigateToMemorialAdd: () -> Unit,
    onNavigateToMemorial: (memorialId: Long) -> Unit,
    onNavigateToTransactionScreen: (transactionId: Long) -> Unit,
    onNavigateToTransaction: (id: Long?, time: Date?) -> Unit,
    onNavigateToNote: (noteId: Long?) -> Unit,
    onNavigateToSearch: (SearchType) -> Unit,
    onNavigateToAccountAnalysis: () -> Unit
) {
    val viewModel = hiltViewModel<HomeViewModel>()
    val homePage by remember(initHomePage) {
        mutableIntStateOf(
            initHomePage.takeIf { it != -1 } ?: viewModel.dataStoreHomePage
        )
    }
    if (homePage == -1) {
        LittleGooseEmptyScreen(modifier = modifier)
    } else {
        val pagerState = rememberPagerState(initialPage = homePage, pageCount = { 5 })
        HomeScreen(
            modifier = modifier.fillMaxSize(),
            pagerState = pagerState,
            onNavigateToSettings = onNavigateToSettings,
            onNavigateToNote = onNavigateToNote,
            onNavigateToSearch = onNavigateToSearch,
            onNavigateToTransaction = onNavigateToTransaction,
            onNavigateToMemorialAdd = onNavigateToMemorialAdd,
            onNavigateToMemorial = onNavigateToMemorial,
            onNavigateToAccountAnalysis = onNavigateToAccountAnalysis,
            onNavigateToTransactionScreen = onNavigateToTransactionScreen
        )

        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        DisposableEffect(context, scope) {
            val activity = (context as ComponentActivity)
            val listener = Consumer<Intent> { intent: Intent? ->
                val newHomePage = intent?.getIntExtra(KEY_HOME_PAGE, -1)
                    ?.takeIf { it != -1 } ?: return@Consumer

                if (pagerState.currentPage != newHomePage) {
                    scope.launch(Dispatchers.Main.immediate) {
                        pagerState.scrollToPage(newHomePage)
                    }
                }
            }
            activity.addOnNewIntentListener(listener)
            onDispose {
                activity.removeOnNewIntentListener(listener)
            }
        }

        val view = LocalView.current
        val hierarchyStateHolder = remember(view) {
            PerformanceMetricsState.getHolderForHierarchy(view)
        }
        DisposableEffect(hierarchyStateHolder, pagerState.isScrollInProgress) {
            val key = "home_pager_scroll"
            if (pagerState.isScrollInProgress) {
                val msg = "current page: ${pagerState.currentPage}, " +
                        "offset: ${pagerState.currentPageOffsetFraction}"
                hierarchyStateHolder.state?.putState(key, msg)
            } else {
                hierarchyStateHolder.state?.removeState(key)
            }
            onDispose {
                hierarchyStateHolder.state?.removeState(key)
            }
        }

        DisposableEffect(pagerState.currentPage) {
            viewModel.updateHomePage(pagerState.currentPage)
            onDispose { }
        }
    }

}