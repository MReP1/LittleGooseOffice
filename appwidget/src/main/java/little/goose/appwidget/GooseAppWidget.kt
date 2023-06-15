package little.goose.appwidget

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.Action
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import little.goose.appwidget.component.AppWidgetIcon
import little.goose.appwidget.layout.AppWidgetSizeResponsive
import little.goose.common.constants.KEY_HOME_PAGE
import java.util.Date

class GooseAppWidget : GlanceAppWidget() {

    override val sizeMode: SizeMode = SizeMode.Responsive(
        AppWidgetSizeResponsive.values().map { it.size }.toSet()
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
    Log.d("GooseAppWidgetScreen", "width: ${size.width} height: ${size.height}")
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
            .background(GlanceTheme.colors.primaryContainer)
            .cornerRadius(16.dp)
            .padding(8.dp)
            .clickable(
                actionStartActivity(
                    ComponentName(
                        "little.goose.account",
                        "little.goose.office.MainActivity"
                    )
                )
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AppWidgetIcon(
            drawableResId = R.drawable.icon_fact_check,
            contentDescription = "Schedule",
            modifier = GlanceModifier
                .defaultWeight()
                .fillMaxWidth()
                .clickable(actionStartMainActivity(3))
        )
        AppWidgetIcon(
            drawableResId = R.drawable.icon_event,
            contentDescription = "Memorial",
            modifier = GlanceModifier
                .defaultWeight()
                .fillMaxWidth()
                .clickable(actionStartMainActivity(4))
        )
        AppWidgetIcon(
            drawableResId = R.drawable.icon_edit_note,
            contentDescription = "Note",
            modifier = GlanceModifier
                .defaultWeight()
                .fillMaxWidth()
                .clickable(actionStartMainActivity(1))
        )
        AppWidgetIcon(
            drawableResId = R.drawable.icon_savings,
            contentDescription = "Account",
            modifier = GlanceModifier
                .defaultWeight()
                .fillMaxWidth()
                .clickable(actionStartMainActivity(2))
        )
    }
}

@Composable
private fun HorizontalRectangleAppWidget(
    modifier: GlanceModifier = GlanceModifier
) {
    Row(
        modifier = modifier
            .background(GlanceTheme.colors.primaryContainer)
            .cornerRadius(16.dp)
            .padding(8.dp)
            .clickable(
                actionStartActivity(
                    ComponentName(
                        "little.goose.account",
                        "little.goose.office.MainActivity"
                    )
                )
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AppWidgetIcon(
            drawableResId = R.drawable.icon_fact_check,
            contentDescription = "Schedule",
            modifier = GlanceModifier
                .defaultWeight()
                .fillMaxHeight()
                .clickable(actionStartMainActivity(3))
        )
        AppWidgetIcon(
            drawableResId = R.drawable.icon_event,
            contentDescription = "Memorial",
            modifier = GlanceModifier
                .defaultWeight()
                .fillMaxHeight()
                .clickable(actionStartMainActivity(4))
        )
        AppWidgetIcon(
            drawableResId = R.drawable.icon_edit_note,
            contentDescription = "Note",
            modifier = GlanceModifier
                .defaultWeight()
                .fillMaxHeight()
                .clickable(actionStartMainActivity(1))
        )
        AppWidgetIcon(
            drawableResId = R.drawable.icon_savings,
            contentDescription = "Account",
            modifier = GlanceModifier
                .defaultWeight()
                .fillMaxHeight()
                .clickable(actionStartMainActivity(2))
        )
    }
}

@Composable
private fun SquareAppWidget(
    modifier: GlanceModifier = GlanceModifier
) {
    val context = LocalContext.current
    Column(
        modifier = modifier
            .cornerRadius(16.dp)
            .background(GlanceTheme.colors.primaryContainer)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = GlanceModifier.fillMaxWidth().defaultWeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AppWidgetIcon(
                drawableResId = R.drawable.icon_fact_check,
                contentDescription = "Schedule",
                modifier = GlanceModifier
                    .defaultWeight()
                    .fillMaxHeight()
                    .clickable(actionStartMainActivity(3))
            )
            AppWidgetIcon(
                drawableResId = R.drawable.icon_event,
                contentDescription = "Memorial",
                modifier = GlanceModifier
                    .defaultWeight()
                    .fillMaxHeight()
                    .clickable(actionStartMainActivity(4))
            )
        }
        Row(
            modifier = GlanceModifier.fillMaxWidth().defaultWeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AppWidgetIcon(
                drawableResId = R.drawable.icon_edit_note,
                contentDescription = "Note",
                modifier = GlanceModifier
                    .defaultWeight()
                    .fillMaxHeight()
                    .clickable(actionStartMainActivity(1))
            )
            AppWidgetIcon(
                drawableResId = R.drawable.icon_savings,
                contentDescription = "Account",
                modifier = GlanceModifier
                    .defaultWeight()
                    .fillMaxHeight()
                    .clickable(actionStartMainActivity(2))
            )
        }
    }
}

private fun startANewTransaction(context: Context) {
    PendingIntent.getActivity(
        context, 0, Intent().apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse(
                "little-goose://office/graph_account/transaction" +
                        "?transaction_id=0" +
                        "?time=${Date().time}"
            )
            component = ComponentName(
                "little.goose.account",
                "little.goose.office.MainActivity"
            )
        },
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    ).send()
}

private fun actionStartMainActivity(page: Int): Action {
    return actionStartActivity(
        componentName = ComponentName(
            "little.goose.account",
            "little.goose.office.MainActivity"
        ),
        parameters = actionParametersOf(
            ActionParameters.Key<Int>(KEY_HOME_PAGE) to page
        )
    )
}