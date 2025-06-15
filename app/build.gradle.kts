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

    implementation ("androidx.appcompat:appcompat:1.4.0")
    implementation ("com.google.android.material:material:1.4.0")
    implementation ("androidx.constraintlayout:constraintlayout:2.1.2")
    implementation ("androidx.recyclerview:recyclerview:1.2.1")
    implementation ("androidx.cardview:cardview:1.0.0")

// Markwon
    implementation ("io.noties.markwon:core:4.6.2")
    implementation ("io.noties.markwon:editor:4.6.2")
    implementation ("io.noties.markwon:ext-strikethrough:4.6.2")
    implementation ("io.noties.markwon:ext-tasklist:4.6.2")

// Para el drawer
    implementation ("com.google.android.material:material:1.4.0")

    testImplementation ("junit:junit:4.13.2")
    androidTestImplementation ("androidx.test.ext:junit:1.1.3")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.4.0")
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
    implementation ("com.google.android.material:material:1.6.0")
    implementation ("androidx.appcompat:appcompat:1.4.0")
    implementation ("com.google.android.material:material:1.6.0")
    implementation ("io.noties.markwon:core:4.6.2")
}