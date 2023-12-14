package little.goose.home.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DoneAll
import androidx.compose.material.icons.rounded.RemoveDone
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import little.goose.design.system.component.MovableActionButton
import little.goose.design.system.component.MovableActionButtonState
import little.goose.design.system.component.dialog.DeleteDialog
import little.goose.design.system.component.dialog.DeleteDialogState
import little.goose.home.data.HomePage


@Composable
internal fun HomeMovableActionButton(
    currentHomePage: HomePage,
    buttonState: MovableActionButtonState,
    isMultiSelecting: Boolean,
    modifier: Modifier = Modifier,
    onDeleteNotes: () -> Unit,
    onDeleteTransactions: () -> Unit,
    onDeleteMemorials: () -> Unit,
    onNavigateToNewNote: () -> Unit,
    onNavigateToNewTransaction: () -> Unit,
    onNavigateToNewMemorial: () -> Unit,
    onSelectAllNotes: () -> Unit,
    onSelectAllTransactions: () -> Unit,
    onSelectAllMemorials: () -> Unit,
    onCancelNotesMultiSelecting: () -> Unit,
    onCancelTransactionsMultiSelecting: () -> Unit,
    onCancelMemorialsMultiSelecting: () -> Unit
) {
    val deleteDialogState = remember { DeleteDialogState() }

    MovableActionButton(
        modifier = modifier,
        state = buttonState,
        needToExpand = isMultiSelecting,
        mainButtonContent = {
            Icon(
                imageVector = if (isMultiSelecting) {
                    Icons.Rounded.Delete
                } else {
                    Icons.Rounded.Add
                },
                contentDescription = "More"
            )
        },
        onMainButtonClick = {
            when {
                currentHomePage == HomePage.Notebook && isMultiSelecting -> {
                    deleteDialogState.show(onConfirm = onDeleteNotes)
                }

                currentHomePage == HomePage.Notebook && !isMultiSelecting -> {
                    onNavigateToNewNote()
                }

                currentHomePage == HomePage.Account && isMultiSelecting -> {
                    deleteDialogState.show(onConfirm = onDeleteTransactions)
                }

                currentHomePage == HomePage.Account && !isMultiSelecting -> {
                    onNavigateToNewTransaction()
                }

                currentHomePage == HomePage.Memorial && isMultiSelecting -> {
                    deleteDialogState.show(onConfirm = onDeleteMemorials)
                }

                currentHomePage == HomePage.Memorial && !isMultiSelecting -> {
                    onNavigateToNewMemorial()
                }

                else -> {}
            }
        },
        topSubButtonContent = {
            Icon(
                imageVector = Icons.Rounded.DoneAll,
                contentDescription = "Select all"
            )
        },
        onTopSubButtonClick = {
            when (currentHomePage) {
                HomePage.Notebook -> onSelectAllNotes()
                HomePage.Account -> onSelectAllTransactions()
                HomePage.Memorial -> onSelectAllMemorials()
                else -> {}
            }
        },
        bottomSubButtonContent = {
            Icon(
                imageVector = Icons.Rounded.RemoveDone,
                contentDescription = "Remove done"
            )
        },
        onBottomSubButtonClick = {
            when (currentHomePage) {
                HomePage.Account -> onCancelTransactionsMultiSelecting()
                HomePage.Memorial -> onCancelMemorialsMultiSelecting()
                HomePage.Notebook -> onCancelNotesMultiSelecting()
                else -> {}
            }
        }
    )

    DeleteDialog(state = deleteDialogState)
}