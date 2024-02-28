package little.goose.account.ui.component

import Vibration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import little.goose.account.R
import little.goose.account.logic.MoneyCalculatorLogic
import rememberVibrator

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
        val vibrator = rememberVibrator()
        val clickVibrate = remember(vibrator) {
            { vibrator.vibrate(Vibration.ClickShot) }
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
                Icon(imageVector = Icons.AutoMirrored.Rounded.Backspace, contentDescription = "BackSpace")
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