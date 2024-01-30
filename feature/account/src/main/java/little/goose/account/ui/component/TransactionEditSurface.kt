package little.goose.account.ui.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imeAnimationTarget
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text2.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Adb
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import little.goose.account.ui.transaction.TransactionScreenIntent
import little.goose.design.system.util.Display
import little.goose.design.system.util.Icon
import java.util.Date

@Stable
internal data class TransactionEditSurfaceState(
    val money: String = "",
    val content: String = "",
    val icon: Icon = Icon.Vector(icon = Icons.Rounded.Adb),
    val time: Date = Date(),
    val isContainOperator: Boolean = false,
    val isEditDescription: Boolean = false,
    val descriptionTextFieldState: TextFieldState = TextFieldState("")
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun TransactionEditSurface(
    modifier: Modifier = Modifier,
    state: TransactionEditSurfaceState,
    onTransactionChange: (TransactionScreenIntent.ChangeTransaction) -> Unit,
    onOperationIntent: (TransactionScreenIntent.TransactionOperation) -> Unit,
    onIsDescriptionEditChange: (TransactionScreenIntent.ChangeIsEditDescription) -> Unit
) {

    Column(
        modifier = modifier.animateContentSize(
            animationSpec = tween(200)
        )
    ) {

        val iconAndContent = remember(state.icon, state.content) {
            IconAndContent(state.icon, state.content)
        }
        TransactionContentItem(
            modifier = Modifier.fillMaxWidth(),
            iconAndContent = iconAndContent,
            money = state.money
        )

        TransactionContentEditBar(
            isDescriptionEdit = state.isEditDescription,
            time = state.time,
            onIsDescriptionEditChange = {
                onIsDescriptionEditChange(TransactionScreenIntent.ChangeIsEditDescription(it))
            },
            onTransactionChange = onTransactionChange,
            descriptionTextFieldState = state.descriptionTextFieldState
        )

        val density = LocalDensity.current
        Calculator(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (WindowInsets.isImeVisible && state.isEditDescription) {
                    with(density) {
                        val bottom = WindowInsets.imeAnimationTarget
                            .exclude(WindowInsets.navigationBars)
                            .getBottom(density)
                            .toDp()
                        if (bottom > 0.dp) bottom else 288.dp
                    }
                } else 288.dp),
            onNumClick = {
                onOperationIntent(TransactionScreenIntent.TransactionOperation.AppendEnd(it.digitToChar()))
            },
            onAgainClick = {
                onOperationIntent(TransactionScreenIntent.TransactionOperation.Again)
            },
            onDoneClick = {
                onOperationIntent(TransactionScreenIntent.TransactionOperation.Done)
            },
            onOperatorClick = {
                onOperationIntent(TransactionScreenIntent.TransactionOperation.ModifyOther(it))
            },
            isContainOperator = state.isContainOperator
        )

    }
}

private data class IconAndContent(
    val icon: Icon,
    val content: String
)

@Composable
private fun TransactionContentItem(
    modifier: Modifier = Modifier,
    iconAndContent: IconAndContent,
    money: String
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(start = 16.dp, end = 24.dp, top = 12.dp, bottom = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedContent(
                targetState = iconAndContent,
                transitionSpec = {
                    val inDurationMillis = 180
                    val outDurationMillis = 160
                    fadeIn(
                        animationSpec = tween(
                            durationMillis = inDurationMillis,
                            delayMillis = 36,
                            easing = LinearOutSlowInEasing
                        )
                    ) + slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Down,
                        animationSpec = tween(
                            durationMillis = inDurationMillis,
                            delayMillis = 36,
                            easing = LinearOutSlowInEasing
                        ),
                        initialOffset = { it / 2 }
                    ) togetherWith fadeOut(
                        animationSpec = tween(outDurationMillis, easing = LinearOutSlowInEasing)
                    ) + slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Down,
                        animationSpec = tween(outDurationMillis, easing = LinearOutSlowInEasing),
                        targetOffset = { it / 2 }
                    )
                },
                label = "transaction content item"
            ) { iac ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    iac.icon.Display(
                        modifier = Modifier.size(32.dp),
                        contentDescription = iac.content
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = iac.content)
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(text = money, style = MaterialTheme.typography.titleLarge)
        }
    }
}

@Preview(device = "spec:width=380dp,height=480dp,dpi=440")
@Composable
private fun PreviewTransactionEditSurface() {
    TransactionEditSurface(
        state = TransactionEditSurfaceState(),
        onOperationIntent = {},
        onTransactionChange = {},
        onIsDescriptionEditChange = {}
    )
}