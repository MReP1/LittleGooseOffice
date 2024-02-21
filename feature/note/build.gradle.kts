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

    // 富文本
    implementation(project(":core:design-system"))
    implementation(project(":core:common"))
    implementation(project(":core:ui"))

    // Markdown note
    implementation(libs.compose.markdown.android)

    implementation(libs.androidx.compose.navigation)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    implementation(libs.androidx.activity.ktx)
    api(project(":shared:feature:note"))

    implementation(libs.voyager.navigator)
}