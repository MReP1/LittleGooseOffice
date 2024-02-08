package little.goose.note.ui.note

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import kotlinx.coroutines.android.awaitFrame
import little.goose.common.constants.DEEP_LINK_THEME_AND_HOST
import little.goose.note.data.constants.KEY_NOTE
import little.goose.note.data.constants.KEY_NOTE_ID
import little.goose.note.event.NoteScreenEvent
import little.goose.note.ui.NoteContentState
import little.goose.note.ui.NoteScreen

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
        NoteRoute(
            modifier = Modifier
                .fillMaxSize()
                .shadow(36.dp, clip = false),
            onBack = onBack
        )
    }
}

@Composable
internal fun NoteRoute(
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    val viewModel = hiltViewModel<NoteViewModel>()

    val contentState by viewModel.noteContentState.collectAsState()
    val bottomBarState by viewModel.noteBottomBarState.collectAsState()

    val blockColumnState = rememberLazyListState()

    NoteScreen(
        onBack = onBack,
        modifier = modifier,
        bottomBarState = bottomBarState,
        noteContentState = contentState,
        blockColumnState = blockColumnState
    )

    LaunchedEffect(viewModel.event) {
        viewModel.event.collect { event ->
            when (event) {
                is NoteScreenEvent.AddNoteBlock -> {
                    val editState = contentState as? NoteContentState.Edit ?: return@collect
                    val blockIndex = editState.contentStateList.indexOfLast {
                        it.id == event.id
                    }
                    awaitFrame()
                    if (blockIndex != -1) {
                        // 标题占了一个位置，所以要 +1
                        blockColumnState.animateScrollToItem(blockIndex + 1, 0)
                    }
                    // 申请焦点
                    runCatching {
                        editState.contentStateList
                            .getOrNull(blockIndex)
                            ?.focusRequester
                            ?.requestFocus()
                    }
                }
            }
        }
    }
}