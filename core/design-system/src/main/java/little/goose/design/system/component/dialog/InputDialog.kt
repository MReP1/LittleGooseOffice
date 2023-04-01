package little.goose.design.system.component.dialog

import android.view.ViewTreeObserver.OnWindowFocusChangeListener
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun InputDialog(
    state: BottomSheetDialogState,
    text: String,
    onConfirm: (String) -> Unit
) {
    var softwareKeyboardController: SoftwareKeyboardController? = null
    val scope = rememberCoroutineScope()
    fun dismissDialog() {
        softwareKeyboardController?.hide()
        scope.launch { state.close() }
    }
    BottomSheetDialog(
        state = state,
        onDismissRequest = ::dismissDialog
    ) {
        softwareKeyboardController = LocalSoftwareKeyboardController.current
        DisposableEffect(Unit) {
            onDispose { softwareKeyboardController = null }
        }
        InputDialogScreen(
            modifier = Modifier.fillMaxWidth(),
            text = text,
            onConfirm = {
                onConfirm(it)
                dismissDialog()
            },
            onCancel = ::dismissDialog
        )
    }
}


@Composable
private fun InputDialogScreen(
    modifier: Modifier = Modifier,
    text: String,
    onConfirm: (String) -> Unit,
    onCancel: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    Surface(modifier = modifier) {
        Column(modifier = Modifier.fillMaxWidth()) {
            var content by remember(text) {
                mutableStateOf(
                    TextFieldValue(
                        text = text,
                        selection = TextRange(text.length)
                    )
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onCancel) {
                    Text(text = stringResource(id = little.goose.design.system.R.string.cancel))
                }
                TextButton(onClick = {
                    onConfirm(content.text)
                }) {
                    Text(text = stringResource(id = little.goose.design.system.R.string.confirm))
                }
            }
            TextField(
                value = content,
                onValueChange = { content = it },
                shape = RectangleShape,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                maxLines = 1
            )
        }
    }
    val localView = LocalView.current
    DisposableEffect(localView) {
        val listener = OnWindowFocusChangeListener { hasFocus ->
            if (hasFocus) {
                focusRequester.requestFocus()
            }
        }
        localView.viewTreeObserver.addOnWindowFocusChangeListener(listener)
        onDispose {
            localView.viewTreeObserver.removeOnWindowFocusChangeListener(listener)
        }
    }
}