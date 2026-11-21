plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kover)
}

android {
    namespace = "cl.duoc.Bitacora"
    compileSdk = 37

    defaultConfig {
        applicationId = "cl.duoc.Bitacora"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
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
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.4")
    implementation("androidx.room:room-runtime:2.8.4")
    implementation("androidx.room:room-ktx:2.8.4")
    implementation("io.coil-kt.coil3:coil-compose:3.3.0")
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
    ksp("androidx.room:room-compiler:2.8.4")
}

kover {
    reports {
        filters {
            includes {
                classes(
                    "cl.duoc.consumiendoapi.ui.form.ClienteValidator*",
                    "cl.duoc.consumiendoapi.ui.form.ClienteFormViewModel*",
                    "cl.duoc.consumiendoapi.ui.clientes.ClientesViewModel*",
                    "cl.duoc.consumiendoapi.data.repository.ClientesRepository*",
                    "cl.duoc.consumiendoapi.data.repository.ApiErrorMapper*",
                    "cl.duoc.consumiendoapi.data.usecase.ClientesUseCases*",
                    "cl.duoc.consumiendoapi.domain.usecase.CalcularEstadisticasUseCase*"
                )
            }
        }
    }
}
