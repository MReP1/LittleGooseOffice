package little.goose.home.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import little.goose.account.ui.AccountHome
import little.goose.account.ui.AccountHomeState
import little.goose.design.system.theme.LocalWindowSizeClass
import little.goose.home.data.ACCOUNT
import little.goose.home.data.HOME
import little.goose.home.data.HomePage
import little.goose.home.data.MEMORIAL
import little.goose.home.data.NOTEBOOK
import little.goose.home.ui.index.IndexHome
import little.goose.home.ui.index.IndexHomeState
import little.goose.memorial.ui.MemorialHome
import little.goose.memorial.ui.MemorialHomeState
import little.goose.note.ui.notebook.NoteColumnState
import little.goose.note.ui.notebook.NotebookHome
import little.goose.note.ui.notebook.NotebookIntent
import little.goose.search.SearchType
import little.goose.shared.ui.button.MovableActionButtonState
import java.util.Date

@Composable
fun HomePageContent(
    modifier: Modifier,
    pagerState: PagerState,
    indexHomeState: IndexHomeState,
    currentHomePage: HomePage,
    onNavigateToMemorialAdd: () -> Unit,
    onNavigateToMemorial: (memorialId: Long) -> Unit,
    onNavigateToTransactionScreen: (transactionId: Long) -> Unit,
    onNavigateToTransaction: (transactionId: Long?, date: Date?) -> Unit,
    onNavigateToNote: (noteId: Long?) -> Unit,
    onNavigateToSearch: (SearchType) -> Unit,
    onNavigateToAccountAnalysis: () -> Unit,
    memorialHomeState: MemorialHomeState,
    accountHomeState: AccountHomeState,
    noteColumnState: NoteColumnState,
    noteAction: (NotebookIntent) -> Unit
) {
    val buttonState = remember { MovableActionButtonState() }

    val isMultiSelecting = when (currentHomePage) {
        HomePage.Notebook -> noteColumnState.isMultiSelecting
        HomePage.Account -> accountHomeState.transactionColumnState.isMultiSelecting
        HomePage.Memorial -> memorialHomeState.memorialColumnState.isMultiSelecting
        else -> false
    }

    LaunchedEffect(isMultiSelecting) {
        if (isMultiSelecting) {
            buttonState.expend()
        } else {
            buttonState.fold()
        }
    }

    val windowSizeClass = LocalWindowSizeClass.current
    val isWindowWidthSizeCompat = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact

    Box(modifier = modifier) {
        HorizontalPager(
            modifier = Modifier.fillMaxSize(),
            state = pagerState,
            userScrollEnabled = isWindowWidthSizeCompat
        ) { index ->
            when (index) {
                HOME -> {
                    IndexHome(
                        modifier = Modifier.fillMaxSize(),
                        state = indexHomeState,
                        onTransactionAdd = { time ->
                            onNavigateToTransaction(null, time)
                        },
                        onTransactionClick = { transaction ->
                            transaction.id?.run(onNavigateToTransactionScreen)
                        },
                        onMemorialClick = { memorial ->
                            memorial.id?.let(onNavigateToMemorial)
                        }
                    )
                }

                NOTEBOOK -> {
                    NotebookHome(
                        modifier = Modifier.fillMaxSize(),
                        noteColumnState = noteColumnState,
                        onNavigateToNote = onNavigateToNote,
                        onNavigateToSearch = { onNavigateToSearch(SearchType.Note) },
                        action = noteAction
                    )
                }

                ACCOUNT -> {
                    AccountHome(
                        modifier = Modifier.fillMaxSize(),
                        accountHomeState = accountHomeState,
                        onNavigateToTransactionScreen = onNavigateToTransactionScreen,
                        onNavigateToSearch = { onNavigateToSearch(SearchType.Transaction) },
                        onNavigateToAccountAnalysis = onNavigateToAccountAnalysis
                    )
                }

                MEMORIAL -> {
                    MemorialHome(
                        modifier = Modifier.fillMaxSize(),
                        memorialHomeState = memorialHomeState,
                        onNavigateToMemorial = onNavigateToMemorial,
                        onNavigateToSearch = { onNavigateToSearch(SearchType.Memorial) }
                    )
                }
            }
        }
        if (currentHomePage != HomePage.Home && isWindowWidthSizeCompat) {
            HomeMovableActionButton(
                modifier = Modifier.align(Alignment.BottomEnd),
                buttonState = buttonState,
                isMultiSelecting = isMultiSelecting,
                currentHomePage = currentHomePage,
                onDeleteNotes = {
                    noteAction(NotebookIntent.DeleteMultiSelectingNotes)
                },
                onNavigateToNewNote = {
                    onNavigateToNote(null)
                },
                onDeleteTransactions = {
                    accountHomeState.transactionColumnState.deleteTransactions(
                        accountHomeState.transactionColumnState.multiSelectedTransactions.toList()
                    )
                    accountHomeState.transactionColumnState.cancelMultiSelecting()
                },
                onNavigateToNewTransaction = {
                    onNavigateToTransaction(null, Date())
                },
                onDeleteMemorials = {
                    memorialHomeState.memorialColumnState.deleteMemorials(
                        memorialHomeState.memorialColumnState.multiSelectedMemorials.toList()
                    )
                    memorialHomeState.memorialColumnState.cancelMultiSelecting()
                },
                onNavigateToNewMemorial = onNavigateToMemorialAdd,
                onSelectAllNotes = { noteAction(NotebookIntent.SelectAllNotes) },
                onSelectAllTransactions = accountHomeState.transactionColumnState.selectAllTransactions,
                onSelectAllMemorials = memorialHomeState.memorialColumnState.selectAllMemorial,
                onCancelTransactionsMultiSelecting = accountHomeState.transactionColumnState.cancelMultiSelecting,
                onCancelMemorialsMultiSelecting = memorialHomeState.memorialColumnState.cancelMultiSelecting,
                onCancelNotesMultiSelecting = { noteAction(NotebookIntent.CancelMultiSelecting) }
            )
        }
    }
}