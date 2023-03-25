package little.goose.memorial.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import little.goose.common.utils.parcelable
import little.goose.design.system.component.MovableActionButton
import little.goose.design.system.component.MovableActionButtonState
import little.goose.design.system.theme.AccountTheme
import little.goose.memorial.R
import little.goose.memorial.data.constants.KEY_MEMORIAL
import little.goose.memorial.data.entities.Memorial
import little.goose.memorial.ui.component.MemorialCard

@AndroidEntryPoint
class MemorialShowActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AccountTheme {
                MemorialShowRoute()
            }
        }
    }

    companion object {
        fun open(context: Context, memorial: Memorial) {
            val intent = Intent(context, MemorialShowActivity::class.java).apply {
                putExtra(KEY_MEMORIAL, memorial)
            }
            context.startActivity(intent)
        }
    }

}

@Composable
private fun MemorialShowRoute() {
    val viewModel: MemorialShowViewModel = hiltViewModel()
    val context = LocalContext.current
    val memorial by viewModel.memorial.collectAsState()
    val register = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val mem = result.data?.parcelable<Memorial>(KEY_MEMORIAL)
                ?: return@rememberLauncherForActivityResult
            viewModel.updateMemorial(mem)
        }
    )
    MemorialShowScreen(
        modifier = Modifier.fillMaxSize(),
        memorial = memorial,
        onBack = (context as Activity)::finish,
        onEditClick = remember {
            { register.launch(MemorialActivity.getEditIntent(context, it)) }
        }
    )
}

@Composable
private fun MemorialShowScreen(
    modifier: Modifier = Modifier,
    memorial: Memorial,
    onBack: () -> Unit,
    onEditClick: (Memorial) -> Unit,
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
                    onEditClick(memorial)
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