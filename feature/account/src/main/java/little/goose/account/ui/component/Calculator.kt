package little.goose.account.ui.component

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Backspace
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import little.goose.account.R
import little.goose.account.logic.MoneyCalculatorLogic
import little.goose.design.system.theme.AccountTheme

@Composable
fun Calculator(
    modifier: Modifier = Modifier,
    onNumClick: (num: Int) -> Unit,
    onAgainClick: () -> Unit,
    onDoneClick: () -> Unit,
    onOperatorClick: (MoneyCalculatorLogic) -> Unit,
    isContainOperator: Boolean
) {
    Column(modifier = modifier) {
        val context = LocalContext.current
        val vibrator = remember(context) {
            runCatching { context.getSystemService(Vibrator::class.java) }.getOrNull()
        }
        val clickVibrate = remember(vibrator) {
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    vibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
                } else {
                    vibrator?.vibrate(VibrationEffect.createOneShot(16, 180))
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1F),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (num in 7..9) {
                Cell(
                    modifier = Modifier.weight(1F),
                    onClick = {
                        clickVibrate()
                        onNumClick(num)
                    }
                ) {
                    Text(text = num.toString())
                }
            }
            Cell(
                modifier = Modifier.weight(1F),
                onClick = {
                    clickVibrate()
                    onOperatorClick(MoneyCalculatorLogic.BACKSPACE)
                }
            ) {
                Icon(imageVector = Icons.Rounded.Backspace, contentDescription = "BackSpace")
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1F),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (num in 4..6) {
                Cell(
                    modifier = Modifier.weight(1F),
                    onClick = {
                        clickVibrate()
                        onNumClick(num)
                    }
                ) {
                    Text(text = num.toString())
                }
            }
            Cell(
                modifier = Modifier.weight(1F),
                onClick = {
                    clickVibrate()
                    onOperatorClick(MoneyCalculatorLogic.Operator.PLUS)
                }
            ) {
                Text(text = "+")
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1F),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (num in 1..3) {
                Cell(
                    modifier = Modifier.weight(1F),
                    onClick = {
                        clickVibrate()
                        onNumClick(num)
                    }
                ) {
                    Text(text = num.toString())
                }
            }
            Cell(
                modifier = Modifier.weight(1F),
                onClick = {
                    clickVibrate()
                    onOperatorClick(MoneyCalculatorLogic.Operator.SUB)
                }
            ) {
                Text(text = "-")
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1F),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Cell(
                modifier = Modifier.weight(1F),
                onClick = {
                    clickVibrate()
                    onOperatorClick(MoneyCalculatorLogic.DOT)
                }
            ) {
                Text(text = ".")
            }
            Cell(
                modifier = Modifier.weight(1F),
                onClick = {
                    clickVibrate()
                    onNumClick(0)
                }
            ) {
                Text(text = "0")
            }
            Cell(
                modifier = Modifier.weight(1F),
                onClick = {
                    clickVibrate()
                    onAgainClick()
                }
            ) {
                Text(
                    text = stringResource(id = R.string.next_transaction),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Cell(
                modifier = Modifier.weight(1F),
                onClick = {
                    clickVibrate()
                    if (isContainOperator) {
                        onOperatorClick(MoneyCalculatorLogic.Operator.RESULT)
                    } else {
                        onDoneClick()
                    }
                }
            ) {
                Text(
                    text = if (isContainOperator) "=" else stringResource(id = R.string.done),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
private fun Cell(
    modifier: Modifier,
    onClick: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) = Surface(modifier = modifier, onClick = onClick) {
    ProvideTextStyle(value = MaterialTheme.typography.titleLarge) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
            content = content
        )
    }
}

@Preview(heightDp = 380)
@Composable
fun PreviewCalculator() = AccountTheme {
    Calculator(
        onNumClick = {},
        onAgainClick = {},
        onDoneClick = {},
        onOperatorClick = {},
        isContainOperator = false
    )
}