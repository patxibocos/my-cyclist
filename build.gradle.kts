import com.diffplug.gradle.spotless.SpotlessExtension

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(libs.android.plugin.gradle)
        classpath(libs.kotlin.plugin.gradle)
        classpath(libs.hilt.plugin.gradle)
        classpath(libs.google.services)
    }
}

plugins {
    alias(libs.plugins.spotless) apply false
}

subprojects {
    apply(plugin = rootProject.libs.plugins.spotless.get().pluginId)
    configure<SpotlessExtension> {
        kotlin {
            target("**/*.kt")
            targetExclude("$buildDir/**/*.kt", "bin/**/*.kt", "**/protobuf/**/*.kt")
            ktlint(libs.versions.ktlint.get())
        }

        kotlinGradle {
            target("*.gradle.kts")
            ktlint(libs.versions.ktlint.get())
        }
        // https://github.com/diffplug/spotless/issues/1644
        lineEndings = com.diffplug.spotless.LineEnding.PLATFORM_NATIVE
    }
}