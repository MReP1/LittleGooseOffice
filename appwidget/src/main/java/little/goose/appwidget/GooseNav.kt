package little.goose.appwidget

import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import androidx.glance.action.Action
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
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
        homePageAction = actionStartMainActivity(1),
        addAction = actionStartMainActivity(1)
    ),
    ACCOUNT(
        drawableResId = R.drawable.icon_savings,
        contentDescription = "Account",
        homePageAction = actionStartMainActivity(2),
        addAction = actionStartMainActivity(2)
    ),
    SCHEDULE(
        drawableResId = R.drawable.icon_fact_check,
        contentDescription = "Schedule",
        homePageAction = actionStartMainActivity(3),
        addAction = actionStartMainActivity(3)
    ),
    MEMORIAL(
        drawableResId = R.drawable.icon_event,
        contentDescription = "Memorial",
        homePageAction = actionStartMainActivity(4),
        addAction = actionStartMainActivity(4)
    );
}

internal fun actionStartTransactionScreen(): Action {
    return androidx.glance.appwidget.action.actionStartActivity(
        Intent().apply {
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
        }
    )
}

internal fun actionStartMainActivity(page: Int): Action {
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