plugins {
    id("goose.android.library")
    id("goose.android.compose")
    id("goose.android.hilt")
    alias(libs.plugins.ksp)
    alias(libs.plugins.protobuf)
}

android {
    namespace = "little.goose.account"
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
        java.srcDir(buildDir.resolve("generated/source/proto/${it.name}/java/"))
        kotlin.srcDir(buildDir.resolve("generated/source/proto/${it.name}/kotlin"))
    }
}

afterEvaluate {
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
    implementation(libs.androidx.appcompat)

    implementation(libs.androidx.constraintLayout)

    implementation(project(":core:design-system"))
    implementation(project(":core:common"))
    implementation(project(":core:ui"))

    implementation(libs.androidx.fragment.ktx)
    implementation(libs.google.android.material)

    implementation(libs.androidx.recyclerView)

    // Room database
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    implementation(libs.accompanist.navigation.animation)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.constraintLayout.compose)

    implementation(libs.androidx.lifecycle.runtime.compose)

    // MPAndroidChart
    implementation("com.github.PhilJay:MPAndroidChart:3.1.0")

    // protobuf
    implementation(libs.protobuf.kotlin.lite)
    // datastore
    implementation(libs.androidx.datastore)
}