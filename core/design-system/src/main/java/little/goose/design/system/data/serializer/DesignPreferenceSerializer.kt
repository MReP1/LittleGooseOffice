package little.goose.design.system.data.serializer

import androidx.datastore.core.Serializer
import little.goose.design.system.data.DesignThemePreference
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

class DesignPreferenceSerializer @Inject constructor() : Serializer<DesignThemePreference> {

    override val defaultValue: DesignThemePreference = DesignThemePreference.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): DesignThemePreference {
        return try {
            // readFrom is already called on the data store background thread
            DesignThemePreference.parseFrom(input)
        } catch (exception: Exception) {
            throw Exception("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: DesignThemePreference, output: OutputStream) {
        t.writeTo(output)
    }

}