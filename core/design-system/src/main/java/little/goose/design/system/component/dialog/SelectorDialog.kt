package little.goose.design.system.component.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun SelectorDialog(
    state: DialogState,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    NormalDialog(
        state = state
    ) {
        Surface(
            modifier = Modifier,
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                content()
                Spacer(modifier = Modifier.height(16.dp))

                DialogButtonGroup(
                    startButtonContent = {
                        Text(text = stringResource(id = little.goose.common.R.string.cancel))
                    },
                    onStartButtonClick = onCancel,
                    endButtonContent = {
                        Text(text = stringResource(id = little.goose.common.R.string.confirm))
                    },
                    onEndButtonClick = onConfirm
                )
            }
        }
    }
}