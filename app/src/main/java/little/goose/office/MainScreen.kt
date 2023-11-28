package little.goose.office

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import little.goose.account.accountGraph
import little.goose.account.ui.analysis.navigateToAccountAnalysis
import little.goose.account.ui.navigateToTransactionExample
import little.goose.account.ui.transaction.navigateToTransaction
import little.goose.design.system.theme.ThemeConfig
import little.goose.design.system.theme.ThemeType
import little.goose.home.KEY_INIT_HOME_PAGE
import little.goose.home.ROUTE_HOME
import little.goose.home.homeRoute
import little.goose.memorial.memorialGraph
import little.goose.memorial.ui.MemorialScreenType
import little.goose.memorial.ui.navigateToMemorial
import little.goose.note.ui.note.NoteNavigatingType
import little.goose.note.ui.note.navigateToNote
import little.goose.note.ui.note.noteRoute
import little.goose.search.navigateToSearch
import little.goose.search.searchRoute
import little.goose.settings.navigateToSettings
import little.goose.settings.settingsRoute

@Stable
sealed interface AppState {

    data object Loading : AppState {
        override val themeConfig: ThemeConfig
            get() = ThemeConfig(
                isDynamicColor = true,
                themeType = ThemeType.FOLLOW_SYSTEM
            )
    }

    data class Success(
        override val themeConfig: ThemeConfig
    ) : AppState

    val themeConfig: ThemeConfig
}

@Composable
internal fun MainScreen(modifier: Modifier) {
    val navController = rememberNavController()
    LittleGooseAnimatedNavHost(
        modifier = modifier,
        navController = navController,
        startDestination = "$ROUTE_HOME/{$KEY_INIT_HOME_PAGE}"
    ) {
        homeRoute(
            onNavigateToSettings = navController::navigateToSettings,
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
            onNavigateToTransactionScreen = navController::navigateToTransaction,
            onNavigateToMemorialAdd = {
                navController.navigateToMemorial(MemorialScreenType.Add)
            },
            onNavigateToMemorial = {
                navController.navigateToMemorial(MemorialScreenType.Modify, memorialId = it)
            },
            onNavigateToAccountAnalysis = navController::navigateToAccountAnalysis
        )

        noteRoute(
            onBack = navController::navigateUp
        )

        searchRoute(
            onNavigateToNote = { noteId ->
                navController.navigateToNote(NoteNavigatingType.Edit(noteId))
            },
            onNavigateToMemorial = {
                navController.navigateToMemorial(MemorialScreenType.Modify, memorialId = it)
            },
            onNavigateToTransactionScreen = navController::navigateToTransaction,
            onBack = navController::navigateUp
        )

        accountGraph(
            onNavigateToTransactionExample = navController::navigateToTransactionExample,
            onNavigateToTransactionScreen = navController::navigateToTransaction,
            onBack = navController::navigateUp
        )

        memorialGraph(
            onBack = navController::navigateUp
        )

        settingsRoute(
            onBack = navController::navigateUp
        )
    }
}

private const val DEFAULT_ENTER_DURATION = 300
private const val DEFAULT_EXIT_DURATION = 220

@Composable
fun LittleGooseAnimatedNavHost(
    modifier: Modifier,
    navController: NavHostController,
    startDestination: String,
    builder: NavGraphBuilder.() -> Unit
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            activityEnterTransition()
        },
        exitTransition = {
            activityExitTransition()
        },
        popEnterTransition = {
            activityPopEnterTransition()
        },
        popExitTransition = {
            activityPopExitTransition()
        },
        builder = builder
    )
}

private fun AnimatedContentTransitionScope<NavBackStackEntry>.activityEnterTransition(): EnterTransition {
    return slideIntoContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Start,
        animationSpec = tween(DEFAULT_ENTER_DURATION, easing = LinearOutSlowInEasing),
        initialOffset = { it }
    )
}

@Suppress("UnusedReceiverParameter")
private fun AnimatedContentTransitionScope<NavBackStackEntry>.activityExitTransition(): ExitTransition {
    return scaleOut(
        animationSpec = tween(DEFAULT_ENTER_DURATION),
        targetScale = 0.96F
    )
}

@Suppress("UnusedReceiverParameter")
private fun AnimatedContentTransitionScope<NavBackStackEntry>.activityPopEnterTransition(): EnterTransition {
    return scaleIn(
        animationSpec = tween(DEFAULT_EXIT_DURATION),
        initialScale = 0.96F
    )
}

private fun AnimatedContentTransitionScope<NavBackStackEntry>.activityPopExitTransition(): ExitTransition {
    return slideOutOfContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.End,
        animationSpec = tween(DEFAULT_EXIT_DURATION, easing = FastOutLinearInEasing),
        targetOffset = { it }
    )
}