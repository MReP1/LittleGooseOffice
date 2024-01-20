package little.goose.home.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import little.goose.home.data.HomePage
import little.goose.home.ui.component.IndexTopBar
import little.goose.home.ui.index.IndexTopBarState
import little.goose.settings.R

@Composable
internal fun HomeTopBar(
    modifier: Modifier = Modifier,
    currentHomePage: HomePage,
    indexTopBarState: IndexTopBarState,
    onNavigateToSettings: () -> Unit
) {
    if (currentHomePage != HomePage.Home) {
        CenterAlignedTopAppBar(
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            title = {
                Text(text = stringResource(id = currentHomePage.labelRes))
            },
            actions = {
                IconButton(onClick = onNavigateToSettings) {
                    Icon(
                        imageVector = Icons.Rounded.Settings,
                        contentDescription = stringResource(
                            id = R.string.settings
                        )
                    )
                }
            }
        )
    } else {
        IndexTopBar(
            modifier = modifier.fillMaxWidth(),
            state = indexTopBarState
        )
    }
}