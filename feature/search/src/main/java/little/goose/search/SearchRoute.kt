package little.goose.search

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import little.goose.note.ui.search.SearchNoteRoute
import little.goose.search.memorial.SearchMemorialRoute
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
) { entry ->
    val searchType = rememberSaveable(entry, saver = SearchType.saver) {
        SearchType.fromValue(entry.arguments!!.getInt(SearchType.KEY_SEARCH_TYPE))
    }
    SearchRoute(
        modifier = Modifier.fillMaxSize(),
        searchType = searchType,
        onNavigateToNote = onNavigateToNote,
        onNavigateToMemorial = onNavigateToMemorial,
        onNavigateToTransactionScreen = onNavigateToTransactionScreen,
        onBack = onBack
    )
}

@Composable
fun SearchRoute(
    modifier: Modifier = Modifier,
    searchType: SearchType,
    onNavigateToMemorial: (memorialId: Long) -> Unit,
    onNavigateToTransactionScreen: (transactionId: Long) -> Unit,
    onNavigateToNote: (noteId: Long) -> Unit,
    onBack: () -> Unit
) {
    when (searchType) {
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

        SearchType.Transaction -> SearchTransactionRoute(
            modifier = modifier,
            onNavigateToTransactionScreen = onNavigateToTransactionScreen,
            onBack = onBack
        )
    }
}