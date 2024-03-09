package little.goose.shared.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import little.goose.resource.GooseRes
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun LittleGooseLoadingScreen(
    modifier: Modifier = Modifier
) {
    LoadingScreen(
        modifier = modifier,
        content = {
            val normalGoose = painterResource(GooseRes.drawable.ic_little_goose)
            val backGoose = painterResource(GooseRes.drawable.ic_little_goose_back)
            val foreGoose = painterResource(GooseRes.drawable.ic_little_goose_fore)
            var painter by remember { mutableStateOf(normalGoose) }
            Image(painter = painter, contentDescription = "Loading")
            LaunchedEffect(Unit) {
                delay(80)
                while (isActive) {
                    painter = backGoose
                    delay(100)
                    painter = normalGoose
                    delay(80)
                    painter = foreGoose
                    delay(100)
                    painter = normalGoose
                    delay(80)
                }
            }
        }
    )
}

@Composable
internal fun LoadingScreen(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    content: @Composable () -> Unit = {}
) {
    Surface(
        modifier = modifier,
        color = backgroundColor
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}