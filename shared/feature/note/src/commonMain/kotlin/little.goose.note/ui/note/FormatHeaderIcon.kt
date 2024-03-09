@file:OptIn(ExperimentalResourceApi::class)

package little.goose.note.ui.note

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
import little.goose.note.util.FormatType
import little.goose.resource.GooseRes
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@Composable
fun FormatHeaderIcon(
    modifier: Modifier = Modifier,
    onHeaderClick: (FormatType.Header) -> Unit
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
            IconButton(
                onClick = { onHeaderClick(FormatType.Header.H1) }
            ) {
                Icon(
                    painter = painterResource(GooseRes.drawable.icon_format_h1),
                    contentDescription = "Header1"
                )
            }
            IconButton(
                onClick = { onHeaderClick(FormatType.Header.H2) }
            ) {
                Icon(
                    painter = painterResource(GooseRes.drawable.icon_format_h2),
                    contentDescription = "Header2"
                )
            }
            IconButton(
                onClick = { onHeaderClick(FormatType.Header.H3) }
            ) {
                Icon(
                    painter = painterResource(GooseRes.drawable.icon_format_h3),
                    contentDescription = "Header3"
                )
            }
            IconButton(
                onClick = { onHeaderClick(FormatType.Header.H4) }
            ) {
                Icon(
                    painter = painterResource(GooseRes.drawable.icon_format_h4),
                    contentDescription = "Header4"
                )
            }
            IconButton(
                onClick = { onHeaderClick(FormatType.Header.H5) }
            ) {
                Icon(
                    painter = painterResource(GooseRes.drawable.icon_format_h5),
                    contentDescription = "Header5"
                )
            }
            IconButton(
                onClick = { onHeaderClick(FormatType.Header.H6) }
            ) {
                Icon(
                    painter = painterResource(GooseRes.drawable.icon_format_h6),
                    contentDescription = "Header6"
                )
            }
        }
    }
}