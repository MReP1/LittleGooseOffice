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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import little.goose.account.data.entities.Transaction
import little.goose.account.logic.MoneyCalculator
import little.goose.account.ui.transaction.TransactionScreenIntent
import little.goose.account.ui.transaction.icon.TransactionIconHelper
import little.goose.design.system.util.Display
import java.math.BigDecimal

@Stable
internal data class TransactionEditSurfaceState(
    val transaction: Transaction = Transaction()
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun TransactionEditSurface(
    modifier: Modifier = Modifier,
    state: TransactionEditSurfaceState,
    onTransactionChangeIntent: (TransactionScreenIntent.ChangeTransaction) -> Unit,
    onOperationIntent: (TransactionScreenIntent.TransactionOperation) -> Unit,
) {
    val moneyCalculator = remember { MoneyCalculator(state.transaction.money) }

    DisposableEffect(state.transaction.money) {
        moneyCalculator.setMoney(state.transaction.money)
        onDispose { }
    }

    val isContainOperator by moneyCalculator.isContainOperator.collectAsState()
    val money by moneyCalculator.money.collectAsState()

    LaunchedEffect(moneyCalculator) {
        moneyCalculator.money.collect { moneyStr ->
            runCatching {
                BigDecimal(moneyStr)
            }.getOrNull()?.let { money ->
                onTransactionChangeIntent(
                    TransactionScreenIntent.ChangeTransaction.Money(money)
                )
            }
        }
    }

    Column(
        modifier = modifier.animateContentSize(
            animationSpec = tween(200)
        )
    ) {

        val iconAndContent = remember(state.transaction.icon_id, state.transaction.content) {
            IconAndContent(state.transaction.icon_id, state.transaction.content)
        }
        TransactionContentItem(
            modifier = Modifier.fillMaxWidth(),
            iconAndContent = iconAndContent,
            money = money
        )

        val (isDescriptionEdit, onIsDescriptionEditChange) = remember { mutableStateOf(false) }
        TransactionContentEditBar(
            transaction = state.transaction,
            isDescriptionEdit = isDescriptionEdit,
            onIsDescriptionEditChange = onIsDescriptionEditChange,
            onTransactionChange = onTransactionChangeIntent
        )

        val density = LocalDensity.current
        Calculator(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (WindowInsets.isImeVisible && isDescriptionEdit) {
                    with(density) {
                        val bottom = WindowInsets.imeAnimationTarget
                            .exclude(WindowInsets.navigationBars)
                            .getBottom(density)
                            .toDp()
                        if (bottom > 0.dp) bottom else 288.dp
                    }
                } else 288.dp),
            onNumClick = {
                moneyCalculator.appendMoneyEnd(it.digitToChar())
            },
            onAgainClick = {
                moneyCalculator.operate()
                onOperationIntent(
                    TransactionScreenIntent.TransactionOperation.Again(
                        money = BigDecimal(moneyCalculator.money.value)
                    )
                )
            },
            onDoneClick = {
                moneyCalculator.operate()
                onOperationIntent(
                    TransactionScreenIntent.TransactionOperation.Done(
                        money = BigDecimal(moneyCalculator.money.value)
                    )
                )
            },
            onOperatorClick = moneyCalculator::modifyOther,
            isContainOperator = isContainOperator
        )

    }
}

private data class IconAndContent(
    val iconId: Int,
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
                    TransactionIconHelper.getIcon(iac.iconId).Display(
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
        state = TransactionEditSurfaceState(
            transaction = Transaction(
                id = 0,
                icon_id = 0,
                content = "饮食",
                money = BigDecimal(0)
            )
        ),
        onOperationIntent = {},
        onTransactionChangeIntent = {}
    )
}