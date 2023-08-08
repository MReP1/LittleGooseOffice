package little.goose.search

import androidx.compose.runtime.Stable

@Stable
interface SearchState {
    val search: (String) -> Unit
}