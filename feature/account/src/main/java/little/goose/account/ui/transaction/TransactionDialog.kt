package little.goose.account.ui.transaction

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import little.goose.account.data.entities.Transaction
import little.goose.account.ui.transaction.icon.TransactionIconHelper
import little.goose.common.utils.toChineseMonthDayTime
import little.goose.common.utils.toSignString
import little.goose.design.system.component.dialog.*

@Composable
fun rememberTransactionDialogState(initIsShow: Boolean = false): TransactionDialogState {
    return rememberSaveable(saver = Saver(
        save = { it.dialogState.isShow },
        restore = { TransactionDialogState(it) }
    )) {
        TransactionDialogState(initIsShow)
    }
}

@Stable
class TransactionDialogState(_isShow: Boolean = false) {

    internal var transaction by mutableStateOf(Transaction())
        private set

    internal val dialogState: DialogState = DialogState(_isShow = _isShow)

    fun show(transaction: Transaction) {
        this.transaction = transaction
        dialogState.show()
    }

    fun dismiss() {
        dialogState.dismiss()
    }
}

@Composable
fun TransactionDialog(
    state: TransactionDialogState,
    onDelete: (Transaction) -> Unit
) {
    val deleteDialogState = remember { DeleteDialogState() }
    val context = LocalContext.current

    NormalDialog(
        state = state.dialogState
    ) {
        TransactionDialogScreen(
            transaction = state.transaction,
            onDeleteClick = { transaction ->
                deleteDialogState.show(onConfirm = {
                    onDelete(transaction)
                    state.dismiss()
                })
            },
            onEditClick = {
                TransactionActivity.openEdit(context, it)
                state.dismiss()
            }
        )
    }

    DeleteDialog(state = deleteDialogState)
}

@Composable
private fun TransactionDialogScreen(
    modifier: Modifier = Modifier,
    transaction: Transaction,
    onDeleteClick: (Transaction) -> Unit,
    onEditClick: (Transaction) -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(18.dp))
            Icon(
                modifier = Modifier.size(64.dp),
                painter = painterResource(
                    id = TransactionIconHelper.getIconPath(transaction.icon_id)
                ),
                contentDescription = transaction.content
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = transaction.content)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = transaction.money.toSignString(),
                    style = MaterialTheme.typography.displaySmall
                )
            }
            Text(text = transaction.time.toChineseMonthDayTime())

            Spacer(modifier = Modifier.height(12.dp))
            DialogButtonGroup(
                startButtonContent = {
                    Text(text = "删除")
                },
                onStartButtonClick = {
                    onDeleteClick(transaction)
                },
                endButtonContent = {
                    Text(text = "编辑")
                },
                onEndButtonClick = {
                    onEditClick(transaction)
                }
            )
        }
    }
}
