package little.goose.appwidget

import androidx.compose.runtime.Composable
import androidx.glance.GlanceTheme

@Composable
fun GooseGlanceTheme(content: @Composable () -> Unit) {
    // TODO 完善品牌色
    GlanceTheme(content = content)
}