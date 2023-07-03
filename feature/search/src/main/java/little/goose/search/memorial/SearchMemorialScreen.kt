package little.goose.search.memorial

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest
import little.goose.design.system.theme.AccountTheme
import little.goose.memorial.data.entities.Memorial
import little.goose.memorial.ui.component.MemorialColumnState
import little.goose.search.SearchState
import little.goose.search.component.SearchTopAppBar
import little.goose.ui.screen.LittleGooseEmptyScreen
import little.goose.ui.screen.LittleGooseLoadingScreen

sealed interface SearchMemorialState : SearchState {
    data class Empty(
        override val search: (String) -> Unit
    ) : SearchMemorialState

    data class Loading(
        override val search: (String) -> Unit
    ) : SearchMemorialState

    data class Success(
        val data: MemorialColumnState,
        override val search: (String) -> Unit
    ) : SearchMemorialState
}

sealed interface SearchMemorialEvent {
    data class DeleteMemorials(val memorials: List<Memorial>) : SearchMemorialEvent
}

@Composable
fun SearchMemorialRoute(
    modifier: Modifier = Modifier,
    onNavigateToMemorialDialog: (Long) -> Unit,
    onBack: () -> Unit
) {
    val viewModel = hiltViewModel<SearchMemorialViewModel>()
    val context = LocalContext.current
    val searchMemorialState by viewModel.searchMemorialState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(viewModel.searchMemorialEvent) {
        viewModel.searchMemorialEvent.collectLatest { event ->
            when (event) {
                is SearchMemorialEvent.DeleteMemorials -> {
                    snackbarHostState.showSnackbar(
                        message = context.getString(little.goose.common.R.string.deleted)
                    )
                }
            }
        }
    }
    SearchMemorialScreen(
        modifier = modifier,
        state = searchMemorialState,
        snackbarHostState = snackbarHostState,
        onNavigateToMemorialDialog = onNavigateToMemorialDialog,
        onBack = onBack
    )
}

@Composable
fun SearchMemorialScreen(
    modifier: Modifier = Modifier,
    state: SearchMemorialState,
    snackbarHostState: SnackbarHostState,
    onNavigateToMemorialDialog: (Long) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { snackbarData ->
                Snackbar(snackbarData)
            }
        },
        topBar = {
            var keyword by rememberSaveable { mutableStateOf("") }
            SearchTopAppBar(
                keyword = keyword,
                onKeywordChange = {
                    keyword = it
                    state.search(it)
                },
                onBack = onBack
            )
        },
        content = { paddingValues ->
            val contentModifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
            when (state) {
                is SearchMemorialState.Empty -> {
                    LittleGooseEmptyScreen(modifier = contentModifier)
                }

                is SearchMemorialState.Loading -> {
                    LittleGooseLoadingScreen(modifier = contentModifier)
                }

                is SearchMemorialState.Success -> {
                    SearchMemorialContent(
                        modifier = contentModifier,
                        memorialColumnState = state.data,
                        onNavigateToMemorial = onNavigateToMemorialDialog
                    )
                }
            }
        }
    )

}

@Preview
@Composable
fun PreviewSearchMemorialScreen() = AccountTheme {
    SearchMemorialScreen(
        state = SearchMemorialState.Success(
            data = MemorialColumnState(
                memorials = listOf(
                    Memorial(id = 1, content = "纪念日"),
                    Memorial(id = 2, content = "纪念日")
                ),
            ),
            search = {}
        ),
        snackbarHostState = SnackbarHostState(),
        onNavigateToMemorialDialog = {},
        onBack = {}
    )
}