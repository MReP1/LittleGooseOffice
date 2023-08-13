package little.goose.search

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import little.goose.search.memorial.SearchMemorialRoute
import little.goose.search.note.SearchNoteRoute
import little.goose.search.schedule.SearchScheduleRoute
import little.goose.search.transaction.SearchTransactionRoute

const val ROUTE_SEARCH = "search"

fun NavController.navigateToSearch(type: SearchType) {
    navigate("$ROUTE_SEARCH/${type.value}") {
        launchSingleTop = true
    }
}

fun NavGraphBuilder.searchRoute(
    onNavigateToNote: (noteId: Long) -> Unit,
    onNavigateToMemorial: (memorialId: Long) -> Unit,
    onNavigateToTransactionScreen: (transactionId: Long) -> Unit,
    onNavigateToScheduleDialog: (scheduleId: Long) -> Unit,
    onBack: () -> Unit
) = composable(
    route = "$ROUTE_SEARCH/{${SearchType.KEY_SEARCH_TYPE}}",
    arguments = listOf(
        navArgument(name = SearchType.KEY_SEARCH_TYPE) {
            type = NavType.IntType
            defaultValue = 0
        }
    ),
    enterTransition = {
        fadeIn(
            animationSpec = tween(200, easing = LinearOutSlowInEasing)
        ) + slideIntoContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.Down,
            animationSpec = tween(200, easing = LinearOutSlowInEasing),
            initialOffset = { it / 6 }
        )
    },
    exitTransition = null,
    popExitTransition = {
        fadeOut(
            animationSpec = tween(200, easing = FastOutLinearInEasing)
        ) + slideOutOfContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.Up,
            animationSpec = tween(200, easing = FastOutLinearInEasing),
            targetOffset = { it / 6 }
        )
    },
    popEnterTransition = null
) {
    SearchRoute(
        modifier = Modifier.fillMaxSize(),
        onNavigateToNote = onNavigateToNote,
        onNavigateToMemorial = onNavigateToMemorial,
        onNavigateToTransactionScreen = onNavigateToTransactionScreen,
        onNavigateToScheduleDialog = onNavigateToScheduleDialog,
        onBack = onBack
    )
}

@Composable
fun SearchRoute(
    modifier: Modifier = Modifier,
    onNavigateToMemorial: (memorialId: Long) -> Unit,
    onNavigateToTransactionScreen: (transactionId: Long) -> Unit,
    onNavigateToNote: (noteId: Long) -> Unit,
    onNavigateToScheduleDialog: (Long) -> Unit,
    onBack: () -> Unit
) {
    val viewModel = hiltViewModel<SearchViewModel>()
    when (viewModel.searchType) {
        SearchType.Memorial -> SearchMemorialRoute(
            modifier = modifier,
            onNavigateToMemorialDialog = onNavigateToMemorial,
            onBack = onBack
        )

        SearchType.Note -> SearchNoteRoute(
            modifier = modifier,
            onNavigateToNote = onNavigateToNote,
            onBack = onBack
        )

        SearchType.Schedule -> SearchScheduleRoute(
            modifier = modifier,
            onNavigateToScheduleDialog = onNavigateToScheduleDialog,
            onBack = onBack
        )

        SearchType.Transaction -> SearchTransactionRoute(
            modifier = modifier,
            onNavigateToTransactionScreen = onNavigateToTransactionScreen,
            onBack = onBack
        )
    }
}