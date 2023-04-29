package little.goose.note.ui.note

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import little.goose.design.system.theme.AccountTheme
import little.goose.note.R
import little.goose.note.data.constants.KEY_NOTE
import little.goose.note.data.entities.Note

@AndroidEntryPoint
class NoteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AccountTheme {
                NoteRoute()
            }
        }
    }


    companion object {
        fun openAdd(context: Context) {
            val intent = Intent(context, NoteActivity::class.java)
            context.startActivity(intent)
        }

        fun openEdit(context: Context, note: Note) {
            val intent = Intent(context, NoteActivity::class.java).apply {
                putExtra(KEY_NOTE, note)
            }
            context.startActivity(intent)
        }
    }

}

@Composable
fun NoteRoute(
    modifier: Modifier = Modifier
) {
    val viewModel = hiltViewModel<NoteViewModel>()
    val state by viewModel.noteScreenState.collectAsState()
    NoteScreen(modifier = modifier, state = state)
}

data class NoteScreenState(
    val note: Note = Note(),
    val onTitleChange: (String) -> Unit = {},
    val onContentChange: (String) -> Unit = {}
)

@Composable
fun NoteScreen(
    modifier: Modifier = Modifier,
    state: NoteScreenState
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = {
                    Text(text = stringResource(id = R.string.notebook))
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.fillMaxWidth(),
                content = {
                    // TODO ICON
                }
            )
        }
    ) {
        Column(
            modifier = Modifier.padding(it)
        ) {
            TextField(
                value = state.note.title,
                onValueChange = state.onTitleChange
            )
            TextField(
                value = state.note.content,
                onValueChange = state.onContentChange
            )
        }
    }
}