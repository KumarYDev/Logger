import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

abstract class GenerateBuildConfigTask : DefaultTask() {
    @get:Input
    abstract val loggingEnabled: Property<Boolean>

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun generate() {
        val enabled = loggingEnabled.get()
        val dir = outputDir.get().asFile.resolve("com/asura/logger")
        dir.mkdirs()
        dir.resolve("BuildConfig.kt").writeText(
            """
            package com.asura.logger

            object BuildConfig {
                const val isLogEnabled: Boolean = $enabled
            }
            """.trimIndent() + "\n"
        )
    }
}

val isRelease = gradle.startParameter.taskNames.any { taskName ->
    taskName.contains("Release", ignoreCase = true) ||
    taskName.contains("Production", ignoreCase = true) ||
    taskName.contains("Distribution", ignoreCase = true)
}

val isLogEnabledProperty = providers.gradleProperty("isLogEnabled")
    .orElse(providers.gradleProperty("logEnabled"))
    .map { it.toBoolean() }
    .orNull

val isLogEnabledValue: Boolean = isLogEnabledProperty ?: (!isRelease)

val generateBuildConfig = tasks.register<GenerateBuildConfigTask>("generateBuildConfig") {
    loggingEnabled.set(isLogEnabledValue)
    outputDir.set(layout.buildDirectory.dir("generated/buildconfig/kotlin"))
}

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }
    
    jvm()
    
    js {
        browser()
        binaries.executable()
    }
    
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }
    
    android {
       namespace = "com.asura.logger.shared"
       compileSdk = libs.versions.android.compileSdk.get().toInt()
       minSdk = libs.versions.android.minSdk.get().toInt()
    
       compilerOptions {
           jvmTarget = JvmTarget.JVM_11
       }
       androidResources {
           enable = true
       }
       withHostTest {
           isIncludeAndroidResources = true
       }
       withDeviceTestBuilder {
           sourceSetTreeName = "test"
       }.configure {
           instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
       }
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.compose.uiTooling)
        }
        commonMain {
            kotlin.srcDir(generateBuildConfig.map { it.outputs })
            dependencies {
                implementation(libs.compose.runtime)
                implementation(libs.compose.foundation)
                implementation(libs.compose.material3)
                implementation(libs.compose.ui)
                implementation(libs.compose.components.resources)
                implementation(libs.compose.uiToolingPreview)
                implementation(libs.androidx.lifecycle.viewmodelCompose)
                implementation(libs.androidx.lifecycle.runtimeCompose)
                implementation(libs.kotlinx.coroutines.core)

                implementation(libs.kermit)
            }
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jsMain.dependencies {
            implementation(libs.wrappers.browser)
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}