import org.gradle.api.JavaVersion

object AndroidConfigConventions {
    private const val SDK_VERSION = 33
    private const val PACKAGE_NAME = "little.goose.account"

    const val MIN_SDK_VERSION = 26
    const val COMPILE_SDK_VERSION = SDK_VERSION
    const val TARGET_SDK_VERSION = SDK_VERSION

    const val APP_NAMESPACE = PACKAGE_NAME
    const val APPLICATION_ID = PACKAGE_NAME

    val JAVA_VERSION = JavaVersion.VERSION_11

    const val VersionCode = 1
    const val VersionName = "1.0"

}