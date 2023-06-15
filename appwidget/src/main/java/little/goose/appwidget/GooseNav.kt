package little.goose.appwidget

import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.glance.GlanceModifier
import androidx.glance.action.Action
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import little.goose.appwidget.component.AppWidgetIcon
import little.goose.common.constants.DEEP_LINK_THEME_AND_HOST
import little.goose.common.constants.KEY_HOME_PAGE
import java.util.Date

internal enum class GooseNav(
    val drawableResId: Int,
    val contentDescription: String,
    val homePageAction: Action,
    val addAction: Action
) {
    NOTE(
        drawableResId = R.drawable.icon_edit_note,
        contentDescription = "Note",
        homePageAction = actionStartHomeScreen(1),
        addAction = actionStartNoteScreen()
    ),
    ACCOUNT(
        drawableResId = R.drawable.icon_savings,
        contentDescription = "Account",
        homePageAction = actionStartHomeScreen(2),
        addAction = actionStartTransactionScreen()
    ),
    SCHEDULE(
        drawableResId = R.drawable.icon_fact_check,
        contentDescription = "Schedule",
        homePageAction = actionStartHomeScreen(3),
        addAction = actionStartScheduleScreen()
    ),
    MEMORIAL(
        drawableResId = R.drawable.icon_event,
        contentDescription = "Memorial",
        homePageAction = actionStartHomeScreen(4),
        addAction = actionStartMemorialScreen()
    );

    @Composable
    internal fun IconWidget(
        modifier: GlanceModifier
    ) {
        AppWidgetIcon(
            drawableResId = this.drawableResId,
            contentDescription = this.contentDescription,
            modifier = modifier.clickable(homePageAction)
        )
    }
}

internal fun actionStartScheduleScreen(): Action {
    return actionStartMainActivity(
        data = Uri.parse(
            "$DEEP_LINK_THEME_AND_HOST/dialog_schedule" +
                    "/schedule_id=0"
        ),
        parameters = actionParametersOf(
            ActionParameters.Key<Int>(KEY_HOME_PAGE) to 3
        )
    )
}

internal fun actionStartNoteScreen(): Action {
    return actionStartMainActivity(
        data = Uri.parse(
            "$DEEP_LINK_THEME_AND_HOST/note" +
                    "/note_id=-1"
        ),
        parameters = actionParametersOf(
            ActionParameters.Key<Int>(KEY_HOME_PAGE) to 1
        )
    )
}

internal fun actionStartMemorialScreen(): Action {
    return actionStartMainActivity(
        data = Uri.parse(
            "$DEEP_LINK_THEME_AND_HOST/graph_memorial/memorial" +
                    "?type=Add" +
                    "?memorial_id=0"
        ),
        parameters = actionParametersOf(
            ActionParameters.Key<Int>(KEY_HOME_PAGE) to 4
        )
    )
}

internal fun actionStartTransactionScreen(): Action {
    return actionStartMainActivity(
        data = Uri.parse(
            "$DEEP_LINK_THEME_AND_HOST/graph_account/transaction" +
                    "?transaction_id=0" +
                    "?time=${Date().time}"
        ),
        parameters = actionParametersOf(
            ActionParameters.Key<Int>(KEY_HOME_PAGE) to 2
        )
    )
}

internal fun actionStartHomeScreen(page: Int): Action {
    return actionStartMainActivity(
        parameters = actionParametersOf(
            ActionParameters.Key<Int>(KEY_HOME_PAGE) to page
        )
    )
}

internal fun actionStartMainActivity(
    data: Uri? = null,
    parameters: ActionParameters? = null
): Action {
    return androidx.glance.appwidget.action.actionStartActivity(
        Intent().apply {
            action = Intent.ACTION_VIEW
            this.data = data
            component = ComponentName(
                "little.goose.account",
                "little.goose.office.MainActivity"
            )
        },
        parameters = parameters ?: actionParametersOf()
    )
}