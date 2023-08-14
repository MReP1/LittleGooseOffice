package little.goose.search.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import little.goose.design.system.theme.AccountTheme

@OptIn(ExperimentalComposeUiApi::class, ExperimentalLayoutApi::class)
@Composable
internal fun SearchScreen(
    modifier: Modifier = Modifier,
    keyword: String,
    onKeywordChange: (String) -> Unit,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    content: @Composable () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    SearchBar(
        modifier = modifier.focusRequester(focusRequester),
        query = keyword,
        onQueryChange = onKeywordChange,
        onSearch = onKeywordChange,
        active = true,
        onActiveChange = {
            if (!it) onBack()
        },
        leadingIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        trailingIcon = {
            if (keyword.isNotEmpty()) {
                IconButton(onClick = { onKeywordChange("") }) {
                    Icon(
                        imageVector = Icons.Rounded.Clear,
                        contentDescription = "Clear"
                    )
                }
            }
        },
        content = {
            Box(modifier = Modifier.fillMaxSize()) {
                content()
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .windowInsetsPadding(
                            if (WindowInsets.isImeVisible) {
                                WindowInsets.ime.union(BottomAppBarDefaults.windowInsets)
                            } else {
                                BottomAppBarDefaults.windowInsets
                            }
                        )
                ) {
                    Snackbar(snackbarData = it)
                }
            }
        }
    )
    val keyboardController = LocalSoftwareKeyboardController.current
    DisposableEffect(keyboardController) {
        focusRequester.requestFocus()
        keyboardController?.show()
        onDispose { }
    }
}

@Preview
@Composable
fun PreviewSearchScreen() = AccountTheme {
    SearchScreen(
        keyword = "Search keyword",
        snackbarHostState = SnackbarHostState(),
        onKeywordChange = {},
        onBack = {},
        content = {}
    )
}