package little.goose.search

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.composable
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
    onNavigateToMemorialShow: (memorialId: Long) -> Unit,
    onNavigateToTransaction: (transactionId: Long) -> Unit,
    onBack: () -> Unit
) = composable(
    route = "$ROUTE_SEARCH/{${SearchType.KEY_SEARCH_TYPE}}",
    arguments = listOf(
        navArgument(name = SearchType.KEY_SEARCH_TYPE) {
            type = NavType.IntType
            defaultValue = 0
        }
    )
) {
    SearchRoute(
        modifier = Modifier.fillMaxSize(),
        onNavigateToNote = onNavigateToNote,
        onNavigateToMemorialShow = onNavigateToMemorialShow,
        onNavigateToTransaction = onNavigateToTransaction,
        onBack = onBack
    )
}

@Composable
fun SearchRoute(
    modifier: Modifier = Modifier,
    onNavigateToMemorialShow: (memorialId: Long) -> Unit,
    onNavigateToTransaction: (transactionId: Long) -> Unit,
    onNavigateToNote: (noteId: Long) -> Unit,
    onBack: () -> Unit
) {
    val viewModel = hiltViewModel<SearchViewModel>()
    when (viewModel.searchType) {
        SearchType.Memorial -> SearchMemorialRoute(
            modifier = modifier.fillMaxSize(),
            onNavigateToMemorialShow = onNavigateToMemorialShow,
            onBack = onBack
        )

        SearchType.Note -> SearchNoteRoute(
            modifier = modifier.fillMaxSize(),
            onNavigateToNote = onNavigateToNote,
            onBack = onBack
        )

        SearchType.Schedule -> SearchScheduleRoute(
            modifier = modifier,
            onBack = onBack
        )

        SearchType.Transaction -> SearchTransactionRoute(
            modifier = modifier,
            onNavigateToTransaction = onNavigateToTransaction,
            onBack = onBack
        )
    }
}