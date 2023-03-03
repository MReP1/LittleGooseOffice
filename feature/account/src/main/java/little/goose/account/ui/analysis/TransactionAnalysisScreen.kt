package little.goose.account.ui.analysis

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun TransactionAnalysisScreen(
    modifier: Modifier = Modifier
) {
    val viewModel = hiltViewModel<TransactionAnalysisViewModel>()
    val bottomBarState by viewModel.bottomBarState.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = { Text(text = "统计") },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "back"
                    )
                }
            )
        },
        bottomBar = {
            TransactionAnalysisBottomBar(
                modifier = Modifier.wrapContentHeight(),
                state = bottomBarState
            )
        },
        content = {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {

            }
        }
    )
}