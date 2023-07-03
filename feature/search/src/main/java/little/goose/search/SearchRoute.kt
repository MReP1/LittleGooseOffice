package little.goose.search

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
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
    )
) {
    SearchRoute(
        modifier = Modifier
            .fillMaxSize()
            .shadow(36.dp, clip = false),
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