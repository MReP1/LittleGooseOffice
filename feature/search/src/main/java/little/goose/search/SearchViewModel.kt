package little.goose.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
): ViewModel() {

    val searchType by mutableStateOf(
        SearchType.fromValue(savedStateHandle.get<Int>(SearchType.KEY_SEARCH_TYPE)!!)
    )

}