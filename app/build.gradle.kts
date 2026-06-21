import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.serialization)
}

android {
    namespace = "com.therealdeal.kotlift"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.therealdeal.kotlift"
        minSdk = 33
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Injecting the supabase client keys to the project from the local proprieties file
        val localProperties = Properties().apply {
            val localPropertiesFile = rootProject.file("local.properties")
            if (localPropertiesFile.exists()) {
                localPropertiesFile.inputStream().use { load(it) }
            }
        }

        val supabaseUrl = localProperties.getProperty("SUPABASE_URL") ?: "\"\""
        val supabaseAnonKey = localProperties.getProperty("SUPABASE_ANON_KEY") ?: "\"\""

        buildConfigField("String", "SUPABASE_URL", supabaseUrl)
        buildConfigField("String", "SUPABASE_ANON_KEY", supabaseAnonKey)
    }

    // Enable BuildConfig generation
    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx.v1180)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // View Model
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose.android)

    // Data store
    implementation(libs.androidx.datastore.preferences)

    // Gifs and images
    implementation(libs.coil.compose)
    implementation(libs.coil.gif)

    // Charts
    //noinspection UseTomlInstead
    implementation ("io.github.ehsannarmani:compose-charts:0.2.5")

    // Koin
    implementation(libs.koin.androidx.compose)

    // For the navigation
    implementation(libs.androidx.navigation.compose)

    // Object serialization
    implementation(libs.ktor.serialization.kotlinx.json)

    // Db communication and Ktor engine asked by Supabase
    implementation(platform(libs.bom))
    implementation(libs.supabase.postgrest.kt)
    implementation(libs.auth.kt)
    implementation(libs.realtime.kt)
    implementation(libs.storage.kt)
    implementation(libs.ktor.client.android)

    implementation(libs.functions.kt)
    implementation(libs.ktor.client.content.negotiation)

    // Matherial Icons
    implementation(libs.androidx.compose.material.icons.extended)
}