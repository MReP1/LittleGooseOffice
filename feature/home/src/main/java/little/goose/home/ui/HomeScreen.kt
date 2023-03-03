package little.goose.home.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import little.goose.account.data.entities.Transaction
import little.goose.account.ui.AccountRoute
import little.goose.account.ui.analysis.AccountAnalysisActivity
import little.goose.account.ui.transaction.TransactionActivity
import little.goose.account.ui.transaction.TransactionDialog
import little.goose.account.ui.transaction.rememberTransactionDialogState
import little.goose.common.constants.KEY_SCHEDULE
import little.goose.design.system.component.MovableActionButton
import little.goose.design.system.component.MovableActionButtonState
import little.goose.design.system.component.dialog.DeleteDialog
import little.goose.design.system.component.dialog.rememberDialogState
import little.goose.home.data.*
import little.goose.memorial.data.constants.KEY_MEMORIAL
import little.goose.memorial.ui.MemorialActivity
import little.goose.memorial.ui.MemorialDialogFragment
import little.goose.memorial.ui.MemorialRoute
import little.goose.note.ui.NotebookRoute
import little.goose.note.ui.note.NoteActivity
import little.goose.schedule.ui.ScheduleDialogFragment
import little.goose.schedule.ui.ScheduleRoute
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