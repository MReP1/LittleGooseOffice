package little.goose.account.data.holder

import android.util.Log
import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.map
import little.goose.account.data.model.AccountPreference
import little.goose.account.data.model.IconDisplayTypeProto
import little.goose.account.data.model.copy
import little.goose.account.data.models.AccountConfig
import little.goose.account.data.models.IconDisplayType
import little.goose.account.data.models.TransactionConfig

class AccountConfigDataHolder(
    private val accountPreference: DataStore<AccountPreference>
) {
    val accountConfig = accountPreference.data.map { accountPreference ->
        val transactionConfig = TransactionConfig(
            iconDisplayType = when (accountPreference.transactionPreference.iconDisplayType) {
                IconDisplayTypeProto.ICON_CONTENT -> IconDisplayType.ICON_CONTENT
                IconDisplayTypeProto.ICON_ONLY -> IconDisplayType.ICON_ONLY
                IconDisplayTypeProto.UNRECOGNIZED, null -> IconDisplayType.ICON_CONTENT
            }
        )
        AccountConfig(
            transactionConfig = transactionConfig
        )
    }

    suspend fun setIconDisplayType(iconDisplayType: IconDisplayType) {
        runCatching {
            accountPreference.updateData { pref ->
                pref.copy {
                    transactionPreference = transactionPreference.copy {
                        this.iconDisplayType = when (iconDisplayType) {
                            IconDisplayType.ICON_CONTENT -> IconDisplayTypeProto.ICON_CONTENT
                            IconDisplayType.ICON_ONLY -> IconDisplayTypeProto.ICON_ONLY
                        }
                    }
                }
            }
        }.onFailure {
            Log.e("AccountPreference", "Failed to update user preferences")
        }
    }

}