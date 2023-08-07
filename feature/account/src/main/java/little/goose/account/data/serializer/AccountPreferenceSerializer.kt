package little.goose.account.data.serializer

import androidx.datastore.core.Serializer
import little.goose.account.data.model.AccountPreference
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

class AccountPreferenceSerializer @Inject constructor() : Serializer<AccountPreference> {

    override val defaultValue: AccountPreference = AccountPreference.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): AccountPreference {
        return try {
            // readFrom is already called on the data store background thread
            AccountPreference.parseFrom(input)
        } catch (exception: Exception) {
            throw Exception("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: AccountPreference, output: OutputStream) {
        t.writeTo(output)
    }

}