package little.goose.design.system.component.dialog

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
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
        modifier = modifier
            .height(48.dp)
            .fillMaxWidth()
    ) {
        Button(
            onClick = onStartButtonClick,
            modifier = Modifier
                .weight(1F)
                .fillMaxHeight(),
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            startButtonContent()
        }
        Button(
            onClick = onEndButtonClick,
            modifier = Modifier
                .weight(1F)
                .fillMaxHeight(),
            shape = RectangleShape
        ) {
            endButtonContent()
        }
    }
}