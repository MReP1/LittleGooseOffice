package little.goose.account.data.models

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArtTrack
import androidx.compose.material.icons.rounded.Image
import androidx.compose.ui.graphics.vector.ImageVector
import little.goose.account.R

data class AccountConfig(
    val transactionConfig: TransactionConfig = TransactionConfig()
)

data class TransactionConfig(
    val iconDisplayType: IconDisplayType = IconDisplayType.ICON_ONLY
)

enum class IconDisplayType(
    @StringRes val textRes: Int,
    val icon: ImageVector
) {
    ICON_CONTENT(
        textRes = R.string.icon_content,
        icon = Icons.Rounded.ArtTrack
    ),
    ICON_ONLY(
        textRes = R.string.icon_only,
        icon = Icons.Rounded.Image
    );
}
