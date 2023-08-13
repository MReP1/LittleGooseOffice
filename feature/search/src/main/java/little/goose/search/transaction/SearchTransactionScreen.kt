package little.goose.search.transaction

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.hilt.navigation.compose.hiltViewModel
import little.goose.account.data.entities.Transaction
import little.goose.account.ui.component.TransactionColumnState
import little.goose.search.SearchState
import little.goose.search.component.SearchScaffold
import little.goose.ui.screen.LittleGooseEmptyScreen
import little.goose.ui.screen.LittleGooseLoadingScreen

sealed interface SearchTransactionState : SearchState {
    data class Empty(
        override val search: (String) -> Unit
    ) : SearchTransactionState

    data class Loading(
        override val search: (String) -> Unit
    ) : SearchTransactionState

    data class Success(
        val data: TransactionColumnState,
        override val search: (String) -> Unit
    ) : SearchTransactionState
}

sealed interface SearchTransactionEvent {
    data class DeleteTransactions(val transactions: List<Transaction>) : SearchTransactionEvent
}

@Composable
internal fun SearchTransactionRoute(
    modifier: Modifier = Modifier,
    onNavigateToTransactionScreen: (Long) -> Unit,
    onBack: () -> Unit
) {
    val viewModel = hiltViewModel<SearchTransactionViewModel>()
    val context = LocalContext.current
    val searchTransactionState by viewModel.searchTransactionState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel.searchTransactionEvent) {
        viewModel.searchTransactionEvent.collect { event ->
            when (event) {
                is SearchTransactionEvent.DeleteTransactions -> {
                    snackbarHostState.showSnackbar(
                        message = context.getString(little.goose.common.R.string.deleted)
                    )
                }
            }
        }
    }

    SearchTransactionScreen(
        modifier = modifier,
        state = searchTransactionState,
        snackbarHostState = snackbarHostState,
        onNavigateToTransactionScreen = onNavigateToTransactionScreen,
        onBack = onBack
    )
}

@Composable
fun SearchTransactionScreen(
    modifier: Modifier = Modifier,
    state: SearchTransactionState,
    snackbarHostState: SnackbarHostState,
    onNavigateToTransactionScreen: (Long) -> Unit,
    onBack: () -> Unit
) {
    var keyword by rememberSaveable { mutableStateOf("") }
    SearchScaffold(
        modifier = modifier,
        snackbarHostState = snackbarHostState,
        keyword = keyword,
        onKeywordChange = {
            keyword = it
            state.search(it)
        },
        onBack = onBack
    ) {
        AnimatedContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            transitionSpec = {
                val durationMillis = 320
                if (this.initialState is SearchTransactionState.Success &&
                    this.targetState is SearchTransactionState.Success
                ) {
                    fadeIn(
                        initialAlpha = 0.8F, animationSpec = tween(durationMillis = 100)
                    ) togetherWith fadeOut(
                        animationSpec = tween(durationMillis = 100)
                    )
                } else {
                    fadeIn(
                        animationSpec = tween(durationMillis)
                    ) + slideIntoContainer(
                        towards = if (targetState is SearchTransactionState.Success)
                            AnimatedContentTransitionScope.SlideDirection.Down
                        else AnimatedContentTransitionScope.SlideDirection.Up,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        ),
                        initialOffset = { offset -> offset / 2 }
                    ) togetherWith fadeOut(
                        animationSpec = tween(durationMillis)
                    ) + slideOutOfContainer(
                        towards = if (targetState is SearchTransactionState.Success)
                            AnimatedContentTransitionScope.SlideDirection.Down
                        else AnimatedContentTransitionScope.SlideDirection.Up,
                        animationSpec = tween(durationMillis),
                        targetOffset = { offset -> offset / 2 }
                    )
                }
            },
            targetState = state,
            label = "search transaction content"
        ) { state ->
            when (state) {
                is SearchTransactionState.Empty -> {
                    LittleGooseEmptyScreen(modifier = Modifier.fillMaxSize())
                }

                is SearchTransactionState.Loading -> {
                    LittleGooseLoadingScreen(modifier = Modifier.fillMaxSize())
                }

                is SearchTransactionState.Success -> {
                    SearchTransactionContent(
                        modifier = Modifier.fillMaxSize(),
                        transactionColumnState = state.data,
                        onNavigateToTransactionScreen = onNavigateToTransactionScreen
                    )
                }
            }
        }
    }
}