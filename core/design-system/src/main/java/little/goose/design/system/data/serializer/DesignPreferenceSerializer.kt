package little.goose.design.system.data.serializer

import androidx.datastore.core.Serializer
import little.goose.design.system.data.DesignPreference
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

class DesignPreferenceSerializer @Inject constructor() : Serializer<DesignPreference> {

    override val defaultValue: DesignPreference = DesignPreference.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): DesignPreference {
        return try {
            // readFrom is already called on the data store background thread
            DesignPreference.parseFrom(input)
        } catch (exception: Exception) {
            throw Exception("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: DesignPreference, output: OutputStream) {
        t.writeTo(output)
    }

}