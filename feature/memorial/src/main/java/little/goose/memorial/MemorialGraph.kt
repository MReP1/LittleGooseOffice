package little.goose.memorial

import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import little.goose.common.constants.KEY_TYPE
import little.goose.memorial.data.constants.KEY_MEMORIAL_ID
import little.goose.memorial.ui.ROUTE_MEMORIAL
import little.goose.memorial.ui.memorialDialogRoute
import little.goose.memorial.ui.memorialRoute
import little.goose.memorial.ui.memorialShowRoute

const val ROUTE_GRAPH_MEMORIAL = "graph_memorial"

fun NavGraphBuilder.memorialGraph(
    onBack: () -> Unit,
    onNavigateToMemorial: (Long) -> Unit,
    onNavigateToMemorialShow: (Long) -> Unit
) {
    navigation(
        startDestination = ROUTE_MEMORIAL +
                "?$KEY_TYPE={$KEY_TYPE}" +
                "?$KEY_MEMORIAL_ID={$KEY_MEMORIAL_ID}",
        route = ROUTE_GRAPH_MEMORIAL
    ) {
        memorialRoute(
            onBack = onBack
        )
        memorialShowRoute(
            onBack = onBack,
            onNavigateToMemorial = onNavigateToMemorial
        )
        memorialDialogRoute(
            onDismissRequest = onBack,
            onNavigateToMemorialShow = onNavigateToMemorialShow
        )
    }
}