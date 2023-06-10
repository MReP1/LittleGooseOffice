package little.goose.memorial.ui

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.composable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import little.goose.design.system.component.MovableActionButton
import little.goose.design.system.component.MovableActionButtonState
import little.goose.memorial.R
import little.goose.memorial.data.constants.KEY_MEMORIAL_ID
import little.goose.memorial.data.entities.Memorial
import little.goose.memorial.ui.component.MemorialCard

const val ROUTE_MEMORIAL_SHOW = "memorial_show"

fun NavController.navigateToMemorialShow(memorialId: Long) {
    navigate("$ROUTE_MEMORIAL_SHOW/$KEY_MEMORIAL_ID=$memorialId") {
        launchSingleTop = true
        restoreState = true
    }
}

internal fun NavGraphBuilder.memorialShowRoute(
    onBack: () -> Unit,
    onNavigateToMemorial: (Long) -> Unit,
) {
    composable(
        route = "$ROUTE_MEMORIAL_SHOW/$KEY_MEMORIAL_ID={$KEY_MEMORIAL_ID}",
        arguments = listOf(
            navArgument(KEY_MEMORIAL_ID) {
                type = NavType.LongType
            }
        )
    ) {
        MemorialShowRoute(
            modifier = Modifier.fillMaxSize(),
            onBack = onBack,
            onEditClick = onNavigateToMemorial
        )
    }
}

@Composable
private fun MemorialShowRoute(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onEditClick: (Long) -> Unit
) {
    val viewModel: MemorialShowViewModel = hiltViewModel()
    val memorial by viewModel.memorial.collectAsState()
    MemorialShowScreen(
        modifier = modifier.fillMaxSize(),
        memorial = memorial,
        onBack = onBack,
        onEditClick = onEditClick
    )
}

@Composable
private fun MemorialShowScreen(
    modifier: Modifier = Modifier,
    memorial: Memorial,
    onBack: () -> Unit,
    onEditClick: (Long) -> Unit,
) {
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
                    Text(text = stringResource(id = R.string.memorial))
                }
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            val state = remember { MovableActionButtonState() }
            val scope = rememberCoroutineScope()
            val context = LocalContext.current

            MemorialCard(
                memorial = memorial,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .width(280.dp)
                    .align(Alignment.Center)
            )

            MovableActionButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd),
                state = state,
                mainButtonContent = { isExpended ->
                    AnimatedContent(
                        targetState = isExpended,
                        transitionSpec = {
                            fadeIn() + expandIn() with shrinkOut() + fadeOut()
                        }
                    ) { currentExpended ->
                        val icon = if (!currentExpended) Icons.Rounded.Add else Icons.Rounded.Edit
                        Icon(
                            imageVector = icon,
                            contentDescription = if (!currentExpended) "expand" else "edit"
                        )
                    }
                },
                onMainButtonClick = {
                    memorial.id?.let { id -> onEditClick(id) }
                    scope.launch(Dispatchers.Main.immediate) {
                        state.fold()
                    }
                },
                topSubButtonContent = {
                    Icon(imageVector = Icons.Rounded.Image, contentDescription = "image")
                },
                onTopSubButtonClick = {
                    Toast.makeText(context, "TODO: 修改背景", Toast.LENGTH_SHORT).show()
                },
                bottomSubButtonContent = {

                },
                onBottomSubButtonClick = {
                    scope.launch(Dispatchers.Main.immediate) {
                        state.fold()
                    }
                }
            )
        }
    }
}