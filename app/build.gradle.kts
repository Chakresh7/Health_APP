plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
}

import java.util.Properties

android {
    namespace = "com.calai"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.calai"
        minSdk = 26
        targetSdk = 35
        versionCode = 12
        versionName = "0.4.2"
        buildConfigField("String", "API_BASE_URL", "\"http://10.0.2.2:8000/\"")
        buildConfigField("String", "SUPABASE_URL", "\"${escapeForBuildConfig(localProperty("SUPABASE_URL"))}\"")
        buildConfigField("String", "SUPABASE_ANON_KEY", "\"${escapeForBuildConfig(localProperty("SUPABASE_ANON_KEY"))}\"")
        buildConfigField("String", "GOOGLE_WEB_CLIENT_ID", "\"${escapeForBuildConfig(localProperty("GOOGLE_WEB_CLIENT_ID"))}\"")
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlinOptions {
        jvmTarget = "21"
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2025.01.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.activity:activity-compose:1.10.0")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.navigation:navigation-compose:2.8.5")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("io.coil-kt:coil-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")

    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    implementation("androidx.camera:camera-camera2:1.4.1")
    implementation("androidx.camera:camera-lifecycle:1.4.1")
    implementation("androidx.camera:camera-view:1.4.1")

    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    val supabaseBom = platform("io.github.jan-tennert.supabase:bom:3.1.4")
    implementation(supabaseBom)
    implementation("io.github.jan-tennert.supabase:auth-kt")
    implementation("io.ktor:ktor-client-android:3.0.3")
    implementation("androidx.credentials:credentials:1.5.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.5.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")

    testImplementation("junit:junit:4.13.2")
    debugImplementation("androidx.compose.ui:ui-tooling")
}

fun localProperty(key: String): String {
    val file = rootProject.file("local.properties")
    if (!file.exists()) return ""
    val properties = Properties()
    file.inputStream().use { properties.load(it) }
    return properties.getProperty(key, "").orEmpty()
}

fun escapeForBuildConfig(value: String): String =
    value.replace("\\", "\\\\").replace("\"", "\\\"")

