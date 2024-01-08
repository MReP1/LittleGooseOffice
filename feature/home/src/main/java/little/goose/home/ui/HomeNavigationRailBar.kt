package little.goose.home.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DonutSmall
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import little.goose.common.utils.DateTimeUtils
import little.goose.design.system.theme.GooseTheme
import little.goose.design.system.theme.LocalWindowSizeClass
import little.goose.design.system.util.PreviewMultipleScreenSizes
import little.goose.home.data.HomePage
import little.goose.settings.R
import java.time.LocalDate

@Composable
internal fun HomeNavigationRailBar(
    modifier: Modifier = Modifier,
    homePages: List<HomePage> = HomePage.entries,
    currentHomePage: HomePage,
    onAddClick: () -> Unit = {},
    currentDayText: String = LocalDate.now().let {
        DateTimeUtils.getTimeFormatTen(it.month.value) + "-" + DateTimeUtils.getTimeFormatTen(it.dayOfMonth)
    },
    todayText: String = LocalDate.now().dayOfMonth.toString(),
    onHomePageClick: (HomePage) -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToAccountAnalysis: () -> Unit = {}
) {
    val windowSizeClass = LocalWindowSizeClass.current
    val isWindowHeightCompat = windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact
    NavigationRail(
        modifier = modifier,
        header = {
            Text(
                text = if (currentHomePage == HomePage.Home && isWindowHeightCompat) {
                    currentDayText
                } else {
                    stringResource(id = currentHomePage.labelRes)
                },
                modifier = Modifier.padding(vertical = 6.dp)
            )
            if (!isWindowHeightCompat) {
                FloatingActionButton(
                    onClick = onAddClick,
                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                ) {
                    if (currentHomePage == HomePage.Home) {
                        Box(
                            modifier = Modifier.wrapContentSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.CalendarToday,
                                contentDescription = "Today"
                            )
                            Text(
                                text = todayText,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    } else {
                        Icon(imageVector = Icons.Rounded.Add, contentDescription = "Add")
                    }
                }
            }
        }
    ) {
        homePages.forEach { homePage ->
            NavigationRailItem(
                selected = homePage == currentHomePage,
                onClick = { onHomePageClick(homePage) },
                label = if (isWindowHeightCompat) null else {
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

@PreviewMultipleScreenSizes
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
                onAddClick = {},
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