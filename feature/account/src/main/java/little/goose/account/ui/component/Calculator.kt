package little.goose.account.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Backspace
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import little.goose.account.logic.MoneyCalculatorLogic

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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Cell(
                modifier = Modifier.weight(1F),
                onClick = { onNumClick(7) }
            ) {
                Text(text = "7")
            }
            Cell(
                modifier = Modifier.weight(1F),
                onClick = { onNumClick(8) }
            ) {
                Text(text = "8")
            }
            Cell(
                modifier = Modifier.weight(1F),
                onClick = { onNumClick(9) }
            ) {
                Text(text = "9")
            }
            Cell(
                modifier = Modifier.weight(1F),
                onClick = { onOperatorClick(MoneyCalculatorLogic.BACKSPACE) }
            ) {
                Icon(imageVector = Icons.Rounded.Backspace, contentDescription = "BackSpace")
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Cell(
                modifier = Modifier.weight(1F),
                onClick = { onNumClick(4) }
            ) {
                Text(text = "4")
            }
            Cell(
                modifier = Modifier.weight(1F),
                onClick = { onNumClick(5) }
            ) {
                Text(text = "5")
            }
            Cell(
                modifier = Modifier.weight(1F),
                onClick = { onNumClick(6) }
            ) {
                Text(text = "6")
            }
            Cell(
                modifier = Modifier.weight(1F),
                onClick = { onOperatorClick(MoneyCalculatorLogic.Operator.PLUS) }
            ) {
                Text(text = "+")
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Cell(
                modifier = Modifier.weight(1F),
                onClick = { onNumClick(1) }
            ) {
                Text(text = "1")
            }
            Cell(
                modifier = Modifier.weight(1F),
                onClick = { onNumClick(2) }
            ) {
                Text(text = "2")
            }
            Cell(
                modifier = Modifier.weight(1F),
                onClick = { onNumClick(3) }
            ) {
                Text(text = "3")
            }
            Cell(
                modifier = Modifier.weight(1F),
                onClick = { onOperatorClick(MoneyCalculatorLogic.Operator.SUB) }
            ) {
                Text(text = "-")
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
        ) {
            Cell(
                modifier = Modifier.weight(1F),
                onClick = { onOperatorClick(MoneyCalculatorLogic.DOT) }
            ) {
                Text(text = ".")
            }
            Cell(
                modifier = Modifier.weight(1F),
                onClick = { onNumClick(0) }
            ) {
                Text(text = "0")
            }
            Cell(modifier = Modifier.weight(1F), onAgainClick) {
                Text(text = "下一笔", style = MaterialTheme.typography.titleMedium)
            }
            Cell(
                modifier = Modifier.weight(1F),
                onClick = {
                    if (isContainOperator) {
                        onOperatorClick(MoneyCalculatorLogic.Operator.RESULT)
                    } else {
                        onDoneClick()
                    }
                }
            ) {
                Text(
                    text = if (isContainOperator) "=" else "完成",
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