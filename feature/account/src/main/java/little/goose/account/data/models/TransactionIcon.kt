package little.goose.account.data.models

import androidx.annotation.DrawableRes

data class TransactionIcon(
    val id: Int,
    val type: Int,
    val name: String,
    @DrawableRes val path: Int
)