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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun SearchScreen(
    modifier: Modifier,
    onBack: () -> Unit
) {
    val viewModel = hiltViewModel<SearchViewModel>()
    var keyword by remember { mutableStateOf("") }
    Scaffold(
        modifier = modifier,
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
                        onValueChange = {
                            keyword = it
                            viewModel.search(it)
                        },
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
                    SearchType.Transaction -> when (val state = viewModel.transactionState) {
                        is SearchViewModel.State.Data -> {
                            val transactions by state.items.collectAsState()
                            SearchTransactionScreen(
                                modifier = Modifier.fillMaxSize(),
                                transactions = transactions,
                                onDeleteTransaction = viewModel::deleteTransaction
                            )
                        }
                        SearchViewModel.State.Empty -> {
                        }
                    }
                    SearchType.Note -> when (val state = viewModel.noteState) {
                        is SearchViewModel.State.Data -> {
                            val notes by state.items.collectAsState()
                            SearchNoteScreen(
                                modifier = Modifier.fillMaxSize(),
                                notes = notes
                            )
                        }
                        SearchViewModel.State.Empty -> {
                        }
                    }
                    SearchType.Memorial -> when (val state = viewModel.memorialState) {
                        is SearchViewModel.State.Data -> {
                            val memorials by state.items.collectAsState()
                            SearchMemorialScreen(
                                modifier = Modifier.fillMaxSize(),
                                memorials = memorials,
                                onDeleteMemorial = viewModel::deleteMemorial
                            )
                        }
                        SearchViewModel.State.Empty -> {
                        }
                    }
                    SearchType.Schedule -> when (val state = viewModel.scheduleState) {
                        is SearchViewModel.State.Data -> {
                            val schedules by state.items.collectAsState()
                            SearchScheduleScreen(
                                modifier = Modifier.fillMaxSize(),
                                schedules = schedules
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