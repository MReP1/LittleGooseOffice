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
import little.goose.account.ui.AccountRoute
import little.goose.account.ui.analysis.AccountAnalysisActivity
import little.goose.account.ui.transaction.TransactionActivity
import little.goose.common.constants.KEY_SCHEDULE
import little.goose.design.system.component.MovableActionButton
import little.goose.design.system.component.MovableActionButtonState
import little.goose.home.data.*
import little.goose.home.ui.component.IndexTopBar
import little.goose.memorial.data.constants.KEY_MEMORIAL
import little.goose.memorial.ui.MemorialActivity
import little.goose.memorial.ui.MemorialDialogFragment
import little.goose.memorial.ui.MemorialRoute
import little.goose.note.ui.NotebookRoute
import little.goose.note.ui.note.NoteActivity
import little.goose.schedule.ui.ScheduleDialogFragment
import little.goose.schedule.ui.ScheduleRoute
import java.time.LocalDate
import java.util.*

@OptIn(ExperimentalPagerApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier,
) {
    val context = LocalContext.current
    val fragmentManager = (context as FragmentActivity).supportFragmentManager
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
                            IndexScreen(
                                modifier = Modifier.fillMaxSize(),
                                state = indexScreenState
                            )
                        }
                        NOTEBOOK -> {
                            NotebookRoute(modifier = Modifier.fillMaxSize())
                        }
                        ACCOUNT -> {
                            AccountRoute(modifier = Modifier.fillMaxSize())
                        }
                        SCHEDULE -> {
                            ScheduleRoute(
                                modifier = Modifier.fillMaxSize(),
                                onScheduleClick = {
                                    ScheduleDialogFragment.newInstance(it)
                                        .showNow(fragmentManager, KEY_SCHEDULE)
                                }
                            )
                        }
                        MEMORIAL -> {
                            MemorialRoute(
                                modifier = Modifier.fillMaxSize(),
                                onMemorialClick = {
                                    MemorialDialogFragment.newInstance(it)
                                        .showNow(fragmentManager, KEY_MEMORIAL)
                                }
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
                                    ScheduleDialogFragment.newInstance(null, Date())
                                        .showNow(fragmentManager, KEY_SCHEDULE)
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