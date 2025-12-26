import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
}

kotlin {

    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    val xcf = XCFramework("agoraio")

    iosArm64()
    iosSimulatorArm64()

    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget>().configureEach {

        binaries.framework {
            baseName = "agoraio"
            xcf.add(this)

            linkerOpts(
                "-F${project.projectDir}/libs",
                "-framework", "AgoraRtcKit"
            )
        }

        compilations["main"].cinterops {
            create("agora") {
                defFile(project.file("src/nativeInterop/cinterop/AgoraRtcKit.def"))
            }
        }
    }

    sourceSets {

        val commonMain by getting {
            dependencies {
                implementation(libs.kotlinx.coroutines.core)
            }
        }

        val androidMain by getting {
            dependencies {
                implementation("io.agora.rtc:full-sdk:4.3.0")
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.kotlinx.coroutines.test)
            }
        }
    }
}

android {
    namespace = "br.med.televida.pocagoraio.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}