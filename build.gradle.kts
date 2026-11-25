import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    kotlin("multiplatform") version "2.3.20"
}

kotlin {
    val xcf = XCFramework("ReproKotlin")
    macosArm64 {
        binaries.framework {
            baseName = "ReproKotlin"
            isStatic = true
            xcf.add(this)
        }
    }

    sourceSets {
        macosArm64Main.dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
        }
    }
}
