package little.goose.shared.common

import kotlinx.coroutines.flow.SharedFlow

data class MviHolder<State, Event, Intent>(
    val state: State,
    val event: SharedFlow<Event>,
    val intent: (Intent) -> Unit
)
