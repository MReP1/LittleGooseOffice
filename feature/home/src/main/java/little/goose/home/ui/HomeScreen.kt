package little.goose.home.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.DonutSmall
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SubdirectoryArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import little.goose.account.ui.AccountHome
import little.goose.account.ui.analysis.AccountAnalysisActivity
import little.goose.account.ui.transaction.TransactionActivity
import little.goose.design.system.component.MovableActionButton
import little.goose.design.system.component.MovableActionButtonState
import little.goose.home.data.*
import little.goose.home.ui.component.IndexTopBar
import little.goose.memorial.ui.*
import little.goose.note.ui.NotebookHome
import little.goose.note.ui.note.NoteActivity
import little.goose.schedule.data.entities.Schedule
import little.goose.schedule.ui.ScheduleDialog
import little.goose.schedule.ui.ScheduleHome
import little.goose.schedule.ui.ScheduleViewModel
import little.goose.schedule.ui.rememberScheduleDialogState
import little.goose.search.SearchActivity
import little.goose.search.SearchType
import java.util.*

@OptIn(ExperimentalPagerApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState()
    val scrollable by remember { derivedStateOf { pagerState.currentPage != HOME } }
    val currentHomePage = remember(pagerState.currentPage) {
        HomePage.fromPageIndex(pagerState.currentPage)
    }

    val scheduleViewModel = viewModel<ScheduleViewModel>()
    val indexViewModel = viewModel<IndexViewModel>()
    val indexScreenState by indexViewModel.indexScreenState.collectAsState()
    val indexTopBarState by indexViewModel.indexTopBarState.collectAsState()
    val buttonState = remember { MovableActionButtonState() }
    val scheduleDialogState = rememberScheduleDialogState()
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
                    state = indexTopBarState
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
                                state = indexScreenState,
                                onScheduleClick = { scheduleDialogState.show(it) }
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
                            Icon(imageVector = Icons.Rounded.Add, contentDescription = "More")
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
                                    scheduleDialogState.show(Schedule())
                                }
                                HomePage.Memorial -> {
                                    MemorialActivity.openAdd(context)
                                }
                                else -> {}
                            }
                        },
                        topSubButtonContent = {
                            Icon(imageVector = Icons.Rounded.Search, contentDescription = "search")
                        },
                        onTopSubButtonClick = {
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
                        },
                        bottomSubButtonContent = {
                            when (currentHomePage) {
                                HomePage.ACCOUNT -> {
                                    Icon(
                                        imageVector = Icons.Outlined.DonutSmall,
                                        contentDescription = "Analysis"
                                    )
                                }
                                else -> {
                                    Icon(
                                        imageVector = Icons.Rounded.SubdirectoryArrowRight,
                                        contentDescription = "fold"
                                    )
                                }
                            }
                        },
                        onBottomSubButtonClick = {
                            when (currentHomePage) {
                                HomePage.ACCOUNT -> {
                                    AccountAnalysisActivity.open(context)
                                }
                                else -> scope.launch {
                                    buttonState.fold()
                                }
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
    ScheduleDialog(
        state = scheduleDialogState,
        onDelete = scheduleViewModel::deleteSchedule,
        onAdd = scheduleViewModel::addSchedule,
        onModify = scheduleViewModel::updateSchedule
    )
}