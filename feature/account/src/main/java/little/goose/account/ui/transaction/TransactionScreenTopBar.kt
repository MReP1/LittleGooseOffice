package little.goose.account.ui.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import little.goose.account.R
import little.goose.account.data.models.IconDisplayType
import little.goose.design.system.theme.GooseTheme

@Stable
internal data class TransactionScreenTopBarState(
    val iconDisplayType: IconDisplayType = IconDisplayType.ICON_CONTENT,
)

@Composable
internal fun TransactionScreenTopBar(
    modifier: Modifier,
    onBack: () -> Unit,
    title: @Composable () -> Unit,
    actions: @Composable RowScope.() -> Unit,
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        title = title,
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = stringResource(id = R.string.back)
                )
            }
        },
        actions = actions
    )
}

@Composable
internal fun TransactionScreenTopBarAction(
    modifier: Modifier = Modifier,
    iconDisplayType: IconDisplayType,
    onIconDisplayTypeChange: (TransactionScreenIntent.ChangeIconDisplayType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    IconButton(
        modifier = modifier,
        onClick = { expanded = true }
    ) {
        Icon(
            imageVector = iconDisplayType.icon,
            contentDescription = stringResource(id = iconDisplayType.textRes)
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            IconDisplayType.entries.forEach { type ->
                DropdownMenuItem(
                    text = {
                        Text(text = stringResource(id = type.textRes))
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = type.icon,
                            contentDescription = stringResource(id = type.textRes)
                        )
                    },
                    onClick = {
                        onIconDisplayTypeChange(
                            TransactionScreenIntent.ChangeIconDisplayType(type)
                        )
                    }
                )
            }
        }
    }
}

@Composable
internal fun TransactionScreenTabRow(
    modifier: Modifier,
    selectedTabIndex: Int,
    offsetFraction: Float,
    onTabSelected: (Int) -> Unit,
) {
    TabRow(
        modifier = modifier,
        selectedTabIndex = selectedTabIndex,
        divider = {},
        indicator = { tabPositions ->
            val currentTabPosition = tabPositions[selectedTabIndex]
            val indicatorWidth = 16.dp
            val indicatorOffset =
                (currentTabPosition.right - (currentTabPosition.width / 2)) * (1 + offsetFraction) - (indicatorWidth / 2)
            Spacer(
                Modifier
                    .wrapContentSize(Alignment.BottomStart)
                    .size(width = 16.dp, height = 3.dp)
                    .offset(x = indicatorOffset)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    ) {
        Tab(
            selected = selectedTabIndex == 0,
            onClick = { onTabSelected(0) },
            modifier = Modifier.height(42.dp)
        ) {
            Text(
                text = stringResource(id = R.string.expense),
                style = MaterialTheme.typography.titleMedium
            )
        }
        Tab(
            selected = selectedTabIndex == 1,
            onClick = { onTabSelected(1) },
            modifier = Modifier.height(42.dp)
        ) {
            Text(
                text = stringResource(id = R.string.income),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Preview
@Composable
private fun PreviewTransactionScreenTabRow() = GooseTheme {
    var currentSelectedTab by remember { mutableIntStateOf(0) }
    TransactionScreenTabRow(
        modifier = Modifier,
        selectedTabIndex = currentSelectedTab,
        offsetFraction = 0F,
        onTabSelected = { currentSelectedTab = it }
    )
}