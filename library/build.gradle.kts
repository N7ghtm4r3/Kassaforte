import com.vanniktech.maven.publish.KotlinMultiplatform
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.vanniktech.mavenPublish)
    kotlin("plugin.serialization") version "2.2.0"
}

group = "com.tecknobit.kassaforte"
version = "1.0.0beta-01"

kotlin {
    androidTarget {
        publishLibraryVariants("release")
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_18)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
        macosX64(),
        macosArm64()
    ).forEach { appleTarget ->
        appleTarget.binaries.framework {
            baseName = "kassaforte"
            isStatic = true
        }
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        binaries.executable()
        browser {
            webpackTask {
            }
        }
    }

    jvm {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_18)
        }
    }

    sourceSets {

        val commonMain by getting {
            dependencies {
                // TODO: TO USE THIS INSTEAD
                // implementation(libs.equinox.core)
                implementation(libs.json)
                implementation(libs.kotlinx.coroutines)
            }
        }

        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val macosX64Main by getting
        val macosArm64Main by getting
        val appleMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            macosX64Main.dependsOn(this)
            macosArm64Main.dependsOn(this)
            dependencies {
                implementation(libs.krypto)
            }
        }

        val cipherBasedMain by creating {
            dependsOn(commonMain)
            // TODO: TO REMOVE AND USE IN THE CORE ONE
            dependencies {
                implementation(libs.equinox.core)
            }
        }

        val jvmMain by getting {
            dependsOn(cipherBasedMain)
            dependencies {
                implementation(libs.keyring)
                implementation(libs.kotlinx.coroutines.swing)
            }
        }

        val hybridMain by creating {
            dependsOn(commonMain)
            // TODO: TO REMOVE AND USE IN THE CORE ONE
            dependencies {
                implementation(libs.equinox.core)
            }
        }

        val androidMain by getting {
            dependsOn(cipherBasedMain)
            dependsOn(hybridMain)
            dependencies {
                implementation(libs.startup)
            }
        }

        val wasmJsMain by getting {
            dependsOn(hybridMain)
            dependencies {
                implementation(libs.kotlin.browser)
            }
        }

    }
}

mavenPublishing {
    configure(
        platform = KotlinMultiplatform(
            // TODO: TO SET 
            //javadocJar = JavadocJar.Dokka("dokkaHtml"),
            sourcesJar = true
        )
    )
    coordinates(
        groupId = "io.github.n7ghtm4r3",
        artifactId = "kassaforte",
        version = "1.0.0beta-01"
    )
    pom {
        name.set("Kassaforte")
        // TODO: TO SET
        // description.set("Utilities to handle the navigation in Compose Multiplatform applications")
        inceptionYear.set("2025")
        url.set("https://github.com/N7ghtm4r3/Kassaforte")

        licenses {
            license {
                name.set("APACHE2")
                url.set("https://opensource.org/license/apache-2-0")
            }
        }
        developers {
            developer {
                id.set("N7ghtm4r3")
                name.set("Manuel Maurizio")
                email.set("maurizio.manuel2003@gmail.com")
                url.set("https://github.com/N7ghtm4r3")
            }
        }
        scm {
            url.set("https://github.com/N7ghtm4r3/Kassaforte")
        }
    }
    publishToMavenCentral()
    signAllPublications()
}

android {
    namespace = "com.tecknobit.kassaforte"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_18
        targetCompatibility = JavaVersion.VERSION_18
    }
}