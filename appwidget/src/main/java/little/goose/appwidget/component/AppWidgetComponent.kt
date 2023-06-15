package little.goose.appwidget.component

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalSize
import androidx.glance.action.Action
import androidx.glance.action.clickable
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import little.goose.appwidget.R

@Composable
internal fun AppWidgetIconColumn(
    modifier: GlanceModifier = GlanceModifier,
    @DrawableRes drawableResId: Int,
    contentDescription: String,
    primaryAction: Action,
    secondaryAction: Action
) {
    val size = LocalSize.current
    Column(
        modifier = modifier
            .cornerRadius(16.dp)
            .background(GlanceTheme.colors.primaryContainer)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (size.height >= 100.dp) {
            PrimaryAppWidgetIcon(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .defaultWeight()
                    .clickable(primaryAction),
                drawableResId = drawableResId,
                contentDescription = contentDescription
            )
            AppWidgetAddIcon(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .defaultWeight()
                    .clickable(secondaryAction)
            )
        } else {
            AppWidgetIcon(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .defaultWeight()
                    .clickable(primaryAction),
                drawableResId = drawableResId,
                contentDescription = contentDescription
            )
        }
    }
}

@Composable
fun AppWidgetIconRow(
    modifier: GlanceModifier = GlanceModifier,
    @DrawableRes drawableResId: Int,
    contentDescription: String,
    primaryAction: Action,
    secondaryAction: Action
) {
    val size = LocalSize.current
    Row(
        modifier = modifier
            .cornerRadius(16.dp)
            .background(GlanceTheme.colors.primaryContainer)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (size.width >= 100.dp) {
            PrimaryAppWidgetIcon(
                modifier = GlanceModifier
                    .fillMaxHeight()
                    .defaultWeight()
                    .clickable(primaryAction),
                drawableResId = drawableResId,
                contentDescription = contentDescription
            )
            AppWidgetAddIcon(
                modifier = GlanceModifier
                    .fillMaxHeight()
                    .defaultWeight()
                    .clickable(secondaryAction)
            )
        } else {
            AppWidgetIcon(
                modifier = GlanceModifier
                    .fillMaxHeight()
                    .defaultWeight()
                    .clickable(primaryAction),
                drawableResId = drawableResId,
                contentDescription = contentDescription
            )
        }
    }
}

@Composable
internal fun PrimaryAppWidgetIcon(
    modifier: GlanceModifier = GlanceModifier,
    @DrawableRes drawableResId: Int,
    contentDescription: String
) {
    Box(
        modifier = modifier.cornerRadius(16.dp).background(GlanceTheme.colors.primary),
        contentAlignment = Alignment.Center
    ) {
        Image(
            provider = ImageProvider(drawableResId),
            contentDescription = contentDescription,
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(colorProvider = GlanceTheme.colors.onPrimary),
            modifier = GlanceModifier.fillMaxSize().padding(5.dp)
        )
    }
}

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

@Composable
internal fun AppWidgetAddIcon(
    modifier: GlanceModifier = GlanceModifier
) {
    Box(
        modifier = modifier.cornerRadius(88.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            provider = ImageProvider(R.drawable.icon_add),
            contentDescription = "Add one",
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(colorProvider = GlanceTheme.colors.secondary),
            modifier = GlanceModifier.fillMaxSize().padding(5.dp)
        )
    }
}

