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

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "agoraio"
            isStatic = true
            xcf.add(this)
            export("agora:agora")
            linkerOpts.add("-F${project.projectDir}/libs")
        }
        iosTarget.compilations.getByName("main"){
            cinterops.create("agora") {
                defFile(project.file("src/nativeInterop/cinterop/AgoraRtcKit.def"))
                packageName("agora")
                compilerOpts("-framework", "AgoraRtcKit", "-F${project.projectDir}/libs")
            }
        }
    }

    sourceSets {

        val commonMain by getting {
            dependencies {
                // Coroutines (StateFlow / SharedFlow)
                implementation(libs.kotlinx.coroutines.core)
            }
        }

        val androidMain by getting {
            dependencies {
                // A versão do Android foi atualizada para ser mais próxima da de iOS.
                implementation("io.agora.rtc:full-sdk:4.3.0")
            }
        }

        val iosMain by creating {
            dependsOn(commonMain)

            dependencies {
                implementation(libs.kotlinx.coroutines.core)
                implementation(
                    project.files(
                        project.tasks.getByName<org.jetbrains.kotlin.gradle.tasks.CInteropProcess>(
                            "cinteropAgoraIosArm64"
                        ).outputFile
                    )
                )
                implementation(
                    project.files(
                        project.tasks.getByName<org.jetbrains.kotlin.gradle.tasks.CInteropProcess>(
                            "cinteropAgoraIosSimulatorArm64"
                        ).outputFile
                    )
                )

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
