plugins {
    alias(libs.plugins.goose.android.library)
    alias(libs.plugins.goose.android.compose)
}

android {
    namespace = "little.goose.appwidget"
}

dependencies {
    implementation(libs.androidx.glance)
    implementation(libs.androidx.glance.appwidget)
    implementation(project(":core:common"))
}