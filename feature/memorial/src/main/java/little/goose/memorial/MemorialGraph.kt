package little.goose.memorial

import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import little.goose.memorial.ui.FULL_ROUTE_MEMORIAL
import little.goose.memorial.ui.memorialRoute

const val ROUTE_GRAPH_MEMORIAL = "graph_memorial"

fun NavGraphBuilder.memorialGraph(
    onBack: () -> Unit
) {
    navigation(
        startDestination = FULL_ROUTE_MEMORIAL,
        route = ROUTE_GRAPH_MEMORIAL
    ) {
        memorialRoute(
            onBack = onBack
        )
    }
}