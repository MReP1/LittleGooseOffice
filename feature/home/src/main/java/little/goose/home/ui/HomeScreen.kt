package little.goose.home.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import little.goose.design.system.component.MovableActionButton
import little.goose.design.system.component.MovableActionButtonState
import little.goose.home.data.*
import little.goose.memorial.ui.MemorialFragmentRoute
import little.goose.schedule.ui.ScheduleRoute

@OptIn(ExperimentalPagerApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier,
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState()
    val currentHomePage = remember(pagerState.currentPage) {
        HomePage.fromPageIndex(pagerState.currentPage)
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                title = {
                    Text(text = stringResource(id = currentHomePage.labelRes))
                }
            )
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                HorizontalPager(
                    count = 5,
                    modifier = Modifier.fillMaxSize(),
                    state = pagerState
                ) { index ->
                    when (index) {
                        HOME -> {
                            Box(modifier = Modifier.fillMaxSize())
                        }
                        NOTEBOOK -> {
                            Box(modifier = Modifier.fillMaxSize())
                        }
                        ACCOUNT -> {
                            Box(modifier = Modifier.fillMaxSize())
                        }
                        SCHEDULE -> {
                            ScheduleRoute(
                                modifier = Modifier.fillMaxSize(),
                                onScheduleClick = {

                                }
                            )
                        }
                        MEMORIAL -> {
                            MemorialFragmentRoute(
                                modifier = Modifier.fillMaxSize(),
                                onMemorialClick = {}
                            )
                        }
                    }
                }
                val buttonState = remember { MovableActionButtonState() }
                if (currentHomePage != HomePage.Home) {
                    MovableActionButton(
                        modifier = Modifier.align(Alignment.BottomEnd),
                        state = buttonState,
                        mainButtonContent = {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "More"
                            )
                        },
                        onMainButtonClick = {

                        },
                        topSubButtonContent = {

                        },
                        onTopSubButtonClick = {

                        },
                        bottomSubButtonContent = {

                        },
                        onBottomSubButtonClick = {

                        }
                    )
                }
            }
        },
        bottomBar = {
            BottomAppBar(
                actions = {
                    for (homePage in HomePage.values()) {
                        NavigationBarItem(
                            selected = homePage == currentHomePage,
                            onClick = {
                                scope.launch(Dispatchers.Main.immediate) {
                                    pagerState.scrollToPage(homePage.index)
                                }
                            },
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
    )

}