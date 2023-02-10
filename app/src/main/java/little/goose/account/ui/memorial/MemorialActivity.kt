package little.goose.account.ui.memorial

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import little.goose.account.R
import little.goose.account.common.dialog.InputTextDialogFragment
import little.goose.account.common.dialog.time.DateTimePickerBottomDialog
import little.goose.account.common.dialog.time.TimeType
import little.goose.account.logic.data.constant.KEY_MEMORIAL
import little.goose.account.logic.data.constant.KEY_TYPE
import little.goose.account.logic.data.constant.TYPE_ADD
import little.goose.account.logic.data.constant.TYPE_MODIFY
import little.goose.account.logic.data.entities.Memorial
import little.goose.account.ui.base.BaseActivity
import little.goose.account.ui.memorial.widget.MemorialText
import little.goose.account.utils.appendTimeSuffix
import little.goose.account.utils.toChineseYearMonDayWeek
import little.goose.design.system.theme.AccountTheme
import java.util.*

@AndroidEntryPoint
class MemorialActivity : BaseActivity() {

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
    MemorialScreen(
        modifier = modifier,
        memorial = memorial,
        onChangeTimeClick = {
            DateTimePickerBottomDialog.Builder()
                .setDimVisibility(true)
                .setTime(viewModel.memorial.value.time)
                .setType(TimeType.DATE)
                .setConfirmAction {
                    viewModel.updateMemorial(memorial.copy(time = it))
                }.showNow(context.supportFragmentManager)
        },
        onContentClick = {
            InputTextDialogFragment.Builder()
                .setInputText(memorial.content)
                .setConfirmCallback { content ->
                    viewModel.updateMemorial(memorial.copy(content = content))
                }.showNow(context.supportFragmentManager)
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