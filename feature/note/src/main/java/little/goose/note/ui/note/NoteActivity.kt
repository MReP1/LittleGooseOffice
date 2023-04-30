package little.goose.note.ui.note

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import little.goose.design.system.theme.AccountTheme
import little.goose.note.data.constants.KEY_NOTE
import little.goose.note.data.entities.Note

@AndroidEntryPoint
class NoteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AccountTheme {
                NoteRoute(
                    modifier = Modifier.fillMaxSize(),
                    onBack = ::finish
                )
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
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    val viewModel = hiltViewModel<NoteViewModel>()
    val state by viewModel.noteScreenState.collectAsState()
    NoteScreen(
        modifier = modifier,
        state = state,
        onBack = onBack
    )
}