package little.goose.account.ui.transaction

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.dialog
import androidx.navigation.navArgument
import little.goose.account.data.entities.Transaction
import little.goose.account.ui.transaction.icon.TransactionIconHelper
import little.goose.common.utils.toChineseMonthDayTime
import little.goose.common.utils.toSignString
import little.goose.design.system.component.dialog.DeleteDialog
import little.goose.design.system.component.dialog.DeleteDialogState
import little.goose.design.system.component.dialog.DialogButtonGroup

const val ROUTE_DIALOG_TRANSACTION = "dialog_transaction"

fun NavController.navigateToTransactionDialog(transactionId: Long) {
    navigate("$ROUTE_DIALOG_TRANSACTION/$transactionId") {
        launchSingleTop = true
    }
}

fun NavGraphBuilder.transactionDialogRoute(
    onDismissRequest: () -> Unit,
    onNavigateToTransaction: (Long) -> Unit
) = dialog(
    route = "$ROUTE_DIALOG_TRANSACTION/{$KEY_TRANSACTION_ID}",
    arguments = listOf(
        navArgument(KEY_TRANSACTION_ID) {
            type = NavType.LongType
        }
    )
) {
    val viewModel = hiltViewModel<TransactionDialogViewModel>()
    val transaction by viewModel.transaction.collectAsStateWithLifecycle()
    val deleteDialogState = remember { DeleteDialogState() }

    TransactionDialogScreen(
        transaction = transaction,
        onDeleteClick = {
            deleteDialogState.show(
                onConfirm = {
                    viewModel.deleteTransaction(it)
                    onDismissRequest()
                }
            )
        },
        onEditClick = {
            onDismissRequest()
            onNavigateToTransaction(it.id!!)
        }
    )

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
                    Text(text = stringResource(id = little.goose.account.R.string.delete))
                },
                onStartButtonClick = {
                    onDeleteClick(transaction)
                },
                endButtonContent = {
                    Text(text = stringResource(id = little.goose.account.R.string.edit))
                },
                onEndButtonClick = {
                    onEditClick(transaction)
                }
            )
        }
    }
}
