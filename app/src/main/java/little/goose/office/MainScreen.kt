package little.goose.office

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import little.goose.account.accountGraph
import little.goose.account.ui.analysis.navigateToAccountAnalysis
import little.goose.account.ui.navigateToTransactionExample
import little.goose.account.ui.transaction.navigateToTransaction
import little.goose.home.ROUTE_HOME
import little.goose.home.homeRoute
import little.goose.note.ui.note.NoteNavigatingType
import little.goose.note.ui.note.navigateToNote
import little.goose.note.ui.note.noteRoute
import little.goose.search.navigateToSearch
import little.goose.search.searchRoute

@Composable
internal fun MainScreen(
    modifier: Modifier
) {
    val navController = rememberAnimatedNavController()
    LittleGooseAnimatedNavHost(
        modifier = modifier,
        navController = navController,
        startDestination = ROUTE_HOME
    ) {
        homeRoute(
            onNavigateToNote = { noteId ->
                val navigatingType = if (noteId != null) {
                    NoteNavigatingType.Edit(noteId)
                } else {
                    NoteNavigatingType.Add
                }
                navController.navigateToNote(navigatingType)
            },
            onNavigateToSearch = navController::navigateToSearch,
            onNavigateToTransaction = { id, time ->
                if (id != null) {
                    navController.navigateToTransaction(id)
                } else if (time != null) {
                    navController.navigateToTransaction(time)
                }
            },
            onNavigateToAccountAnalysis = navController::navigateToAccountAnalysis
        )
        noteRoute(onBack = navController::popBackStack)
        searchRoute(
            onNavigateToNote = { noteId ->
                navController.navigateToNote(NoteNavigatingType.Edit(noteId))
            },
            onNavigateToTransaction = navController::navigateToTransaction,
            onBack = navController::popBackStack
        )
        accountGraph(
            onNavigateToTransactionExample = navController::navigateToTransactionExample,
            onNavigateToTransaction = { id, time ->
                if (id != null) {
                    navController.navigateToTransaction(id)
                } else if (time != null) {
                    navController.navigateToTransaction(time)
                }
            },
            onBack = navController::popBackStack
        )
    }
}

private const val defaultDurationMillis = 400

@Composable
fun LittleGooseAnimatedNavHost(
    modifier: Modifier,
    navController: NavHostController,
    startDestination: String,
    builder: NavGraphBuilder.() -> Unit
) {
    AnimatedNavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentScope.SlideDirection.Start,
                animationSpec = tween(defaultDurationMillis),
                initialOffset = { it }
            )
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentScope.SlideDirection.Start,
                animationSpec = tween(defaultDurationMillis),
                targetOffset = { it }
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentScope.SlideDirection.End,
                animationSpec = tween(defaultDurationMillis),
                initialOffset = { it }
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentScope.SlideDirection.End,
                animationSpec = tween(defaultDurationMillis),
                targetOffset = { it }
            )
        },
        builder = builder
    )
}