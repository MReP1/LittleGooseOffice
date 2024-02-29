package little.goose.shared.ui.architecture

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.SharedFlow

@Stable
data class MviHolder<S, E, I>(
    val state: S,
    val event: SharedFlow<E>,
    val action: (I) -> Unit
)
