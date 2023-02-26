package little.goose.design.system.component.dialog

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp

@Composable
fun SelectorDialog(
    state: DialogState,
    onCancel: (() -> Unit)? = null,
    onConfirm: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    NormalDialog(
        state = state
    ) {
        Surface(modifier = Modifier, shape = RoundedCornerShape(24.dp)) {
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                content()
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Button(
                        onClick = {
                            onCancel?.invoke()
                            state.dismiss()
                        },
                        modifier = Modifier
                            .weight(1F)
                            .fillMaxHeight(),
                        shape = RectangleShape,
                    ) {
                        Text(text = "取消")
                    }
                    Button(
                        onClick = {
                            onConfirm?.invoke()
                            state.dismiss()
                        },
                        modifier = Modifier
                            .weight(1F)
                            .fillMaxHeight(),
                        shape = RectangleShape
                    ) {
                        Text(text = "确认")
                    }
                }
            }
        }
    }
}