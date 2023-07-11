package little.goose.memorial.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Preview
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.google.accompanist.navigation.animation.composable
import little.goose.common.constants.DEEP_LINK_THEME_AND_HOST
import little.goose.common.constants.KEY_TYPE
import little.goose.common.utils.TimeType
import little.goose.common.utils.toChineseYearMonDayWeek
import little.goose.design.system.component.dialog.TimeSelectorCenterDialog
import little.goose.design.system.component.dialog.rememberDialogState
import little.goose.memorial.R
import little.goose.memorial.ROUTE_GRAPH_MEMORIAL
import little.goose.memorial.data.constants.KEY_MEMORIAL_ID
import little.goose.memorial.data.entities.Memorial
import little.goose.memorial.ui.component.MemorialCard
import little.goose.memorial.ui.component.MemorialText
import little.goose.memorial.utils.appendTimeSuffix
import little.goose.ui.screen.LittleGooseLoadingScreen
import java.util.Date

const val ROUTE_MEMORIAL = "memorial"

const val FULL_ROUTE_MEMORIAL = ROUTE_MEMORIAL +
        "/$KEY_TYPE={$KEY_TYPE}" +
        "?$KEY_MEMORIAL_ID={$KEY_MEMORIAL_ID}"

private const val DEEP_LINK_URI_PATTERN_MEMORIAL =
    "$DEEP_LINK_THEME_AND_HOST/$ROUTE_GRAPH_MEMORIAL/$ROUTE_MEMORIAL" +
            "/$KEY_TYPE={$KEY_TYPE}" +
            "?$KEY_MEMORIAL_ID={$KEY_MEMORIAL_ID}"

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
    route = FULL_ROUTE_MEMORIAL,
    deepLinks = listOf(
        navDeepLink {
            uriPattern = DEEP_LINK_URI_PATTERN_MEMORIAL
        }
    ),
    arguments = listOf(
        navArgument(KEY_TYPE) {
            type = NavType.StringType
            defaultValue = MemorialScreenType.Add.toString()
        },
        navArgument(KEY_MEMORIAL_ID) {
            type = NavType.LongType
            defaultValue = 0
        }
    )
) {
    MemorialRoute(
        modifier = Modifier
            .fillMaxSize()
            .shadow(36.dp, clip = false),
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

    val memorialScreenState by viewModel.memorialScreenState.collectAsState()
    when (val state = memorialScreenState) {
        MemorialScreenState.Loading -> LittleGooseLoadingScreen()
        is MemorialScreenState.Success -> {
            val memorial = state.memorial
            MemorialScreen(
                modifier = modifier,
                memorial = memorial,
                onChangeTimeClick = timeSelectorDialogState::show,
                onContentChange = {
                    viewModel.updateMemorial(memorial = memorial.copy(content = it))
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
        }
    }
}

@Composable
private fun MemorialScreen(
    modifier: Modifier = Modifier,
    memorial: Memorial,
    onChangeTimeClick: () -> Unit,
    onContentChange: (String) -> Unit,
    onTopCheckedChange: (Boolean) -> Unit,
    onBack: () -> Unit,
    onConfirmClick: () -> Unit
) {
    val context = LocalContext.current
    var isPreview by remember { mutableStateOf(false) }
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    AnimatedVisibility(
                        visible = !isPreview,
                        enter = fadeIn(
                            animationSpec = tween(200, delayMillis = 60)
                        ) + slideInVertically(
                            animationSpec = tween(200, delayMillis = 60),
                            initialOffsetY = { it / 2 }
                        ),
                        exit = fadeOut(
                            animationSpec = tween(120)
                        ) + slideOutVertically(
                            animationSpec = tween(120),
                            targetOffsetY = { it / 2 }
                        ),
                        label = "Memorial Title"
                    ) {
                        Text(text = memorial.content.appendTimeSuffix(memorial.time, context))
                    }
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
            AnimatedContent(
                modifier = Modifier.padding(paddingValues),
                targetState = isPreview,
                label = "Memorial content",
                transitionSpec = {
                    fadeIn(
                        animationSpec = tween(durationMillis = 200, delayMillis = 60)
                    ) + slideInVertically(
                        animationSpec = tween(durationMillis = 200, delayMillis = 60),
                        initialOffsetY = { (if (targetState) -it else it) / 24 }
                    ) with fadeOut(
                        animationSpec = tween(durationMillis = 120)
                    ) + slideOutVertically(
                        animationSpec = tween(durationMillis = 120),
                        targetOffsetY = { (if (targetState) it else -it) / 24 }
                    )
                }
            ) { preview ->
                if (!preview) {
                    MemorialEditContent(
                        memorial = memorial,
                        onChangeTimeClick = onChangeTimeClick,
                        onContentChange = onContentChange,
                        onTopCheckedChange = onTopCheckedChange
                    )
                } else {
                    MemorialPreviewContent(
                        memorial = memorial
                    )
                }
            }
        },
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.fillMaxWidth(),
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = { isPreview = !isPreview }
                    ) {
                        Icon(imageVector = Icons.Rounded.Preview, contentDescription = "Preview")
                    }
                },
                actions = {
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = onConfirmClick) {
                        Icon(imageVector = Icons.Rounded.Save, contentDescription = "Save")
                    }
                }
            )
        }
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun MemorialEditContent(
    modifier: Modifier = Modifier,
    memorial: Memorial,
    onChangeTimeClick: () -> Unit,
    onContentChange: (String) -> Unit,
    onTopCheckedChange: (Boolean) -> Unit
) {
    val state = rememberScrollState()
    val context = LocalContext.current
    Column(
        modifier
            .fillMaxSize()
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

        val durationMillis = 200
        var isContentExpended by remember { mutableStateOf(false) }
        val contentHeight by animateDpAsState(
            targetValue = if (isContentExpended) 108.dp else 54.dp,
            animationSpec = tween(durationMillis),
            label = "content height"
        )
        val keyboard = LocalSoftwareKeyboardController.current
        DisposableEffect(isContentExpended) {
            if (!isContentExpended) {
                keyboard?.hide()
            }
            onDispose { }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(contentHeight)
                .clickable {
                    isContentExpended = !isContentExpended
                }
        ) {
            Row(
                modifier = Modifier
                    .height(54.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(32.dp))
                Text(text = stringResource(id = R.string.content))
                Spacer(modifier = Modifier.weight(1f))
                AnimatedVisibility(
                    visible = !isContentExpended,
                    enter = fadeIn(
                        animationSpec = tween(durationMillis)
                    ) + slideInVertically(
                        animationSpec = tween(durationMillis),
                        initialOffsetY = { it / 2 }
                    ),
                    exit = fadeOut(
                        animationSpec = tween(durationMillis)
                    ) + slideOutVertically(
                        animationSpec = tween(durationMillis),
                        targetOffsetY = { it / 2 }
                    )
                ) {
                    Text(text = memorial.content)
                }
                Spacer(modifier = Modifier.width(32.dp))
            }
            AnimatedVisibility(
                visible = isContentExpended,
                enter = fadeIn(
                    animationSpec = tween(durationMillis)
                ) + slideInVertically(
                    animationSpec = tween(durationMillis)
                ),
                exit = fadeOut(
                    animationSpec = tween(durationMillis)
                ) + slideOutVertically(
                    animationSpec = tween(durationMillis)
                )
            ) {
                var content by remember {
                    mutableStateOf(
                        TextFieldValue(
                            text = memorial.content,
                            selection = TextRange(start = 0, end = memorial.content.length)
                        )
                    )
                }

                val focusRequester = remember { FocusRequester() }
                DisposableEffect(focusRequester) {
                    focusRequester.requestFocus()
                    onDispose {}
                }

                DisposableEffect(memorial.content) {
                    if (content.text != memorial.content) {
                        content = content.copy(text = memorial.content)
                    }
                    onDispose { }
                }

                BasicTextField(
                    modifier = Modifier
                        .padding(
                            start = 32.dp, end = 32.dp,
                            top = 8.dp, bottom = 16.dp
                        )
                        .weight(1F)
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    value = content,
                    onValueChange = {
                        content = it
                        onContentChange(it.text)
                    },
                    maxLines = 1,
                    keyboardActions = KeyboardActions(
                        onDone = { isContentExpended = false }
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                )
            }
        }

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
    }
}

@Composable
private fun MemorialPreviewContent(
    modifier: Modifier = Modifier,
    memorial: Memorial,
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        MemorialCard(
            memorial = memorial,
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .width(280.dp)
                .align(Alignment.Center)
        )
    }
}

@Preview
@Composable
private fun PreviewMemorialScreen() {
    MemorialScreen(
        memorial = Memorial(null, "纪念日", true, Date()),
        onChangeTimeClick = {},
        onContentChange = {},
        onBack = {},
        onTopCheckedChange = {},
        onConfirmClick = {}
    )
}