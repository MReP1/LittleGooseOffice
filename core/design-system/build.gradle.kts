plugins {
    alias(libs.plugins.goose.android.library)
    alias(libs.plugins.goose.android.compose)
    alias(libs.plugins.goose.android.hilt)
    alias(libs.plugins.protobuf)
}

android {

    defaultConfig {
        consumerProguardFiles("consumer-proguard-rules.pro")
    }

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

androidComponents.beforeVariants {
    android.sourceSets.getByName(it.name) {
        val buildDir = layout.buildDirectory.get().asFile
        java.srcDir(buildDir.resolve("generated/source/proto/${it.name}/java"))
        kotlin.srcDir(buildDir.resolve("generated/source/proto/${it.name}/kotlin"))
    }
}

afterEvaluate {
    // Fix the implicit with ksp and protobuf.
    tasks.forEach { task ->
        if (task.name.contains("kspDebugKotlin")) {
            task.dependsOn("generateDebugProto")
        }
        if (task.name.contains("kspReleaseKotlin")) {
            task.dependsOn("generateReleaseProto")
        }
    }
}

dependencies {
    api(libs.androidx.compose.ui)
    api(libs.androidx.compose.ui.tool.preview)
    api(libs.androidx.compose.ui.tooling)
    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.material.iconsExtended)
    api(libs.androidx.compose.material3.windowSizeClass)
    implementation(project(":shared:ui"))
    implementation(libs.protobuf.kotlin.lite)
    implementation(libs.androidx.datastore)
    implementation(libs.androidx.dataStore.preferences)
    implementation(libs.androidx.appcompat)
    implementation(project(":core:common"))
    implementation(libs.androidx.window)
}