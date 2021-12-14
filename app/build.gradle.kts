plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("androidx.navigation.safeargs.kotlin")
    id("kotlin-parcelize")
}

android {
    compileSdk = Android.compileSdk
    buildToolsVersion = Android.buildTools

    defaultConfig {
        minSdk = Android.minSdk
        targetSdk = Android.targetSdk
        versionCode = 1
        versionName = "1.0"
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

    defaultConfig {
        applicationId = "com.entin.lighttasks"
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
}

dependencies {

    // Main dependencies of project
    Dependencies.base.apply {
        implementation (coreKtx)
        implementation (appcompat)
        implementation (material)
        implementation (constraintlayout)
    }

    // Hilt
    Dependencies.hilt.apply {
        implementation(mainHilt)
        kapt(compileAndroid)
    }

    // ROOM
    Dependencies.room.apply {
        implementation (runtime)
        kapt (compiler)
        implementation (ktx)
    }

    // NAVIGATION COMPONENT
    Dependencies.navigation.apply {
        implementation(mainNavigation)
        implementation(ui)
    }

    // Lifecycle + ViewModel & LiveData
    Dependencies.lifecycle.apply {
        implementation(lifecycle)
        implementation(liveData)
        implementation(viewModel)
    }

    // ViewBinding delegate [No REFLECTION] - Kirill Rozov
    Dependencies.viewBindingDelegate.apply {
        implementation(main)
    }

    // Gson
    Dependencies.gson.apply {
        implementation (gson)
    }

    // DATA STORE
    implementation ("androidx.datastore:datastore-preferences:1.0.0")

    // Recyclerview
    implementation ("androidx.recyclerview:recyclerview:1.2.1")
}

kapt {
    correctErrorTypes = true
}