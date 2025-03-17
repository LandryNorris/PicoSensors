import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    kotlin("plugin.serialization") version "2.1.10"
}

group = "io.github.landrynorris"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    // Note, if you develop a library, you should use compose.desktop.common.
    // compose.desktop.currentOs should be used in launcher-sourceSet
    // (in a separate module for demo project and in testMain).
    // With compose.desktop.common you will also lose @Preview functionality
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    implementation(compose.materialIconsExtended)

    implementation("com.fazecast:jSerialComm:2.11.0")
    implementation("com.arkivanov.decompose:decompose:3.3.0")
    implementation("com.arkivanov.decompose:extensions-compose:3.3.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.10.1")

    implementation("org.jetbrains.lets-plot:lets-plot-kotlin-kernel:4.9.3")
    implementation("org.jetbrains.lets-plot:lets-plot-common:4.5.2")
    implementation("org.jetbrains.lets-plot:platf-awt:4.5.2")

    implementation("org.jetbrains.lets-plot:lets-plot-compose:2.1.1")

    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.2")
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "SensorClient"
            packageVersion = "1.0.0"
        }
    }
}
