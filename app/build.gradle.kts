plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "upvictoria.pm_may_ago_2025.iti_271415.pi1u1.notion"
    compileSdk = 35

    defaultConfig {
        applicationId = "upvictoria.pm_may_ago_2025.iti_271415.pi1u1.notion"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    // Markwon para renderizado de Markdown en TextView/EditText
    implementation ("io.noties.markwon:core:4.6.2")
    implementation ("io.noties.markwon:editor:4.6.2")

    // Soporte para sintaxis adicional (opcional)
    implementation ("io.noties.markwon:ext-strikethrough:4.6.2")  // ~~tachado~~
    implementation ("io.noties.markwon:ext-tables:4.6.2")         // tablas
    implementation ("io.noties.markwon:ext-tasklist:4.6.2")      // - [x] tareas
    implementation ("io.noties.markwon:html:4.6.2")              // soporte HTML básico
}