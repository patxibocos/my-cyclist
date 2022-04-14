import com.google.protobuf.gradle.builtins
import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.application")
    id("kotlin-android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
    id("com.google.protobuf") version "0.8.18"
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.19.4"
    }

    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                id("java") {
                    option("lite")
                }
            }
        }
    }
}

android {
    compileSdk = 31

    defaultConfig {
        applicationId = "io.github.patxibocos.mycyclist"
        minSdk = 21
        targetSdk = 31
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.get()
    }
}

protobuf {

}

tasks.withType<KotlinCompile> {
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
}

dependencies {
    kapt(libs.hilt.compiler)

    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation(libs.accompanist.pager)
    implementation(libs.accompanist.pager.indicators)
    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.android.material)
    implementation(libs.androidx.compose.material.material)
    implementation(libs.androidx.compose.material.material.icons.extended)
    implementation(libs.androidx.compose.ui.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.constraintlayout.compose)
    implementation(libs.androidx.core)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.coil.compose)
    implementation(libs.firebase.config)
    implementation(libs.hilt.library)
    implementation(libs.kotlin.coroutines.play.services)
    implementation(libs.protobuf.javalite)

    coreLibraryDesugaring(libs.android.desugar.jdk)

    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.compose.ui.test)
}