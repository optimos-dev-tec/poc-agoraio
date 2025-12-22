import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinCocoapods)
}

val xcFrameworkName = "AgoraRtcEngine_iOS_Beta"

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    val xcf = XCFramework(xcFrameworkName)
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
            xcf.add(this)
        }
    }

    cocoapods {
        version = "1.0.0"
        summary = "Shared module for PocAgoraIO"
        homepage = "Link to your project homepage"
        ios.deploymentTarget = "14.1" // Use uma versão compatível
        podfile = project.file("../iosApp/Podfile") // Caminho para o Podfile do seu app iOS

        // Adiciona a dependência do SDK do Agora via Pod
        pod("AgoraRtcEngine_iOS") {
            version = "4.3.0" // Use a versão mais recente ou a desejada
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
        }

        val iosArm64Main by getting { dependsOn(iosMain) }
        val iosSimulatorArm64Main by getting { dependsOn(iosMain) }

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
