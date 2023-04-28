package little.goose.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DoneAll
import androidx.compose.material.icons.rounded.RemoveDone
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import little.goose.design.system.component.MovableActionButton
import little.goose.design.system.component.MovableActionButtonState
import little.goose.design.system.theme.AccountTheme
import little.goose.memorial.data.entities.Memorial
import little.goose.memorial.ui.MemorialDialog
import little.goose.memorial.ui.component.MemorialColumn
import little.goose.memorial.ui.component.MemorialColumnState
import little.goose.memorial.ui.rememberMemorialDialogState

@Composable
fun SearchMemorialScreen(
    modifier: Modifier = Modifier,
    memorialColumnState: MemorialColumnState,
    onDeleteMemorial: (Memorial) -> Unit
) {
    if (memorialColumnState.memorials.isNotEmpty()) {
        val memorialDialogState = rememberMemorialDialogState()
        MemorialDialog(
            state = memorialDialogState,
            onDelete = onDeleteMemorial
        )
        MemorialColumn(
            modifier = modifier.fillMaxSize(),
            state = memorialColumnState,
            onMemorialClick = {
                memorialDialogState.show(it)
            }
        )
    }

    if (memorialColumnState.isMultiSelecting) {
        val buttonState = remember { MovableActionButtonState() }
        LaunchedEffect(buttonState) { buttonState.expend() }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
        ) {
            MovableActionButton(
                modifier = Modifier.align(Alignment.BottomEnd),
                state = buttonState,
                mainButtonContent = {
                    Icon(imageVector = Icons.Rounded.Delete, contentDescription = "Delete")
                },
                onMainButtonClick = {
                    memorialColumnState.deleteMemorials(
                        memorialColumnState.multiSelectedMemorials.toList()
                    )
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
}

@Preview
@Composable
private fun PreviewSearchMemorialScreen() = AccountTheme {
    SearchMemorialScreen(
        memorialColumnState = MemorialColumnState(
            memorials = (0..5).map {
                Memorial(id = it.toLong(), content = "Memorial$it",)
            },
            isMultiSelecting = true,
            multiSelectedMemorials = emptySet(),
            onSelectMemorial = { _, _ -> },
            selectAllMemorial = {},
            cancelMultiSelecting = {},
            deleteMemorials = {}
        ),
        onDeleteMemorial = {}
    )
}