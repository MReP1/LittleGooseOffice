plugins {
    id("goose.android.library")
    id("goose.android.compose")
    id("goose.android.hilt")
}

android {
    namespace = "little.goose.home"

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintLayout)
    implementation(libs.accompanist.pager)
    implementation(libs.androidx.recyclerView)
    implementation(libs.google.android.material)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.dataStore.preferences)
    implementation(libs.compose.calendar)
    implementation(project(":core:design-system"))
    implementation(project(":core:common"))
    implementation(project(":feature:memorial"))
    implementation(project(":feature:schedule"))
    implementation(project(":feature:note"))
    implementation(project(":feature:account"))
    implementation(project(":calendarview"))
}