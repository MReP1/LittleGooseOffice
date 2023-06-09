package little.goose.home.navigation

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
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable
import little.goose.common.utils.getDataOrDefault
import little.goose.home.data.HOME
import little.goose.home.ui.HomeScreen
import little.goose.home.utils.KEY_PREF_PAGER
import little.goose.home.utils.homeDataStore
import little.goose.note.ui.note.ROUTE_NOTE

var isHomePageInit = true

var homePage by mutableStateOf(-1)

const val ROUTE_HOME = "route_home"

fun NavGraphBuilder.homeRoute(navController: NavController) {
    composable(ROUTE_HOME) {
        HomeRoute(
            modifier = Modifier.fillMaxSize(),
            onNavigateToNote = {
                navController.navigate(if (it != null) "$ROUTE_NOTE/$it" else "$ROUTE_NOTE/-1")
            }
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
    onNavigateToNote: (noteId: Long?) -> Unit
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
                onNavigateToNote = onNavigateToNote
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