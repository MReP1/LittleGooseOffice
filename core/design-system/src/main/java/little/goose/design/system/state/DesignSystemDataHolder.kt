package little.goose.design.system.state

import android.util.Log
import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.map
import little.goose.design.system.data.ColorTypeProto
import little.goose.design.system.data.DesignPreference
import little.goose.design.system.data.ThemeTypeProto
import little.goose.design.system.data.copy
import little.goose.design.system.theme.ThemeConfig
import little.goose.design.system.theme.ThemeType

class DesignSystemDataHolder(
    private val designPreference: DataStore<DesignPreference>
) {
    val themeConfig = designPreference.data.map { designPreference ->
        val themeType = when (designPreference.themeType) {
            null, ThemeTypeProto.UNRECOGNIZED, ThemeTypeProto.FOLLOW_SYSTEM -> ThemeType.FOLLOW_SYSTEM
            ThemeTypeProto.LIGHT -> ThemeType.LIGHT
            ThemeTypeProto.DARK -> ThemeType.DART
        }
        val isDynamicColor = when (designPreference.colorType) {
            null, ColorTypeProto.UNRECOGNIZED, ColorTypeProto.DYNAMIC -> true
            ColorTypeProto.CUSTOM -> false
        }
        ThemeConfig(isDynamicColor = isDynamicColor, themeType = themeType)
    }

    suspend fun setThemeConfig(themeConfig: ThemeConfig) {
        runCatching {
            designPreference.updateData { pref ->
                pref.copy {
                    themeType = when (themeConfig.themeType) {
                        ThemeType.FOLLOW_SYSTEM -> ThemeTypeProto.FOLLOW_SYSTEM
                        ThemeType.LIGHT -> ThemeTypeProto.LIGHT
                        ThemeType.DART -> ThemeTypeProto.DARK
                    }
                    colorType = when (themeConfig.isDynamicColor) {
                        true -> ColorTypeProto.DYNAMIC
                        false -> ColorTypeProto.CUSTOM
                    }
                }
            }
        }.onFailure {
            Log.e("DesignPreference", "Failed to update user preferences")
        }
    }
}