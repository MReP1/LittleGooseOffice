package little.goose.memorial.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.dialog
import androidx.navigation.navArgument
import little.goose.design.system.component.dialog.DeleteDialog
import little.goose.design.system.component.dialog.DeleteDialogState
import little.goose.memorial.data.constants.KEY_MEMORIAL_ID
import little.goose.memorial.data.entities.Memorial
import little.goose.memorial.ui.component.MemorialCard

const val ROUTE_DIALOG_MEMORIAL = "dialog_memorial"

fun NavController.navigateToMemorialDialog(memorialId: Long) {
    navigate("$ROUTE_DIALOG_MEMORIAL/$memorialId")
}

fun NavGraphBuilder.memorialDialogRoute(
    onDismissRequest: () -> Unit,
    onNavigateToMemorialShow: (Long) -> Unit
) {
    dialog(
        route = "$ROUTE_DIALOG_MEMORIAL/{$KEY_MEMORIAL_ID}",
        arguments = listOf(
            navArgument(KEY_MEMORIAL_ID) {
                type = NavType.LongType
            }
        )
    ) {
        val deleteMemorialDialogState = remember { DeleteDialogState() }
        val viewModel = hiltViewModel<MemorialDialogViewModel>()
        val memorial by viewModel.memorial.collectAsStateWithLifecycle()
        MemorialDialogScreen(
            modifier = Modifier.wrapContentSize(),
            memorial = memorial,
            onDelete = {
                deleteMemorialDialogState.show(onConfirm = {
                    viewModel.deleteMemorial()
                    onDismissRequest()
                })
            },
            onEdit = {
                onDismissRequest()
                memorial.id?.let { onNavigateToMemorialShow(it) }
            }
        )
        DeleteDialog(state = deleteMemorialDialogState)
    }
}

@Composable
fun MemorialDialogScreen(
    modifier: Modifier,
    memorial: Memorial,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    Column(modifier.clip(RoundedCornerShape(24.dp))) {
        MemorialCard(
            memorial = memorial,
            shape = RectangleShape
        )
        Row(modifier.fillMaxWidth()) {
            Button(
                modifier = Modifier
                    .weight(1F)
                    .height(56.dp),
                onClick = onDelete,
                shape = RectangleShape
            ) {
                Text(text = stringResource(id = little.goose.common.R.string.delete))
            }
            Button(
                modifier = Modifier
                    .weight(1F)
                    .height(56.dp),
                onClick = onEdit,
                shape = RectangleShape
            ) {
                Text(text = stringResource(id = little.goose.common.R.string.edit))
            }
        }
    }
}