package little.goose.home.ui

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import little.goose.design.system.theme.GooseTheme
import little.goose.design.system.theme.LocalWindowSizeClass
import little.goose.home.data.HomePage

@Composable
internal fun HomeNavigationRailBar(
    modifier: Modifier = Modifier,
    homePages: List<HomePage> = HomePage.entries,
    currentHomePage: HomePage,
    onHomePageClick: (HomePage) -> Unit,
    bottomContent: @Composable ColumnScope.(homePage: HomePage) -> Unit
) {
    val windowSizeClass = LocalWindowSizeClass.current
    NavigationRail(
        modifier = modifier,
        header = {
            Text(text = stringResource(id = currentHomePage.labelRes))
        }
    ) {
        homePages.forEach { homePage ->
            NavigationRailItem(
                selected = homePage == currentHomePage,
                onClick = { onHomePageClick(homePage) },
                label = if (windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact)
                    null
                else {
                    { Text(text = stringResource(id = homePage.labelRes)) }
                },
                icon = {
                    Icon(
                        imageVector = homePage.icon,
                        contentDescription = stringResource(id = homePage.labelRes)
                    )
                }
            )
        }
        Spacer(modifier = Modifier.weight(1F))
        bottomContent(currentHomePage)
    }
}

@Preview
@Composable
private fun PreviewHomeNavigationRailBar() = GooseTheme {
    var currentHomePage by remember { mutableStateOf(HomePage.Home) }
    HomeNavigationRailBar(
        modifier = Modifier
            .fillMaxHeight()
            .wrapContentWidth(),
        currentHomePage = currentHomePage,
        onHomePageClick = { currentHomePage = it },
        bottomContent = {}
    )
}