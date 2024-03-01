package little.goose.shared.ui.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp

@Composable
fun DialogButtonGroup(
    modifier: Modifier = Modifier,
    startButtonContent: @Composable () -> Unit,
    onStartButtonClick: () -> Unit,
    endButtonContent: @Composable () -> Unit,
    onEndButtonClick: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        TextButton(
            onClick = onStartButtonClick,
            modifier = Modifier.wrapContentSize(),
            shape = RectangleShape,
            colors = ButtonDefaults.textButtonColors(
                contentColor = MaterialTheme.colorScheme.secondary.copy(
                    alpha = 0.56F
                )
            )
        ) {
            startButtonContent()
        }
        TextButton(
            onClick = onEndButtonClick,
            modifier = Modifier.wrapContentSize(),
            shape = RectangleShape
        ) {
            endButtonContent()
        }
        Spacer(modifier = Modifier.width(12.dp))
    }
}