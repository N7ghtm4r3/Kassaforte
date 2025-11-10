import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import org.jetbrains.dokka.DokkaConfiguration.Visibility.*
import org.jetbrains.dokka.base.DokkaBase
import org.jetbrains.dokka.base.DokkaBaseConfiguration
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.vanniktech.mavenPublish)
    alias(libs.plugins.dokka)
    kotlin("plugin.serialization") version "2.2.0"
}

group = "com.tecknobit.kassaforte"
version = "1.0.0beta-02"

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
                implementation(libs.equinox.core)
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
            }
        }

        val cipherBasedMain by creating {
            dependsOn(commonMain)
        }

        val jvmMain by getting {
            dependsOn(cipherBasedMain)
            dependencies {
                implementation(libs.keyring)
                implementation(libs.kotlinx.coroutines.swing)
                implementation(libs.windpapi4j)
            }
        }

        val hybridKassaforteMain by creating {
            dependsOn(commonMain)
        }

        val androidMain by getting {
            dependsOn(cipherBasedMain)
            dependsOn(hybridKassaforteMain)
            dependencies {
                implementation(libs.startup)
            }
        }

        val wasmJsMain by getting {
            dependsOn(hybridKassaforteMain)
            dependencies {
                implementation(libs.kotlin.browser)
            }
        }

    }
}

mavenPublishing {
    configure(
        platform = KotlinMultiplatform(
            javadocJar = JavadocJar.Dokka("dokkaHtml"),
            sourcesJar = true
        )
    )
    coordinates(
        groupId = "io.github.n7ghtm4r3",
        artifactId = "kassaforte",
        version = "1.0.0beta-02"
    )
    pom {
        name.set("Kassaforte")
        description.set("Kassaforte enables secure storage of sensitive data in Compose Multiplatform applications and on the backend by leveraging each platform’s native security APIs. It further supports the generation and usage of symmetric and asymmetric keys to ensure data protection")
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

buildscript {
    dependencies {
        classpath(libs.dokka.base)
    }
}

subprojects {
    apply(plugin = "org.jetbrains.dokka")
}

tasks.dokkaHtml {
    outputDirectory.set(layout.projectDirectory.dir("../docs/dokka"))
    dokkaSourceSets.configureEach {
        moduleName = "Kassaforte"
        includeNonPublic.set(true)
        documentedVisibilities.set(setOf(PUBLIC, PROTECTED, PRIVATE, INTERNAL))
    }
    pluginConfiguration<DokkaBase, DokkaBaseConfiguration> {
        footerMessage = "(c) 2025 Tecknobit"
    }
}