plugins {
    id("goose.android.library")
    id("goose.android.compose")
    id("goose.android.hilt")
    alias(libs.plugins.protobuf)
}

android {
    namespace = "little.goose.design.system"
}

protobuf {
    protoc {
        artifact = libs.protobuf.protoc.get().toString()
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                register("java") {
                    option("lite")
                }
                register("kotlin") {
                    option("lite")
                }
            }
        }
    }
}

dependencies {
    api(libs.androidx.compose.ui)
    api(libs.androidx.compose.ui.tool.preview)
    api(libs.androidx.compose.ui.tooling)
    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.material.iconsExtended)
    implementation(libs.protobuf.kotlin.lite)
    implementation(libs.androidx.datasotre)
    implementation(libs.androidx.appcompat)
    implementation(project(":core:common"))
}