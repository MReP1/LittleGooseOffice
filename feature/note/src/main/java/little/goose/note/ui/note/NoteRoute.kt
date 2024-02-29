package little.goose.note.ui.note

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import little.goose.common.constants.DEEP_LINK_THEME_AND_HOST
import little.goose.note.NoteScreenRoute
import little.goose.note.data.constants.KEY_NOTE
import little.goose.note.data.constants.KEY_NOTE_ID
import org.koin.androidx.compose.koinViewModel

sealed class NoteNavigatingType {
    data object Add : NoteNavigatingType()
    data class Edit(val noteId: Long) : NoteNavigatingType()
}

const val ROUTE_NOTE = "note"

private const val DEEP_LINK_URI_PATTERN_NOTE = "$DEEP_LINK_THEME_AND_HOST/$KEY_NOTE" +
        "/$KEY_NOTE_ID={$KEY_NOTE_ID}"

fun NavController.navigateToNote(
    type: NoteNavigatingType
) {
    val config: NavOptionsBuilder.() -> Unit = { launchSingleTop = true }
    when (type) {
        NoteNavigatingType.Add -> {
            navigate("$ROUTE_NOTE/-1", config)
        }

        is NoteNavigatingType.Edit -> {
            navigate("$ROUTE_NOTE/${type.noteId}", config)
        }
    }
}

fun NavGraphBuilder.noteRoute(onBack: () -> Unit) {
    composable(
        route = "$ROUTE_NOTE/{$KEY_NOTE_ID}",
        deepLinks = listOf(
            navDeepLink {
                uriPattern = DEEP_LINK_URI_PATTERN_NOTE
            }
        ),
        arguments = listOf(
            navArgument(KEY_NOTE_ID) {
                type = NavType.LongType
                defaultValue = -1L
            }
        )
    ) {
        val viewModel: NoteViewModel = koinViewModel()
        val screenState by viewModel.noteScreenStateHolder.noteScreenState.collectAsState()
        NoteScreenRoute(
            modifier = Modifier
                .fillMaxSize()
                .shadow(36.dp, clip = false),
            onBack = onBack,
            event = viewModel.noteScreenStateHolder.event,
            screenState = screenState,
            action = viewModel.noteScreenStateHolder.action
        )
    }
}