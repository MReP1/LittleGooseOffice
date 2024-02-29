package little.goose.shared.ui.architecture

import androidx.compose.runtime.saveable.Saver
import kotlinx.coroutines.flow.MutableStateFlow

@Suppress("UNCHECKED_CAST")
fun <S : Any> mutableStateFlowAutoSaver() = mutableStateFlowSaver as Saver<MutableStateFlow<S>, Any>

val mutableStateFlowSaver = Saver<MutableStateFlow<Any>, Any>(
    save = { it.value },
    restore = { MutableStateFlow(it) }
)