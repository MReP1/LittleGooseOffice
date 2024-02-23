package little.goose.note

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.flow.MutableSharedFlow
import little.goose.data.note.domain.GetNoteWithContentFlowUseCase
import little.goose.note.ui.home.NoteHomeScreen
import little.goose.note.ui.home.NoteItemState
import little.goose.shared.common.MviHolder
import org.koin.compose.getKoin

internal sealed class NoteHomeState {
    data object Loading : NoteHomeState()
    data class Success(val itemStates: List<NoteItemState>) : NoteHomeState()
}

internal sealed class NoteHomeEvent {

}

internal sealed class NoteHomeIntent {

}

object NoteHomeScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val (state, event, action) = NoteHomeMviHolder()

        NoteHomeScreen(
            state = state,
            onNoteItemClick = { noteId ->
                navigator.push(NoteScreen(noteId))
            }
        )

        LaunchedEffect(event) {
            event.collect {

            }
        }
    }

}

@Composable
internal fun NoteHomeMviHolder(): MviHolder<NoteHomeState, NoteHomeEvent, NoteHomeIntent> {
    val koin = getKoin()
    val getNoteWithContentFlow = remember(koin) { koin.get<GetNoteWithContentFlowUseCase>() }
    val noteHomeState by produceState<NoteHomeState>(NoteHomeState.Loading) {
        getNoteWithContentFlow().collect { nwcList ->
            val states = nwcList.map { nwc ->
                NoteItemState(
                    id = nwc.note.id!!,
                    title = nwc.note.title.ifEmpty { "Untitled" },
                    content = nwc.content.firstOrNull()?.content ?: "Empty!"
                )
            }
            value = NoteHomeState.Success(states)
        }
    }
    val noteHomeEvent = remember { MutableSharedFlow<NoteHomeEvent>() }

    val action: (NoteHomeIntent) -> Unit = remember {
        {

        }
    }

    return remember(noteHomeState) {
        MviHolder(noteHomeState, noteHomeEvent, action)
    }
}