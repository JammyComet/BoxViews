plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    alias(libs.plugins.ksp) // agregar
    alias(libs.plugins.room) // aplicar aquí el alias del plugin

}

android {
    namespace = "com.example.boxview"
    compileSdk = 36
    defaultConfig {
        applicationId = "com.example.boxview"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

    }

    buildTypes {
        release {
            // La optimización de AGP 9 no existe en AGP 8.10.
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {

    // Navegación del ejemplo MyApplication002_FIN
    implementation("androidx.navigation:navigation-compose:2.9.5")
    // Solo para el requisito de cámara y lectura de códigos
    implementation("androidx.camera:camera-camera2:1.4.2")
    implementation("androidx.camera:camera-lifecycle:1.4.2")
    implementation("androidx.camera:camera-view:1.4.2")
    implementation("com.google.mlkit:barcode-scanning:17.3.0")
    implementation("com.google.zxing:core:3.5.3") // Crear QR de cajas para impresión/lectura
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.9.4")

    implementation(libs.room.runtime)
    implementation(libs.room.ktx) // Soporte para Corrutinas y Flow con Room
    ksp(libs.room.compiler)       // Procesador de anotaciones usando KSP
    // --- VIEWMODEL PARA COMPOSE ---
    implementation(libs.compose.viewmodel) // Permite usar 'viewModel()' en Compose
    // --- ICONOS EXTENDIDOS DE MATERIAL ---
    implementation(libs.compose.icons.extended)


    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    debugImplementation(libs.androidx.compose.ui.tooling)
}