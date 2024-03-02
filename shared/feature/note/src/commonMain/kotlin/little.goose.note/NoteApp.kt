package little.goose.note

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition

@Composable
fun NoteApp() {
    Navigator(NotebookHomeScreen) { navigator ->
        SlideTransition(
            navigator,
            modifier = Modifier.fillMaxSize(),
            animationSpec = tween(
                durationMillis = 280,
                easing = LinearOutSlowInEasing
            )
        )
    }
}