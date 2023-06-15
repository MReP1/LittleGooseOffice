package little.goose.appwidget.component

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.appwidget.cornerRadius
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.ContentScale
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding

@Composable
internal fun AppWidgetIcon(
    modifier: GlanceModifier = GlanceModifier,
    @DrawableRes drawableResId: Int,
    contentDescription: String
) {
    Box(modifier = modifier.cornerRadius(88.dp), contentAlignment = Alignment.Center) {
        Image(
            provider = ImageProvider(drawableResId),
            contentDescription = contentDescription,
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(colorProvider = GlanceTheme.colors.onPrimaryContainer),
            modifier = GlanceModifier.fillMaxSize().padding(5.dp)
        )
    }
}

