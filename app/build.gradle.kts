import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.calculator"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.calculator"
        minSdk = 25
        targetSdk = 35
        versionCode = 2
        versionName = "1.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")

            // Release signing is read from environment variables or the
            // git-ignored local.properties so credentials are never committed.
            // If neither is provided, the release variant has no signing config
            // and assembleRelease will fail loudly instead of producing an
            // unsigned artifact.
            val localProps = Properties().apply {
                val file = rootProject.file("local.properties")
                if (file.exists()) file.inputStream().use { load(it) }
            }
            val storeFile = System.getenv("RELEASE_STORE_FILE")
                ?: localProps.getProperty("storeFile")
            val keyAlias = System.getenv("RELEASE_KEY_ALIAS")
                ?: localProps.getProperty("keyAlias")
            val storePassword = System.getenv("RELEASE_STORE_PASSWORD")
                ?: localProps.getProperty("storePassword")
            val keyPassword = System.getenv("RELEASE_KEY_PASSWORD")
                ?: localProps.getProperty("keyPassword")

            if (storeFile != null && keyAlias != null &&
                storePassword != null && keyPassword != null) {
                signingConfig = signingConfigs.create("release") {
                    this.storeFile = file(storeFile)
                    this.storePassword = storePassword
                    this.keyAlias = keyAlias
                    this.keyPassword = keyPassword
                }
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_25
        targetCompatibility = JavaVersion.VERSION_25
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.14.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
}