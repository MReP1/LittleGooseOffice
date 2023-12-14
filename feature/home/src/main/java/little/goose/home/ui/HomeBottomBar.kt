package little.goose.home.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
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
internal fun HomeBottomBar(
    modifier: Modifier = Modifier,
    currentHomePage: HomePage,
    homePageList: List<HomePage>,
    onHomePageClick: (HomePage) -> Unit
) {
    BottomAppBar(
        modifier = modifier,
        actions = {
            for (homePage in homePageList) {
                NavigationBarItem(
                    selected = homePage == currentHomePage,
                    onClick = { onHomePageClick(homePage) },
                    icon = {
                        Icon(
                            imageVector = homePage.icon,
                            contentDescription = stringResource(id = homePage.labelRes)
                        )
                    }
                )
            }
        }
    )
}

@Preview
@Composable
private fun PreviewHomeBottomBar() {
    var currentHomePage by remember { mutableStateOf(HomePage.Home) }
    HomeBottomBar(
        modifier = Modifier.fillMaxWidth(),
        currentHomePage = currentHomePage,
        homePageList = HomePage.entries,
        onHomePageClick = { currentHomePage = it }
    )
}