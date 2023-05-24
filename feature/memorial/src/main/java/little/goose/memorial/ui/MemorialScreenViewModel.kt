package little.goose.memorial.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import little.goose.common.constants.KEY_TYPE
import little.goose.common.constants.TYPE_ADD
import little.goose.common.constants.TYPE_MODIFY
import little.goose.memorial.data.constants.KEY_MEMORIAL
import little.goose.memorial.data.entities.Memorial
import little.goose.memorial.logic.GetMemorialAtTopFlowUseCase
import little.goose.memorial.logic.InsertMemorialUseCase
import little.goose.memorial.logic.UpdateMemorialUseCase
import little.goose.memorial.logic.UpdateMemorialsUseCase
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class MemorialScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getMemorialAtTopFlowUseCase: GetMemorialAtTopFlowUseCase,
    private val updateMemorialUseCase: UpdateMemorialUseCase,
    private val updateMemorialsUseCase: UpdateMemorialsUseCase,
    private val insertMemorialUseCase: InsertMemorialUseCase
) : ViewModel() {

    private val type: String by lazy(LazyThreadSafetyMode.NONE) { savedStateHandle[KEY_TYPE]!! }

    private val _memorial = MutableStateFlow(
        value = savedStateHandle[KEY_MEMORIAL] ?: Memorial(null, "纪念日", false, Date())
    )
    val memorial = _memorial.asStateFlow()

    var isChangeTop = false

    fun updateMemorial(memorial: Memorial) {
        _memorial.value = memorial
    }

    fun storeMemorial() {
        if (isChangeTop && memorial.value.isTop) { //在主线程判断，以免线程不同步
            // FIXME scope
            viewModelScope.launch {
                val topList = getMemorialAtTopFlowUseCase().first()
                    .map { it.copy(isTop = false) }
                updateMemorialsUseCase(topList)
                when (type) {
                    TYPE_MODIFY -> updateMemorialUseCase(memorial.value)
                    TYPE_ADD -> insertMemorialUseCase(memorial.value)
                }
            }
        } else {
            viewModelScope.launch {
                when (type) {
                    TYPE_MODIFY -> updateMemorialUseCase(memorial.value)
                    TYPE_ADD -> insertMemorialUseCase(memorial.value)
                }
            }
        }
    }

}