val mongoDbPassword: String? = project.findProperty("MONGO_DB_PASSWORD") as String?

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.appfranceassossante"
    compileSdk = 35

    buildFeatures {
        buildConfig = true  // Active la fonctionnalité BuildConfig
    }

    defaultConfig {
        applicationId = "com.example.appfranceassossante"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        // Ajout de MONGO_DB_PASSWORD à BuildConfig
        buildConfigField("String", "MONGO_DB_PASSWORD", "\"${project.findProperty("MONGO_DB_PASSWORD") ?: ""}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        getByName("release") {
            buildConfigField("String", "MONGO_DB_PASSWORD", "\"${project.findProperty("MONGO_DB_PASSWORD")}\"")
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/native-image/reflect-config.json"
            excludes += "META-INF/native-image/native-image.properties"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.common.jvm)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    //implementation(libs.mongodb.driver.kotlin)
    implementation(libs.mongodb.driver.sync)

    implementation(libs.mongodb.driver.kotlin.coroutine)
    implementation(libs.slf4j.simple)
    //couroutines
    implementation(libs.kotlinx.coroutines.android)
    // implementation(libs.mongodb.driver.kotlin) // pour utiliser mongodb

    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    //pour les img
    implementation(libs.glide)
    annotationProcessor(libs.compiler)
    //mongodb realm
    implementation(libs.realm.android.library)

}
