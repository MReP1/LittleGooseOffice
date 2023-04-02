package little.goose.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.ViewTreeObserver.OnWindowFocusChangeListener
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import little.goose.design.system.theme.AccountTheme

@AndroidEntryPoint
class SearchActivity : ComponentActivity() {

    companion object {
        fun open(context: Context, searchType: SearchType) {
            context.startActivity(
                Intent(context, SearchActivity::class.java)
                    .apply { putExtra(SearchType.KEY_SEARCH_TYPE, searchType as Parcelable) }
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AccountTheme {
                SearchScreen(
                    modifier = Modifier.fillMaxSize(),
                    onBack = ::finish
                )
            }
        }
    }

}

@Composable
private fun SearchScreen(
    modifier: Modifier,
    onBack: () -> Unit
) {
    val viewModel = hiltViewModel<SearchViewModel>()
    var keyword by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        snapshotFlow {
            keyword
        }.collect {
            viewModel.search(it)
        }
    }

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel.event) {
        suspend fun showDeletedSnackbar() {
            snackbarHostState.showSnackbar(
                message = context.getString(R.string.deleted),
                withDismissAction = true
            )
        }
        viewModel.event.collect { event ->
            when (event) {
                is SearchViewModel.Event.DeleteMemorials -> showDeletedSnackbar()
                is SearchViewModel.Event.DeleteNotes -> showDeletedSnackbar()
                is SearchViewModel.Event.DeleteSchedules -> showDeletedSnackbar()
                is SearchViewModel.Event.DeleteTransactions -> showDeletedSnackbar()
            }
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(snackbarHostState) { snackbarData ->
                Snackbar(snackbarData)
            }
        },
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                title = {
                    val focusRequester = remember { FocusRequester() }
                    BasicTextField(
                        value = keyword,
                        onValueChange = { keyword = it },
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        maxLines = 1
                    )
                    val view = LocalView.current
                    DisposableEffect(focusRequester) {
                        val listener = object : OnWindowFocusChangeListener {
                            override fun onWindowFocusChanged(hasFocus: Boolean) {
                                if (hasFocus) {
                                    focusRequester.requestFocus()
                                    view.viewTreeObserver.removeOnWindowFocusChangeListener(this)
                                }
                            }
                        }
                        view.viewTreeObserver.addOnWindowFocusChangeListener(listener)
                        onDispose {
                            view.viewTreeObserver.removeOnWindowFocusChangeListener(listener)
                        }
                    }
                },
                actions = {
                    if (keyword.isNotEmpty()) {
                        IconButton(onClick = { keyword = "" }) {
                            Icon(
                                imageVector = Icons.Rounded.Clear,
                                contentDescription = "Clear"
                            )
                        }
                    }
                }
            )
        },
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                when (viewModel.type) {
                    SearchType.Transaction -> when (viewModel.transactionState) {
                        is SearchViewModel.State.Data -> {
                            val transactionColumnState by viewModel.transactionColumnState
                                .collectAsState()
                            SearchTransactionScreen(
                                modifier = Modifier.fillMaxSize(),
                                transactionColumnState = transactionColumnState,
                                onDeleteTransaction = viewModel::deleteTransaction
                            )
                        }
                        SearchViewModel.State.Empty -> {
                        }
                    }
                    SearchType.Note -> when (viewModel.noteState) {
                        is SearchViewModel.State.Data -> {
                            val noteGridState by viewModel.noteGridState.collectAsState()
                            SearchNoteScreen(
                                modifier = Modifier.fillMaxSize(),
                                noteGridState = noteGridState
                            )
                        }
                        SearchViewModel.State.Empty -> {
                        }
                    }
                    SearchType.Memorial -> when (viewModel.memorialState) {
                        is SearchViewModel.State.Data -> {
                            val memorialColumnState by viewModel.memorialColumnState
                                .collectAsState()
                            SearchMemorialScreen(
                                modifier = Modifier.fillMaxSize(),
                                onDeleteMemorial = viewModel::deleteMemorial,
                                memorialColumnState = memorialColumnState
                            )
                        }
                        SearchViewModel.State.Empty -> {
                        }
                    }
                    SearchType.Schedule -> when (viewModel.scheduleState) {
                        is SearchViewModel.State.Data -> {
                            val scheduleColumnState by viewModel.scheduleColumnState
                                .collectAsState()
                            SearchScheduleScreen(
                                modifier = Modifier.fillMaxSize(),
                                scheduleColumnState = scheduleColumnState,
                                deleteSchedule = viewModel::deleteSchedule,
                                addSchedule = viewModel::addSchedule,
                                modifySchedule = viewModel::updateSchedule
                            )
                        }
                        SearchViewModel.State.Empty -> {
                        }
                    }
                }
            }
        }
    )
}