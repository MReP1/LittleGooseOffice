plugins {
    alias(libs.plugins.goose.android.library)
    alias(libs.plugins.goose.android.compose)
    alias(libs.plugins.goose.android.hilt)
    alias(libs.plugins.goose.android.room)
}

android {
    namespace = "little.goose.schedule"
}

dependencies {
    implementation(libs.androidx.appcompat)

    implementation(project(":core:design-system"))
    implementation(project(":core:common"))
    implementation(project(":core:ui"))

    // hilt
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)

}