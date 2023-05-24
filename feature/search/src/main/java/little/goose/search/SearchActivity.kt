package little.goose.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import little.goose.design.system.theme.AccountTheme
import little.goose.search.memorial.SearchMemorialRoute
import little.goose.search.note.SearchNoteRoute
import little.goose.search.schedule.SearchScheduleRoute
import little.goose.search.transaction.SearchTransactionRoute

@AndroidEntryPoint
class SearchActivity : ComponentActivity() {

    companion object {
        fun open(context: Context, searchType: SearchType) {
            context.startActivity(
                Intent(context, SearchActivity::class.java)
                    .apply { putExtra(SearchType.KEY_SEARCH_TYPE, searchType.value) }
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val typeValue = intent.getIntExtra(SearchType.KEY_SEARCH_TYPE, 0)
        val type = SearchType.fromValue(typeValue)
        setContent {
            AccountTheme {
                SearchScreen(
                    modifier = Modifier.fillMaxSize(),
                    searchType = type,
                    onBack = ::finish
                )
            }
        }
    }

}

@Composable
private fun SearchScreen(
    modifier: Modifier,
    searchType: SearchType,
    onBack: () -> Unit
) {
    when (searchType) {
        SearchType.Memorial -> SearchMemorialRoute(
            modifier = modifier.fillMaxSize(),
            onBack = onBack
        )

        SearchType.Note -> SearchNoteRoute(
            modifier = modifier.fillMaxSize(),
            onBack = onBack
        )

        SearchType.Schedule -> SearchScheduleRoute(
            modifier = modifier,
            onBack = onBack
        )

        SearchType.Transaction -> SearchTransactionRoute(
            modifier = modifier,
            onBack = onBack
        )
    }
}