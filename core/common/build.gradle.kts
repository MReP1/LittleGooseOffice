@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("goose.android.library")
    alias(libs.plugins.ksp)
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
    implementation("androidx.localbroadcastmanager:localbroadcastmanager:1.0.0")

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.google.android.material)

    implementation(project(":core:utils"))

    // Room database
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
}