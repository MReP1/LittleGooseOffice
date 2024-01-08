package little.goose.home.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DonutSmall
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Surface
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
import little.goose.settings.R

@Composable
internal fun HomeNavigationRailBar(
    modifier: Modifier = Modifier,
    homePages: List<HomePage> = HomePage.entries,
    currentHomePage: HomePage,
    onHomePageClick: (HomePage) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAccountAnalysis: () -> Unit
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

        if (currentHomePage == HomePage.Account
            && windowSizeClass.heightSizeClass != WindowHeightSizeClass.Compact
        ) {
            IconButton(onClick = onNavigateToAccountAnalysis) {
                Icon(
                    imageVector = Icons.Outlined.DonutSmall,
                    contentDescription = "Analysis"
                )
            }
        }
        IconButton(onClick = onNavigateToSettings) {
            Icon(
                imageVector = Icons.Rounded.Settings,
                contentDescription = stringResource(
                    id = R.string.settings
                )
            )
        }
    }
}

@Preview(name = "landscape", device = "spec:parent=pixel_5,orientation=landscape")
@Composable
private fun PreviewHomeNavigationRailBar() {
    GooseTheme {
        Row(modifier = Modifier.fillMaxSize()) {
            var currentHomePage by remember { mutableStateOf(HomePage.Home) }
            HomeNavigationRailBar(
                modifier = Modifier
                    .fillMaxHeight()
                    .wrapContentWidth(),
                currentHomePage = currentHomePage,
                onHomePageClick = { currentHomePage = it },
                onNavigateToAccountAnalysis = {},
                onNavigateToSettings = {}
            )
            Surface(
                modifier = Modifier
                    .weight(1F)
                    .fillMaxHeight()
            ) {}
        }
    }
}

@Preview(name = "tablet", device = "spec:width=1280dp,height=800dp")
@Composable
private fun PreviewHomeNavigationRailBarTablet() {
    GooseTheme {
        Row(modifier = Modifier.fillMaxSize()) {
            var currentHomePage by remember { mutableStateOf(HomePage.Home) }
            HomeNavigationRailBar(
                modifier = Modifier
                    .fillMaxHeight()
                    .wrapContentWidth(),
                currentHomePage = currentHomePage,
                onHomePageClick = { currentHomePage = it },
                onNavigateToAccountAnalysis = {},
                onNavigateToSettings = {}
            )
            Surface(
                modifier = Modifier
                    .weight(1F)
                    .fillMaxHeight()
            ) {}
        }
    }
}