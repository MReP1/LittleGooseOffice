package little.goose.home.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.DonutSmall
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.zIndex
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import little.goose.account.ui.AccountHome
import little.goose.account.ui.analysis.AccountAnalysisActivity
import little.goose.account.ui.transaction.TransactionActivity
import little.goose.common.constants.KEY_SCHEDULE
import little.goose.design.system.component.MovableActionButton
import little.goose.design.system.component.MovableActionButtonState
import little.goose.home.data.*
import little.goose.home.ui.component.IndexTopBar
import little.goose.memorial.ui.*
import little.goose.note.ui.NotebookHome
import little.goose.note.ui.note.NoteActivity
import little.goose.schedule.ui.ScheduleHome
import little.goose.search.SearchActivity
import little.goose.search.SearchType
import java.time.LocalDate
import java.util.*

@OptIn(ExperimentalPagerApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState()
    var scrollable by remember { mutableStateOf(true) }
    DisposableEffect(pagerState.currentPage) {
        scrollable = pagerState.currentPage != HOME
        onDispose { }
    }
    val currentHomePage = remember(pagerState.currentPage) {
        HomePage.fromPageIndex(pagerState.currentPage)
    }

    var today by remember { mutableStateOf(LocalDate.now()) }
    SideEffect {
        val realToday = LocalDate.now()
        if (today != realToday) {
            today = realToday
        }
    }

    val indexViewModel = viewModel<IndexViewModel>()
    val indexScreenState by indexViewModel.indexScreenState.collectAsState()

    val buttonState = remember { MovableActionButtonState() }

    Scaffold(
        modifier = modifier,
        topBar = {
            if (currentHomePage != HomePage.Home) {
                CenterAlignedTopAppBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    title = {
                        Text(text = stringResource(id = currentHomePage.labelRes))
                    }
                )
            } else {
                IndexTopBar(
                    modifier = Modifier.fillMaxWidth(),
                    currentDay = indexScreenState.currentDay,
                    today = today
                )
            }
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                HorizontalPager(
                    count = 5,
                    modifier = Modifier
                        .fillMaxSize()
                        .zIndex(-1F),
                    state = pagerState,
                    userScrollEnabled = scrollable
                ) { index ->
                    when (index) {
                        HOME -> {
                            IndexHome(
                                modifier = Modifier.fillMaxSize(),
                                state = indexScreenState
                            )
                        }
                        NOTEBOOK -> {
                            NotebookHome(
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        ACCOUNT -> {
                            AccountHome(
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        SCHEDULE -> {
                            ScheduleHome(
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        MEMORIAL -> {
                            MemorialHome(
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
                if (currentHomePage != HomePage.Home) {
                    MovableActionButton(
                        modifier = Modifier.align(Alignment.BottomEnd),
                        state = buttonState,
                        mainButtonContent = {
                            Icon(
                                imageVector = Icons.Rounded.Add,
                                contentDescription = "More"
                            )
                        },
                        onMainButtonClick = {
                            when (currentHomePage) {
                                HomePage.Notebook -> {
                                    NoteActivity.openAdd(context)
                                }
                                HomePage.ACCOUNT -> {
                                    TransactionActivity.openAdd(context)
                                }
                                HomePage.Schedule -> {

                                }
                                HomePage.Memorial -> {
                                    MemorialActivity.openAdd(context)
                                }
                                else -> {}
                            }
                        },
                        topSubButtonContent = {
                            when (currentHomePage) {
                                HomePage.Notebook -> {

                                }
                                HomePage.ACCOUNT -> {
                                    Icon(
                                        imageVector = Icons.Outlined.DonutSmall,
                                        contentDescription = "Analysis"
                                    )
                                }
                                HomePage.Schedule -> {

                                }
                                HomePage.Memorial -> {

                                }
                                else -> {}
                            }
                        },
                        onTopSubButtonClick = {
                            when (currentHomePage) {
                                HomePage.Notebook -> {

                                }
                                HomePage.ACCOUNT -> {
                                    AccountAnalysisActivity.open(context)
                                }
                                HomePage.Schedule -> {

                                }
                                HomePage.Memorial -> {

                                }
                                else -> {}
                            }
                        },
                        bottomSubButtonContent = {
                            Icon(imageVector = Icons.Rounded.Search, contentDescription = "search")
                        },
                        onBottomSubButtonClick = {
                            when (currentHomePage) {
                                HomePage.Notebook -> {
                                    SearchActivity.open(context, SearchType.Note)
                                }
                                HomePage.ACCOUNT -> {
                                    SearchActivity.open(context, SearchType.Transaction)
                                }
                                HomePage.Schedule -> {
                                    SearchActivity.open(context, SearchType.Schedule)
                                }
                                HomePage.Memorial -> {
                                    SearchActivity.open(context, SearchType.Memorial)
                                }
                                else -> {}
                            }
                        }
                    )

                    Box(modifier = Modifier.fillMaxSize().zIndex(-0.5F).run {
                        if (buttonState.isExpended.value) {
                            clickable(indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) { scope.launch { buttonState.fold() } }
                        } else this
                    })
                }
            }
        },
        bottomBar = {
            Box {
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
                Box(modifier = Modifier.matchParentSize().zIndex(1F).run {
                    if (buttonState.isExpended.value) {
                        clickable(indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { scope.launch { buttonState.fold() } }
                    } else this
                })
            }
        }
    )
}