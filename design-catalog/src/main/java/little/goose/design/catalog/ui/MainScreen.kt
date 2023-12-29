package little.goose.design.catalog.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import little.goose.design.catalog.ui.chart.ChartCatalogScreen
import little.goose.design.catalog.ui.chart.ROUTE_CHART_CATALOG

@Composable
internal fun MainScreen() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = ROUTE_HOME) {
        composable(ROUTE_HOME) {
            HomeScreen(
                onNavigateToChart = {
                    navController.navigate(ROUTE_CHART_CATALOG)
                }
            )
        }
        composable(ROUTE_CHART_CATALOG) {
            ChartCatalogScreen()
        }
    }
}

