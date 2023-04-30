package little.goose.note.ui.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import little.goose.note.R

data class FormatHeaderIconState(
    val onH1Click: () -> Unit = {},
    val onH2Click: () -> Unit = {},
    val onH3Click: () -> Unit = {},
    val onH4Click: () -> Unit = {},
    val onH5Click: () -> Unit = {},
    val onH6Click: () -> Unit = {}
)

@Composable
fun FormatHeaderIcon(
    modifier: Modifier = Modifier,
    state: FormatHeaderIconState = remember { FormatHeaderIconState() }
) {
    var isExpended by remember { mutableStateOf(false) }

    Row(modifier = modifier.animateContentSize()) {

        TextButton(
            onClick = { isExpended = !isExpended }
        ) {
            // TODO replace Round Icon
            Text(
                text = "H",
                style = MaterialTheme.typography.headlineSmall
            )
        }

        if (isExpended) {
            IconButton(onClick = state.onH1Click) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_format_h1),
                    contentDescription = "Header1"
                )
            }
            IconButton(onClick = state.onH2Click) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_format_h2),
                    contentDescription = "Header2"
                )
            }
            IconButton(onClick = state.onH3Click) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_format_h3),
                    contentDescription = "Header3"
                )
            }
            IconButton(onClick = state.onH4Click) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_format_h4),
                    contentDescription = "Header4"
                )
            }
            IconButton(onClick = state.onH5Click) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_format_h5),
                    contentDescription = "Header5"
                )
            }
            IconButton(onClick = state.onH6Click) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_format_h6),
                    contentDescription = "Header6"
                )
            }
        }
    }
}