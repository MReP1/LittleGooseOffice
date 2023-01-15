object Versions {
    const val compose = "1.3.2"
    const val compose_compiler = "1.3.2"
    const val accompanist_version = "0.28.0"
    const val lifecycle = "2.5.1"
    const val hilt = "2.44.2"
    const val hilt_compose = "1.0.0"
    const val android_core_ktx = "1.9.0"
    const val appcompat = "1.5.1"
    const val recycler_view = "1.2.1"
}

object Dependencies {

    object Androidx {
        const val core_ktx = "androidx.core:core-ktx:${Versions.android_core_ktx}"
        const val appcompat = "androidx.appcompat:appcompat:${Versions.appcompat}"
    }

    const val recycler_view = "androidx.recyclerview:recyclerview:${Versions.recycler_view}"

    object Lifecycle {

        const val runtime_ktx =
            "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycle}"

        object ViewModel {
            const val ktx =
                "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle}"
            const val compose =
                "androidx.lifecycle:lifecycle-viewmodel-compose:${Versions.lifecycle}"
            const val savedState =
                "androidx.lifecycle:lifecycle-viewmodel-savedstate:${Versions.lifecycle}"
        }
    }

    object Compose {
        const val ui = "androidx.compose.ui:ui:${Versions.compose}"
        const val ui_tool_preview = "androidx.compose.ui:ui-tooling-preview:${Versions.compose}"
        const val material3 = "androidx.compose.material3:material3:1.0.1"
        const val activity = "androidx.activity:activity-compose:1.6.1"
        const val hilt_navigation = "androidx.hilt:hilt-navigation-compose:${Versions.hilt_compose}"

        const val ui_test_junit4 = "androidx.compose.ui:ui-test-junit4:${Versions.compose}"
        const val ui_tooling = "androidx.compose.ui:ui-tooling:${Versions.compose}"
        const val ui_test_manifest = "androidx.compose.ui:ui-test-manifest:${Versions.compose}"

        object Accompanist {
            const val navigation_animation =
                "com.google.accompanist:accompanist-navigation-animation:${Versions.accompanist_version}"
        }
    }

    object Hilt {
        const val hilt_android =
            "com.google.dagger:hilt-android:${Versions.hilt}"
        const val hilt_android_compiler =
            "com.google.dagger:hilt-android-compiler:${Versions.hilt}"
    }

}