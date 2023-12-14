package little.goose.home.ui

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import little.goose.home.data.HomePage

@Composable
internal fun HomeNavigationRailBar(
    modifier: Modifier = Modifier,
    currentHomePage: HomePage,
    onHomePageClick: (HomePage) -> Unit
) {
    NavigationRail(
        modifier = modifier,
        header = {
            Text(text = stringResource(id = currentHomePage.labelRes))
        }
    ) {
        HomePage.entries.forEach { homePage ->
            NavigationRailItem(
                selected = homePage == currentHomePage,
                onClick = { onHomePageClick(homePage) },
                label = { Text(text = stringResource(id = homePage.labelRes)) },
                icon = {
                    Icon(
                        imageVector = homePage.icon,
                        contentDescription = stringResource(id = homePage.labelRes)
                    )
                }
            )
        }
    }
}

@Preview
@Composable
private fun PreviewHomeNavigationRailBar() {
    var currentHomePage by remember { mutableStateOf(HomePage.Home) }
    HomeNavigationRailBar(
        modifier = Modifier
            .fillMaxHeight()
            .wrapContentWidth(),
        currentHomePage = currentHomePage,
        onHomePageClick = { currentHomePage = it }
    )
}