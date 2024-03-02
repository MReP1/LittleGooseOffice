plugins {
    alias(libs.plugins.goose.android.application)
    alias(libs.plugins.goose.android.compose)
}

android {
    namespace = "little.goose.note"
    defaultConfig {
        applicationId = "little.goose.note"
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.rumtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(project(":shared:feature:note"))
    implementation(project(":shared:design"))
    implementation(project(":shared:ui"))
    compileOnly(libs.koin.core)
    implementation(libs.koin.android)
}