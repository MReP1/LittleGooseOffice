package little.goose.home.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import little.goose.account.ui.AccountHome
import little.goose.account.ui.component.AccountTitleState
import little.goose.account.ui.component.MonthSelectorState
import little.goose.account.ui.component.TransactionColumnState
import little.goose.design.system.component.MovableActionButtonState
import little.goose.home.data.ACCOUNT
import little.goose.home.data.HOME
import little.goose.home.data.HomePage
import little.goose.home.data.MEMORIAL
import little.goose.home.data.NOTEBOOK
import little.goose.memorial.data.entities.Memorial
import little.goose.memorial.ui.MemorialHome
import little.goose.memorial.ui.component.MemorialColumnState
import little.goose.note.ui.NoteColumnState
import little.goose.note.ui.NotebookHome
import little.goose.search.SearchType
import java.util.Date

@Composable
fun HomePageContent(
    modifier: Modifier,
    pagerState: PagerState,
    indexScreenState: IndexScreenState,
    currentHomePage: HomePage,
    onNavigateToMemorialAdd: () -> Unit,
    onNavigateToMemorial: (memorialId: Long) -> Unit,
    onNavigateToTransactionScreen: (transactionId: Long) -> Unit,
    onNavigateToTransaction: (transactionId: Long?, date: Date?) -> Unit,
    onNavigateToNote: (noteId: Long?) -> Unit,
    onNavigateToSearch: (SearchType) -> Unit,
    onNavigateToAccountAnalysis: () -> Unit,
    noteColumnState: NoteColumnState,
    transactionColumnState: TransactionColumnState,
    memorialColumnState: MemorialColumnState,
    topMemorial: Memorial?,
    accountTitleState: AccountTitleState,
    monthSelectorState: MonthSelectorState
) {
    val buttonState = remember { MovableActionButtonState() }

    val isMultiSelecting = when (currentHomePage) {
        HomePage.Notebook -> noteColumnState.isMultiSelecting
        HomePage.Account -> transactionColumnState.isMultiSelecting
        HomePage.Memorial -> memorialColumnState.isMultiSelecting
        else -> false
    }

    LaunchedEffect(isMultiSelecting) {
        if (isMultiSelecting) {
            buttonState.expend()
        } else {
            buttonState.fold()
        }
    }

    Box(modifier = modifier) {
        HorizontalPager(
            modifier = Modifier.fillMaxSize(),
            state = pagerState,
            userScrollEnabled = true
        ) { index ->
            when (index) {
                HOME -> {
                    IndexHome(
                        modifier = Modifier.fillMaxSize(),
                        state = indexScreenState,
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
                        onNavigateToSearch = { onNavigateToSearch(SearchType.Note) }
                    )
                }

                ACCOUNT -> {
                    AccountHome(
                        modifier = Modifier.fillMaxSize(),
                        transactionColumnState = transactionColumnState,
                        accountTitleState = accountTitleState,
                        monthSelectorState = monthSelectorState,
                        onNavigateToTransactionScreen = onNavigateToTransactionScreen,
                        onNavigateToSearch = { onNavigateToSearch(SearchType.Transaction) },
                        onNavigateToAccountAnalysis = onNavigateToAccountAnalysis
                    )
                }

                MEMORIAL -> {
                    MemorialHome(
                        modifier = Modifier.fillMaxSize(),
                        topMemorial = topMemorial,
                        memorialColumnState = memorialColumnState,
                        onNavigateToMemorial = onNavigateToMemorial,
                        onNavigateToSearch = { onNavigateToSearch(SearchType.Memorial) }
                    )
                }
            }
        }
        if (currentHomePage != HomePage.Home) {
            HomeMovableActionButton(
                modifier = Modifier.align(Alignment.BottomEnd),
                buttonState = buttonState,
                isMultiSelecting = isMultiSelecting,
                currentHomePage = currentHomePage,
                onDeleteNotes = {
                    noteColumnState.deleteNotes(noteColumnState.multiSelectedNotes.toList())
                    noteColumnState.cancelMultiSelecting()
                },
                onNavigateToNewNote = {
                    onNavigateToNote(null)
                },
                onDeleteTransactions = {
                    transactionColumnState.deleteTransactions(
                        transactionColumnState.multiSelectedTransactions.toList()
                    )
                    transactionColumnState.cancelMultiSelecting()
                },
                onNavigateToNewTransaction = {
                    onNavigateToTransaction(null, Date())
                },
                onDeleteMemorials = {
                    memorialColumnState.deleteMemorials(
                        memorialColumnState.multiSelectedMemorials.toList()
                    )
                    memorialColumnState.cancelMultiSelecting()
                },
                onNavigateToNewMemorial = onNavigateToMemorialAdd,
                onSelectAllNotes = noteColumnState.selectAllNotes,
                onSelectAllTransactions = transactionColumnState.selectAllTransactions,
                onSelectAllMemorials = memorialColumnState.selectAllMemorial,
                onCancelTransactionsMultiSelecting = transactionColumnState.cancelMultiSelecting,
                onCancelMemorialsMultiSelecting = memorialColumnState.cancelMultiSelecting,
                onCancelNotesMultiSelecting = noteColumnState.cancelMultiSelecting
            )
        }
    }
}