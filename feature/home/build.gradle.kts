plugins {
    alias(libs.plugins.goose.android.library)
    alias(libs.plugins.goose.android.compose)
    alias(libs.plugins.goose.android.hilt)
}

android {
    namespace = "little.goose.home"
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.google.android.material)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.compose.navigation)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.dataStore.preferences)
    implementation(libs.compose.calendar)
    implementation(libs.androidx.compose.material3.windowSizeClass)
    implementation(project(":core:design-system"))
    implementation(project(":core:common"))
    implementation(project(":feature:search"))
    implementation(project(":feature:memorial"))
    implementation(project(":feature:note"))
    implementation(project(":feature:account"))
    implementation(project(":feature:settings"))
    implementation(project(":core:ui"))

    compileOnly(libs.koin.core)
    implementation(libs.koin.compose)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
}