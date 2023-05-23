package little.goose.search.memorial

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import little.goose.memorial.data.entities.Memorial
import little.goose.memorial.logic.MemorialRepository
import little.goose.memorial.ui.component.MemorialColumnState
import javax.inject.Inject

@HiltViewModel
class SearchMemorialViewModel @Inject constructor(
    private val memorialRepository: MemorialRepository
) : ViewModel() {

    private val _searchMemorialState = MutableStateFlow<SearchMemorialState>(
        SearchMemorialState.Empty(::search)
    )
    val searchMemorialState = _searchMemorialState.asStateFlow()

    private val _searchMemorialEvent = MutableSharedFlow<SearchMemorialEvent>()
    val searchMemorialEvent = _searchMemorialEvent.asSharedFlow()

    private val multiSelectedMemorials = MutableStateFlow(emptySet<Memorial>())

    private var searchingJob: Job? = null

    private fun search(keyword: String) {
        if (keyword.isBlank()) {
            _searchMemorialState.value = SearchMemorialState.Empty(::search)
            return
        }
        _searchMemorialState.value = SearchMemorialState.Loading(::search)
        searchingJob?.cancel()
        searchingJob = combine(
            memorialRepository.searchMemorialByTextFlow(keyword),
            multiSelectedMemorials
        ) { memorials, multiSelectedMemorials ->
            _searchMemorialState.value = if (memorials.isEmpty()) {
                SearchMemorialState.Empty(::search)
            } else {
                SearchMemorialState.Success(
                    data = MemorialColumnState(
                        memorials = memorials,
                        multiSelectedMemorials = multiSelectedMemorials,
                        isMultiSelecting = multiSelectedMemorials.isNotEmpty(),
                        onSelectMemorial = ::selectMemorial,
                        selectAllMemorial = ::selectAllMemorial,
                        deleteMemorials = ::deleteMemorials,
                        cancelMultiSelecting = ::cancelMemorialsMultiSelecting
                    ),
                    search = ::search
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun selectAllMemorial() {
        multiSelectedMemorials.value = (searchMemorialState.value as? SearchMemorialState.Success)
            ?.data?.memorials?.toSet() ?: emptySet()
    }

    private fun cancelMemorialsMultiSelecting() {
        multiSelectedMemorials.value = emptySet()
    }

    private fun selectMemorial(memorial: Memorial, selected: Boolean) {
        multiSelectedMemorials.value = multiSelectedMemorials.value.toMutableSet().apply {
            if (selected) add(memorial) else remove(memorial)
        }
    }

    private fun deleteMemorials(memorials: List<Memorial>) {
        viewModelScope.launch {
            memorialRepository.deleteMemorials(memorials)
            _searchMemorialEvent.emit(SearchMemorialEvent.DeleteMemorials(memorials))
        }
    }

}