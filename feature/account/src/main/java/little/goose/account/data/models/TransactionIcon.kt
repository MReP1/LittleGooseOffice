package little.goose.account.data.models

import little.goose.account.R
import little.goose.account.ui.transaction.icon.TransactionIconHelper
import little.goose.design.system.util.Icon

data class TransactionIcon(
    val id: Int,
    val type: Int,
    val name: String,
    val iconResId: Int,
) {
    val icon get() = TransactionIconHelper.icons[iconResId] ?: Icon.Drawable(R.drawable.icon_money)
}