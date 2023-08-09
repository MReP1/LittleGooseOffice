package little.goose.appwidget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import little.goose.appwidget.component.AppWidgetIcon
import little.goose.appwidget.component.AppWidgetIconColumn
import little.goose.appwidget.component.AppWidgetIconRow
import little.goose.appwidget.layout.AppWidgetSizeResponsive

class GooseAppWidget : GlanceAppWidget() {

    override val sizeMode: SizeMode = SizeMode.Responsive(
        AppWidgetSizeResponsive.entries.map { it.size }.toSet()
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GooseGlanceTheme {
                CompositionLocalProvider(LocalContext provides context) {
                    GooseAppWidgetScreen(
                        modifier = GlanceModifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
private fun GooseAppWidgetScreen(
    modifier: GlanceModifier = GlanceModifier
) {
    val size = LocalSize.current
    when {
        size.width > size.height -> {
            HorizontalRectangleAppWidget(
                modifier = modifier,
            )
        }

        size.width < size.height -> {
            VerticalRectangleAppWidget(
                modifier = modifier
            )
        }

        else -> {
            SquareAppWidget(
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun VerticalRectangleAppWidget(
    modifier: GlanceModifier
) {
    Column(
        modifier = modifier
            .background(GlanceTheme.colors.surface)
            .cornerRadius(16.dp)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val size = LocalSize.current
        val navCollection = GooseNav.entries
        navCollection.forEachIndexed { index, nav ->
            if (size == AppWidgetSizeResponsive.RECTANGLE_50_100.size) {
                AppWidgetIcon(
                    drawableResId = nav.drawableResId,
                    contentDescription = nav.contentDescription,
                    modifier = GlanceModifier
                        .defaultWeight()
                        .fillMaxWidth()
                        .clickable(nav.homePageAction)
                )
            } else {
                AppWidgetIconRow(
                    drawableResId = nav.drawableResId,
                    contentDescription = nav.contentDescription,
                    modifier = GlanceModifier
                        .defaultWeight()
                        .fillMaxWidth(),
                    primaryAction = nav.homePageAction,
                    secondaryAction = nav.addAction
                )
            }
            if (index < navCollection.lastIndex) {
                Spacer(modifier = GlanceModifier.height(6.dp))
            }
        }
    }
}

@Composable
private fun HorizontalRectangleAppWidget(
    modifier: GlanceModifier = GlanceModifier
) {
    Row(
        modifier = modifier
            .background(GlanceTheme.colors.surface)
            .cornerRadius(16.dp)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val size = LocalSize.current
        val navCollection = GooseNav.entries
        navCollection.forEachIndexed { index, gooseNav ->
            if (size == AppWidgetSizeResponsive.RECTANGLE_100_50.size) {
                AppWidgetIcon(
                    drawableResId = gooseNav.drawableResId,
                    contentDescription = gooseNav.contentDescription,
                    modifier = GlanceModifier
                        .defaultWeight()
                        .fillMaxHeight()
                        .clickable(gooseNav.homePageAction)
                )
            } else {
                AppWidgetIconColumn(
                    drawableResId = gooseNav.drawableResId,
                    contentDescription = gooseNav.contentDescription,
                    modifier = GlanceModifier
                        .defaultWeight()
                        .fillMaxHeight(),
                    primaryAction = gooseNav.homePageAction,
                    secondaryAction = gooseNav.addAction
                )
            }
            if (index < navCollection.lastIndex) {
                Spacer(modifier = GlanceModifier.width(6.dp))
            }
        }
    }
}

@Composable
private fun SquareAppWidget(
    modifier: GlanceModifier = GlanceModifier
) {
    Column(
        modifier = modifier
            .cornerRadius(16.dp)
            .background(GlanceTheme.colors.surface)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = GlanceModifier.fillMaxWidth().defaultWeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            GooseNav.SCHEDULE.IconWidget(
                modifier = GlanceModifier
                    .defaultWeight()
                    .fillMaxHeight()
            )
            GooseNav.NOTE.IconWidget(
                modifier = GlanceModifier
                    .defaultWeight()
                    .fillMaxHeight()
            )
        }
        Row(
            modifier = GlanceModifier.fillMaxWidth().defaultWeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            GooseNav.ACCOUNT.IconWidget(
                modifier = GlanceModifier
                    .defaultWeight()
                    .fillMaxHeight()
            )
            GooseNav.MEMORIAL.IconWidget(
                modifier = GlanceModifier
                    .defaultWeight()
                    .fillMaxHeight()
            )
        }
    }
}