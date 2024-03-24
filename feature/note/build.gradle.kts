plugins {
    alias(libs.plugins.goose.android.library)
    alias(libs.plugins.goose.android.compose)
    alias(libs.plugins.goose.android.hilt)
    alias(libs.plugins.goose.android.room)
}

android {
    namespace = "little.goose.note"
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintLayout)

    implementation(libs.google.android.material)

    implementation(project(":core:design-system"))
    implementation(project(":core:common"))

    implementation(libs.androidx.compose.navigation)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    implementation(libs.androidx.activity.ktx)
    implementation(project(":shared:feature:note"))
    implementation(project(":shared:data:note"))
    implementation(project(":shared:ui"))
    implementation(project(":shared:common"))

    compileOnly(libs.koin.core)
    implementation(libs.koin.compose)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
}