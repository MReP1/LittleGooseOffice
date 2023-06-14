plugins {
    id("goose.android.library")
    id("goose.android.compose")
}

android {
    namespace = "little.goose.appwidget"
}

dependencies {
    implementation(libs.androidx.glance)
    implementation(libs.androidx.glance.appwidget)
    implementation(libs.androidx.compose.material.iconsExtended)
}