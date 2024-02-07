plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")

    // KSP
    id("com.google.devtools.ksp")
    id("androidx.room")

    // Navigation
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.minizuure.todoplannereducationedition"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.minizuure.todoplannereducationedition"
        minSdk = 26
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
    }

    room {
        schemaDirectory("$projectDir/schemas")
    }

    sourceSets {
        // Adds exported schema location as test app assets.
        // follow this instruction: https://developer.android.com/training/data-storage/room/migrating-db-versions#kotlin_1
        // TODO: build test case
        getByName("androidTest").assets.srcDirs("$projectDir/schemas")
    }


    packaging {
        resources{
            excludes.add("**/LICENSE.md")
            excludes.add("**/LICENSE-notice.md")
        }

    }

}

dependencies {
    // Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.6")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.6")

    // Room
    implementation("androidx.room:room-ktx:2.6.1")
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.annotation:annotation:1.7.1")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    androidTestImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    ksp("androidx.room:room-compiler:2.6.1")

    //firebase
    implementation("com.google.firebase:firebase-firestore:24.10.1")
    implementation("com.google.firebase:firebase-auth-ktx:22.3.1")
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    // Security
    implementation("androidx.security:security-crypto:1.0.0")

    // Testing Unit
    implementation("androidx.test:core-ktx:1.5.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    testImplementation("androidx.room:room-testing:2.6.1")

    // Flexbox
    implementation("com.google.android.flexbox:flexbox:3.0.0")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}