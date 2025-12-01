plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp) // Plugin para Room
}

android {
    namespace = "com.huertohogar.huertohogarmovil" // Namespace Correcto
    compileSdk = 36

    defaultConfig {
        applicationId = "com.huertohogar.huertohogarmovil"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "com.huertohogar.huertohogarmovil.HuertoTestRunner" // Usamos el Runner Custom

        /*testInstrumentationRunnerArguments += mapOf(
            "clearPackageData" to "true",
            "useTestStorageService" to "true"
        )*/

        vectorDrawables {
            useSupportLibrary = true
        }
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

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/LICENSE.md"
            excludes += "/META-INF/LICENSE-notice.md"
        }
    }

    // --- AJUSTE CLAVE PARA JUNIT 5 ---
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
            all {
                it.useJUnitPlatform()
            }
        }
    }
    // ------------------------------------
}

dependencies {
    implementation(libs.play.services.maps)
    // ELIMINADA: implementation(libs.play.services.maps) - Conflicto

    // Compose BOM
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // Core Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose UI
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)

    // Lifecycle & Navigation
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Coil
    implementation(libs.coil.compose)
    implementation(libs.coil.gif)

    // Accompanist & Location
    implementation("com.google.accompanist:accompanist-permissions:0.34.0")
    // Versión actualizada para intentar evitar el error 16KB
    implementation("com.google.android.gms:play-services-location:21.2.0")

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    //DataStore
    implementation("androidx.datastore:datastore-preferences:1.1.1") // Fuente del error 16KB

    //Loggin
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // --- MAPAS: OPENSTREETMAP (Estable y Nativo) ---
    implementation("org.osmdroid:osmdroid-android:6.1.18")

    // ============ TESTING ============

    // JUnit 5 (BOM y Motor Principal)
    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    // Unit Tests (Local) - MockK, Coroutines Test
    testImplementation(libs.mockk)
    testImplementation(libs.kotest.assertions)
    testImplementation(libs.kotlinx.coroutines.test)

    // Retrofit y Gson (Para testing de API)
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

    // Android Instrumented Tests
    androidTestImplementation("androidx.test:core:1.6.1")
    androidTestImplementation("androidx.test:runner:1.6.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation("androidx.test.uiautomator:uiautomator:2.3.0")

    // Compose Testing
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // [Línea conflictiva testImplementation(kotlin("test")) ELIMINADA]
}