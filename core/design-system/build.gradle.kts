plugins {
    id("goose.android.library")
    id("goose.android.compose")
}

android {
    namespace = "little.goose.design.system"
}

dependencies {
    api(libs.androidx.compose.ui)
    api(libs.androidx.compose.ui.tool.preview)
    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.material.iconsExtended)
}