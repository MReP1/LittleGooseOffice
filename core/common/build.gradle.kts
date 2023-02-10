plugins {
    id("goose.android.library")
}

android {
    namespace = "little.goose.common"
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
}