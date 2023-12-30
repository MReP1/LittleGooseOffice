package little.goose.settings

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowRight
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import little.goose.design.system.theme.GooseTheme
import little.goose.design.system.theme.ThemeType

sealed interface SettingsState {
    data object Loading : SettingsState
    data class Success(
        val isDynamicColor: Boolean,
        val onDynamicColorChange: (Boolean) -> Unit,
        val themeType: ThemeType,
        val onThemeTypeChange: (ThemeType) -> Unit
    ) : SettingsState
}

@Composable
internal fun SettingsScreen(
    modifier: Modifier = Modifier,
    settingsState: SettingsState,
    onBack: () -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack, contentDescription = "Back"
                        )
                    }
                },
                title = {
                    Text(text = stringResource(id = R.string.settings))
                }
            )
        },
        content = { paddingValues ->
            val context = LocalContext.current
            val clipboardManager = LocalClipboardManager.current

            val showToast: (String) -> Unit = remember(context) {
                { message ->
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                item {
                    var clickedTime by remember { mutableStateOf(6) }
                    ListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (--clickedTime == 0) {
                                    throw Exception("Boom!")
                                } else {
                                    showToast(
                                        context.getString(R.string.click_to_crash, clickedTime)
                                    )
                                }
                            },
                        overlineContent = {
                            Text(text = stringResource(id = R.string.developer))
                        },
                        headlineContent = {
                            Text(text = "米奇律师")
                        }
                    )
                }
                item {
                    val openSourceProjectLink = remember {
                        "https://github.com/MReP1/LittleGooseOffice"
                    }
                    ListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .combinedClickable(
                                onClick = {
                                    showToast(context.getString(R.string.long_click_to_copy))
                                },
                                onLongClick = {
                                    clipboardManager.setText(
                                        annotatedString = buildAnnotatedString {
                                            append(openSourceProjectLink)
                                        }
                                    )
                                    showToast(context.getString(R.string.copied))
                                }
                            ),
                        overlineContent = {
                            Text(text = stringResource(id = R.string.open_source_link))
                        },
                        headlineContent = {
                            Text(text = openSourceProjectLink)
                        }
                    )
                }
                item {
                    val email = remember {
                        "empirx5530@gmail.com"
                    }
                    ListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .combinedClickable(
                                onClick = {
                                    runCatching {
                                        val intent = Intent(Intent.ACTION_SEND).apply {
                                            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                                            putExtra(
                                                Intent.EXTRA_SUBJECT,
                                                "For Little Goose Office: "
                                            )
                                            type = "text/plain"
                                        }
                                        val sendIntent = Intent.createChooser(intent, null)
                                        context.startActivity(sendIntent)
                                    }.onFailure {
                                        showToast(context.getString(R.string.no_email_app))
                                    }
                                },
                                onLongClick = {
                                    clipboardManager.setText(
                                        annotatedString = buildAnnotatedString { append(email) }
                                    )
                                    showToast(context.getString(R.string.copied))
                                }
                            ),
                        overlineContent = {
                            Text(text = stringResource(id = R.string.contact_way))
                        },
                        headlineContent = {
                            Text(text = "empirx5530@gmail.com")
                        }
                    )
                }
                item {
                    var isExpanded by remember { mutableStateOf(false) }
                    ListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                isExpanded = !isExpanded
                            },
                        headlineContent = {
                            Text(text = stringResource(id = R.string.configure_theme))
                        },
                        trailingContent = {
                            Box(
                                contentAlignment = Alignment.TopCenter
                            ) {
                                val rotation by animateFloatAsState(
                                    targetValue = if (isExpanded) 180f else 0f,
                                    label = "Arrow Rotation"
                                )
                                Icon(
                                    modifier = Modifier.rotate(rotation),
                                    imageVector = Icons.Rounded.ArrowRight,
                                    contentDescription = "More"
                                )
                            }
                        },
                        supportingContent = {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .animateContentSize()
                            ) {
                                if (isExpanded && settingsState is SettingsState.Success) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .clickable {
                                                settingsState.onDynamicColorChange(
                                                    !settingsState.isDynamicColor
                                                )
                                            }
                                            .padding(horizontal = 8.dp)
                                    ) {
                                        Switch(
                                            checked = settingsState.isDynamicColor,
                                            onCheckedChange = settingsState.onDynamicColorChange
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = stringResource(id = R.string.dynamic_color))
                                    }

                                    val changeThemeType: (ThemeType) -> Unit = {
                                        if (settingsState.themeType != it) settingsState.onThemeTypeChange(
                                            it
                                        )
                                    }
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.clickable {
                                            changeThemeType(ThemeType.FOLLOW_SYSTEM)
                                        }
                                    ) {
                                        Checkbox(
                                            checked = settingsState.themeType == ThemeType.FOLLOW_SYSTEM,
                                            onCheckedChange = {
                                                changeThemeType(ThemeType.FOLLOW_SYSTEM)
                                            }
                                        )
                                        Text(text = stringResource(id = R.string.follow_system))
                                        Spacer(modifier = Modifier.width(8.dp))
                                    }
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.clickable {
                                            changeThemeType(ThemeType.LIGHT)
                                        }
                                    ) {
                                        Checkbox(
                                            checked = settingsState.themeType == ThemeType.LIGHT,
                                            onCheckedChange = { changeThemeType(ThemeType.LIGHT) }
                                        )
                                        Text(text = stringResource(id = R.string.light_mode))
                                        Spacer(modifier = Modifier.width(8.dp))
                                    }
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.clickable {
                                            changeThemeType(ThemeType.DART)
                                        }
                                    ) {
                                        Checkbox(
                                            checked = settingsState.themeType == ThemeType.DART,
                                            onCheckedChange = { changeThemeType(ThemeType.DART) }
                                        )
                                        Text(text = stringResource(id = R.string.dart_mode))
                                        Spacer(modifier = Modifier.width(8.dp))
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    )
}

@Preview
@Composable
private fun PreviewSettingScreen() = GooseTheme {
    SettingsScreen(settingsState = SettingsState.Success(
        isDynamicColor = true,
        themeType = ThemeType.FOLLOW_SYSTEM,
        onDynamicColorChange = {},
        onThemeTypeChange = {}
    ), onBack = {})
}