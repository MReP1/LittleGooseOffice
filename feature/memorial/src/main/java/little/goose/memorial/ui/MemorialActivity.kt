package little.goose.memorial.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import little.goose.common.constants.KEY_TYPE
import little.goose.common.constants.TYPE_ADD
import little.goose.common.constants.TYPE_MODIFY
import little.goose.common.dialog.time.TimeType
import little.goose.common.utils.toChineseYearMonDayWeek
import little.goose.design.system.component.dialog.InputDialog
import little.goose.design.system.component.dialog.TimeSelectorCenterDialog
import little.goose.design.system.component.dialog.rememberBottomSheetDialogState
import little.goose.design.system.component.dialog.rememberDialogState
import little.goose.design.system.theme.AccountTheme
import little.goose.memorial.R
import little.goose.memorial.data.constants.KEY_MEMORIAL
import little.goose.memorial.data.entities.Memorial
import little.goose.memorial.ui.component.MemorialText
import little.goose.memorial.utils.appendTimeSuffix
import java.util.*

@AndroidEntryPoint
class MemorialActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AccountTheme {
                MemorialRoute(
                    modifier = Modifier.fillMaxSize(),
                    onBack = ::finish
                )
            }
        }
    }

    companion object {

        fun getEditIntent(context: Context, memorial: Memorial): Intent {
            return Intent(context, MemorialActivity::class.java).apply {
                putExtra(KEY_TYPE, TYPE_MODIFY)
                putExtra(KEY_MEMORIAL, memorial)
            }
        }

        fun openAdd(context: Context) {
            val intent = Intent(context, MemorialActivity::class.java).apply {
                putExtra(KEY_TYPE, TYPE_ADD)
            }
            context.startActivity(intent)
        }
    }

}

@Composable
private fun MemorialRoute(
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    val viewModel: MemorialActivityViewModel = hiltViewModel()
    val memorial by viewModel.memorial.collectAsState()
    val context = LocalContext.current as FragmentActivity
    val timeSelectorDialogState = rememberDialogState()
    val inputTextDialogState = rememberBottomSheetDialogState()
    val scope = rememberCoroutineScope()

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
            context.setResult(
                71,
                Intent().putExtra(KEY_MEMORIAL, viewModel.memorial.value)
            )
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
                            painter = painterResource(id = R.drawable.icon_back),
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