plugins {
    alias(libs.plugins.goose.android.library)
    alias(libs.plugins.goose.android.hilt)
    alias(libs.plugins.goose.android.room)
}

android {
    namespace = "little.goose.common"

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.rumtime.ktx)
    implementation(libs.androidx.dataStore.preferences)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.google.android.material)

    // Metrics
    api(libs.androidx.metrics)
}