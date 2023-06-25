package little.goose.search.transaction

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
import androidx.hilt.navigation.compose.hiltViewModel
import little.goose.account.data.entities.Transaction
import little.goose.account.ui.component.TransactionColumnState
import little.goose.search.SearchState
import little.goose.search.component.SearchTopAppBar
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
fun SearchTransactionRoute(
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
        searchTransactionState = searchTransactionState,
        snackbarHostState = snackbarHostState,
        onNavigateToTransactionScreen = onNavigateToTransactionScreen,
        onBack = onBack
    )
}

@Composable
fun SearchTransactionScreen(
    modifier: Modifier = Modifier,
    searchTransactionState: SearchTransactionState,
    snackbarHostState: SnackbarHostState,
    onNavigateToTransactionScreen: (Long) -> Unit,
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
                    searchTransactionState.search(it)
                },
                onBack = onBack
            )
        },
        content = { paddingValues ->
            val contentModifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
            when (searchTransactionState) {
                is SearchTransactionState.Empty -> {
                    LittleGooseEmptyScreen(modifier = contentModifier)
                }

                is SearchTransactionState.Loading -> {
                    LittleGooseLoadingScreen(modifier = contentModifier)
                }

                is SearchTransactionState.Success -> {
                    SearchTransactionContent(
                        modifier = contentModifier,
                        transactionColumnState = searchTransactionState.data,
                        onNavigateToTransactionScreen = onNavigateToTransactionScreen
                    )
                }
            }
        }
    )
}