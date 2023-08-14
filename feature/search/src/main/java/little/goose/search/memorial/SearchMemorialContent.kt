package little.goose.search.memorial

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DoneAll
import androidx.compose.material.icons.rounded.RemoveDone
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import little.goose.design.system.component.MovableActionButton
import little.goose.design.system.component.MovableActionButtonState
import little.goose.design.system.component.dialog.DeleteDialog
import little.goose.design.system.component.dialog.DeleteDialogState
import little.goose.design.system.theme.AccountTheme
import little.goose.memorial.data.entities.Memorial
import little.goose.memorial.ui.component.MemorialColumn
import little.goose.memorial.ui.component.MemorialColumnState

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun SearchMemorialContent(
    modifier: Modifier = Modifier,
    memorialColumnState: MemorialColumnState,
    onNavigateToMemorial: (Long) -> Unit
) {
    if (memorialColumnState.memorials.isNotEmpty()) {
        MemorialColumn(
            modifier = modifier.fillMaxSize(),
            state = memorialColumnState,
            onMemorialEdit = { memorial ->
                memorial.id?.run(onNavigateToMemorial)
            }
        )
    }

    val deleteDialogState = remember { DeleteDialogState() }

    if (memorialColumnState.isMultiSelecting) {
        val buttonState = remember { MovableActionButtonState() }
        LaunchedEffect(buttonState) { buttonState.expend() }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(
                    if (WindowInsets.isImeVisible) {
                        WindowInsets.ime.union(BottomAppBarDefaults.windowInsets)
                    } else {
                        BottomAppBarDefaults.windowInsets
                    }
                )
        ) {
            MovableActionButton(
                modifier = Modifier.align(Alignment.BottomEnd),
                state = buttonState,
                mainButtonContent = {
                    Icon(imageVector = Icons.Rounded.Delete, contentDescription = "Delete")
                },
                onMainButtonClick = {
                    deleteDialogState.show(onConfirm = {
                        memorialColumnState.deleteMemorials(
                            memorialColumnState.multiSelectedMemorials.toList()
                        )
                    })
                },
                topSubButtonContent = {
                    Icon(imageVector = Icons.Rounded.DoneAll, contentDescription = "Select All")
                },
                onTopSubButtonClick = {
                    memorialColumnState.selectAllMemorial()
                },
                bottomSubButtonContent = {
                    Icon(
                        imageVector = Icons.Rounded.RemoveDone,
                        contentDescription = "Unselect All"
                    )
                },
                onBottomSubButtonClick = {
                    memorialColumnState.cancelMultiSelecting()
                }
            )
        }
    }

    DeleteDialog(state = deleteDialogState)
}

@Preview
@Composable
private fun PreviewSearchMemorialContent() = AccountTheme {
    SearchMemorialContent(
        memorialColumnState = MemorialColumnState(
            memorials = (0..5).map {
                Memorial(id = it.toLong(), content = "Memorial$it")
            },
            isMultiSelecting = true,
            multiSelectedMemorials = emptySet(),
            onSelectMemorial = { _, _ -> },
            selectAllMemorial = {},
            cancelMultiSelecting = {},
            deleteMemorials = {}
        ),
        onNavigateToMemorial = {}
    )
}