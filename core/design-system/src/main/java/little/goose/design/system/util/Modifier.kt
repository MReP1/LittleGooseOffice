package little.goose.design.system.util

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsCompat

@Composable
fun Modifier.paddingCutout(enabled: Boolean): Modifier {
    return if (enabled) {
        val density = LocalDensity.current
        val layoutDirection = LocalLayoutDirection.current
        val view = LocalView.current
        val displayCutout =
            WindowInsetsCompat.toWindowInsetsCompat(view.rootWindowInsets).displayCutout
        val paddingStart = with(density) {
            displayCutout?.run {
                if (layoutDirection == LayoutDirection.Ltr) safeInsetLeft else safeInsetRight
            }?.toDp() ?: 0.dp
        }
        val paddingEnd = with(density) {
            displayCutout?.run {
                if (layoutDirection == LayoutDirection.Ltr) safeInsetRight else safeInsetLeft
            }?.toDp() ?: 0.dp
        }
        this.padding(start = paddingStart, end = paddingEnd)
    } else this
}