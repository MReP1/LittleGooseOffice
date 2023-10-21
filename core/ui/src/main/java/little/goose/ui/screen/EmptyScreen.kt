package little.goose.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.launch
import little.goose.design.system.theme.GooseStyle
import little.goose.ui.R

@Stable
private data class Goose7(
    var a: Int = 0
)

@Composable
fun LittleGooseEmptyScreen(
    modifier: Modifier,
    content: @Composable () -> Unit = {}
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        val goose7 = remember { Goose7() }
        Image(
            painter = painterResource(id = R.drawable.ic_little_goose),
            contentDescription = "Logo",
            modifier = Modifier.clickable(
                remember { MutableInteractionSource() }, null, true, null, null
            ) {
                if (goose7.a++ > 17) {
                    scope.launch { GooseStyle.killGoose(context) }
                }
            }
        )
        content()
    }
}