package little.goose.design.system.util

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.toComposeRect
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.window.layout.WindowMetricsCalculator

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun calculateWindowSizeClassWithCurrentContext(): WindowSizeClass {
    LocalConfiguration.current
    val density = LocalDensity.current
    val context = LocalContext.current
    val metrics = WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(context)
    val size = with(density) { metrics.bounds.toComposeRect().size.toDpSize() }
    return remember(size) {
        WindowSizeClass.calculateFromSize(size)
    }
}