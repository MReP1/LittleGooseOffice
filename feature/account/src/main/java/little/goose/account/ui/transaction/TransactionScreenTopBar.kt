package little.goose.account.ui.transaction

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import little.goose.account.R
import little.goose.account.data.models.IconDisplayType

@Composable
internal fun TransactionScreenTopBar(
    modifier: Modifier,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    onBack: () -> Unit,
    iconDisplayType: IconDisplayType,
    onIconDisplayTypeChange: (IconDisplayType) -> Unit,
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        title = {
            TransactionScreenTabRow(
                modifier = Modifier.width(120.dp),
                selectedTabIndex = selectedTabIndex,
                onTabSelected = onTabSelected
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = stringResource(id = R.string.back)
                )
            }
        },
        actions = {
            var expanded by remember { mutableStateOf(false) }
            IconButton(onClick = { expanded = true }) {
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
                                onIconDisplayTypeChange(type)
                            }
                        )
                    }
                }
            }
        }
    )
}

@Composable
private fun TransactionScreenTabRow(
    modifier: Modifier,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
) {
    TabRow(
        modifier = modifier,
        selectedTabIndex = selectedTabIndex,
        divider = {},
        indicator = { tabPositions ->
            val currentTabPosition = tabPositions[selectedTabIndex]
            val indicatorWidth = 16.dp
            val indicatorOffset by animateDpAsState(
                targetValue = currentTabPosition.right - (currentTabPosition.width / 2) - (indicatorWidth / 2),
                animationSpec = tween(
                    durationMillis = 250,
                    easing = FastOutSlowInEasing
                ),
                label = "indicator offset"
            )
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