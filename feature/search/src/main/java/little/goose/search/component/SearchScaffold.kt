package little.goose.search.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal fun SearchScaffold(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
    keyword: String,
    onKeywordChange: (String) -> Unit,
    onBack: () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { snackbarData ->
                Snackbar(snackbarData)
            }
        },
        topBar = {
            SearchTopAppBar(
                keyword = keyword,
                onKeywordChange = onKeywordChange,
                onBack = onBack
            )
        },
        content = content
    )
}