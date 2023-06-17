package little.goose.design.system.state

import android.util.Log
import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.map
import little.goose.design.system.data.DesignPreference
import little.goose.design.system.data.copy
import little.goose.design.system.theme.ThemeConfig
import little.goose.design.system.theme.ThemeType
import little.goose.design.system.data.ThemeType as ProtoThemeType

class DesignSystemStateHolder(
    private val designPreference: DataStore<DesignPreference>
) {
    val themeConfig = designPreference.data.map { designPreference ->
        val themeType = when (designPreference.themeType) {
            null, ProtoThemeType.UNRECOGNIZED, ProtoThemeType.FOLLOW_SYSTEM -> ThemeType.FOLLOW_SYSTEM
            ProtoThemeType.LIGHT -> ThemeType.LIGHT
            ProtoThemeType.DARK -> ThemeType.DART
        }
        val isDynamicColor = designPreference.isDynamicColor
        ThemeConfig(isDynamicColor, themeType)
    }

    suspend fun setThemeConfig(themeConfig: ThemeConfig) {
        runCatching {
            designPreference.updateData { pref ->
                pref.copy {
                    themeType = when (themeConfig.themeType) {
                        ThemeType.FOLLOW_SYSTEM -> ProtoThemeType.FOLLOW_SYSTEM
                        ThemeType.LIGHT -> ProtoThemeType.LIGHT
                        ThemeType.DART -> ProtoThemeType.DARK
                    }
                    isDynamicColor = themeConfig.isDynamicColor
                }
            }
        }.onFailure {
            Log.e("DesignPreference", "Failed to update user preferences")
        }
    }
}