package little.goose.memorial.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import little.goose.common.constants.KEY_TYPE
import little.goose.common.constants.TYPE_ADD
import little.goose.common.constants.TYPE_MODIFY
import little.goose.memorial.data.constants.KEY_MEMORIAL_ID
import little.goose.memorial.data.entities.Memorial
import little.goose.memorial.logic.GetMemorialAtTopFlowUseCase
import little.goose.memorial.logic.GetMemorialFlowUseCase
import little.goose.memorial.logic.InsertMemorialUseCase
import little.goose.memorial.logic.UpdateMemorialUseCase
import little.goose.memorial.logic.UpdateMemorialsUseCase
import javax.inject.Inject

@HiltViewModel
class MemorialScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getMemorialFlowUseCase: GetMemorialFlowUseCase,
    private val getMemorialAtTopFlowUseCase: GetMemorialAtTopFlowUseCase,
    private val updateMemorialUseCase: UpdateMemorialUseCase,
    private val updateMemorialsUseCase: UpdateMemorialsUseCase,
    private val insertMemorialUseCase: InsertMemorialUseCase
) : ViewModel() {

    private val type: String by lazy(LazyThreadSafetyMode.NONE) { savedStateHandle[KEY_TYPE]!! }

    private val _memorialScreenState =
        MutableStateFlow<MemorialScreenState>(MemorialScreenState.Loading)
    val memorialScreenState = _memorialScreenState.asStateFlow()

    var isChangeTop = false

    init {
        viewModelScope.launch {
            val memorialId = savedStateHandle.get<Long>(KEY_MEMORIAL_ID).takeIf { id ->
                id != null && id > 0
            } ?: run {
                _memorialScreenState.value = MemorialScreenState.Success(
                    Memorial(content = "纪念日", isTop = false)
                )
                return@launch
            }
            getMemorialFlowUseCase(memorialId).onEach {
                _memorialScreenState.value = MemorialScreenState.Success(it)
            }.launchIn(viewModelScope)
        }
    }

    fun updateMemorial(memorial: Memorial) {
        _memorialScreenState.value = MemorialScreenState.Success(memorial)
    }

    fun storeMemorial() {
        val memorial =
            (memorialScreenState.value as? MemorialScreenState.Success)?.memorial ?: return
        viewModelScope.launch(Dispatchers.Main.immediate) {
            if (isChangeTop && memorial.isTop) {
                val topList = getMemorialAtTopFlowUseCase().first()
                    .map { it.copy(isTop = false) }
                updateMemorialsUseCase(topList)
                when (type) {
                    TYPE_MODIFY -> updateMemorialUseCase(memorial)
                    TYPE_ADD -> insertMemorialUseCase(memorial)
                }
            } else {
                when (type) {
                    TYPE_MODIFY -> updateMemorialUseCase(memorial)
                    TYPE_ADD -> insertMemorialUseCase(memorial)
                }
            }
        }
    }

}