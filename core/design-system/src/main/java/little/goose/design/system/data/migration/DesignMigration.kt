package little.goose.design.system.data.migration

import androidx.datastore.core.DataMigration
import little.goose.design.system.data.ColorTypeProto
import little.goose.design.system.data.DesignPreference
import little.goose.design.system.data.copy

class DynamicColorMigration : DataMigration<DesignPreference> {
    override suspend fun cleanUp() = Unit

    override suspend fun shouldMigrate(currentData: DesignPreference): Boolean {
        return !currentData.isMigrateColorType
    }

    override suspend fun migrate(currentData: DesignPreference): DesignPreference {
        return currentData.copy {
            this.colorType = if (deprecatedIsDynamicColor) {
                ColorTypeProto.DYNAMIC
            } else {
                ColorTypeProto.CUSTOM
            }
            isMigrateColorType = true
        }
    }

}