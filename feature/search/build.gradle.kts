plugins {
    id("goose.android.library")
    id("goose.android.compose")
    id("goose.android.hilt")
}

android {
    namespace = "little.goose.search"
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(project(":core:design-system"))
    implementation(project(":core:common"))
    implementation(project(":feature:memorial"))
    implementation(project(":feature:schedule"))
    implementation(project(":feature:note"))
    implementation(project(":feature:account"))
}