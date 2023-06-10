package little.goose.memorial.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.composable
import kotlinx.coroutines.launch
import little.goose.common.constants.KEY_TYPE
import little.goose.common.utils.TimeType
import little.goose.common.utils.toChineseYearMonDayWeek
import little.goose.design.system.component.dialog.InputDialog
import little.goose.design.system.component.dialog.TimeSelectorCenterDialog
import little.goose.design.system.component.dialog.rememberBottomSheetDialogState
import little.goose.design.system.component.dialog.rememberDialogState
import little.goose.memorial.R
import little.goose.memorial.data.constants.KEY_MEMORIAL_ID
import little.goose.memorial.data.entities.Memorial
import little.goose.memorial.ui.component.MemorialText
import little.goose.memorial.utils.appendTimeSuffix
import little.goose.ui.screen.LittleGooseLoadingScreen
import java.util.Date

const val ROUTE_MEMORIAL = "memorial"

fun NavController.navigateToMemorial(
    type: MemorialScreenType,
    memorialId: Long? = null
) {
    navigate(
        route = ROUTE_MEMORIAL +
                "/$KEY_TYPE=$type" +
                if (memorialId != null) "?$KEY_MEMORIAL_ID=$memorialId" else ""
    ) {
        launchSingleTop = true
    }
}

internal class MemorialScreenArgs private constructor(
    val type: MemorialScreenType,
    val memorialId: Long? = null
) {
    internal constructor(savedStateHandle: SavedStateHandle) : this(
        type = MemorialScreenType.valueOf(savedStateHandle[KEY_TYPE]!!),
        memorialId = savedStateHandle.get<Long>(KEY_MEMORIAL_ID)?.takeIf { it > 0 }
    )
}

internal fun NavGraphBuilder.memorialRoute(
    onBack: () -> Unit
) = composable(
    route = ROUTE_MEMORIAL +
            "/$KEY_TYPE={$KEY_TYPE}" +
            "?$KEY_MEMORIAL_ID={$KEY_MEMORIAL_ID}",
    arguments = listOf(
        navArgument(KEY_TYPE) {
            type = NavType.StringType
        },
        navArgument(KEY_MEMORIAL_ID) {
            type = NavType.LongType
            defaultValue = 0
        }
    )
) {
    MemorialRoute(
        modifier = Modifier.fillMaxSize(),
        onBack = onBack
    )
}

sealed interface MemorialScreenState {
    object Loading : MemorialScreenState
    data class Success(val memorial: Memorial) : MemorialScreenState
}

@Composable
private fun MemorialRoute(
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    val viewModel: MemorialScreenViewModel = hiltViewModel()
    val timeSelectorDialogState = rememberDialogState()
    val inputTextDialogState = rememberBottomSheetDialogState()
    val scope = rememberCoroutineScope()

    val memorialScreenState by viewModel.memorialScreenState.collectAsState()
    when (val state = memorialScreenState) {
        MemorialScreenState.Loading -> LittleGooseLoadingScreen()
        is MemorialScreenState.Success -> {
            val memorial = state.memorial
            MemorialScreen(
                modifier = modifier,
                memorial = memorial,
                onChangeTimeClick = timeSelectorDialogState::show,
                onContentClick = {
                    scope.launch { inputTextDialogState.open() }
                },
                onTopCheckedChange = { isTop ->
                    viewModel.isChangeTop = true
                    viewModel.updateMemorial(memorial.copy(isTop = isTop))
                },
                onConfirmClick = {
                    viewModel.storeMemorial()
                    onBack()
                },
                onBack = onBack,
            )

            TimeSelectorCenterDialog(
                state = timeSelectorDialogState,
                initTime = memorial.time,
                type = TimeType.DATE,
                onConfirm = {
                    viewModel.updateMemorial(memorial = memorial.copy(time = it))
                }
            )

            InputDialog(
                state = inputTextDialogState,
                text = memorial.content,
                onConfirm = {
                    viewModel.updateMemorial(memorial = memorial.copy(content = it))
                }
            )
        }
    }
}

@Composable
private fun MemorialScreen(
    modifier: Modifier = Modifier,
    memorial: Memorial,
    onChangeTimeClick: () -> Unit,
    onContentClick: () -> Unit,
    onTopCheckedChange: (Boolean) -> Unit,
    onBack: () -> Unit,
    onConfirmClick: () -> Unit
) {
    val context = LocalContext.current
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(text = memorial.content.appendTimeSuffix(memorial.time, context))
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = ""
                        )
                    }
                }
            )
        },
        content = { paddingValues ->
            val state = rememberScrollState()
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(state),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                MemorialText(
                    memorial = memorial,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(24.dp)
                )

                Row(
                    modifier = Modifier
                        .height(54.dp)
                        .fillMaxWidth()
                        .clickable(onClick = onChangeTimeClick),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(32.dp))
                    Text(text = stringResource(id = R.string.date))
                    Spacer(modifier = Modifier.weight(1f))
                    Text(text = memorial.time.toChineseYearMonDayWeek(context))
                    Spacer(modifier = Modifier.width(32.dp))
                }

                Row(
                    modifier = Modifier
                        .height(54.dp)
                        .fillMaxWidth()
                        .clickable(onClick = onContentClick),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(32.dp))
                    Text(text = stringResource(id = R.string.content))
                    Spacer(modifier = Modifier.weight(1f))
                    Text(text = memorial.content)
                    Spacer(modifier = Modifier.width(32.dp))
                }

                Row(
                    modifier = Modifier
                        .height(54.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(32.dp))
                    Text(text = stringResource(id = R.string.to_top))
                    Spacer(modifier = Modifier.weight(1f))
                    Switch(
                        checked = memorial.isTop,
                        onCheckedChange = onTopCheckedChange
                    )
                    Spacer(modifier = Modifier.width(32.dp))
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onConfirmClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    shape = RectangleShape
                ) {
                    Text(text = stringResource(id = R.string.confirm))
                }
            }
        }
    )
}

@Preview
@Composable
private fun PreviewMemorialScreen() {
    MemorialScreen(
        memorial = Memorial(null, "纪念日", true, Date()),
        onChangeTimeClick = {},
        onContentClick = {},
        onBack = {},
        onTopCheckedChange = {},
        onConfirmClick = {}
    )
}