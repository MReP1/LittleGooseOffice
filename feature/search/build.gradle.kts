plugins {
    alias(libs.plugins.goose.android.library)
    alias(libs.plugins.goose.android.compose)
    alias(libs.plugins.goose.android.hilt)
}

android {
    namespace = "little.goose.search"
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.compose.navigation)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(project(":core:ui"))
    implementation(project(":core:design-system"))
    implementation(project(":core:common"))
    implementation(project(":feature:memorial"))
    implementation(project(":feature:note"))
    implementation(project(":feature:account"))
}