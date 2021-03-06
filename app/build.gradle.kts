plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("androidx.navigation.safeargs.kotlin")
    id("kotlin-parcelize")

    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    compileSdk = Android.compileSdk
    buildToolsVersion = Android.buildTools

    defaultConfig {
        applicationId = "com.entin.lighttasks"
        minSdk = Android.minSdk
        targetSdk = Android.targetSdk
        versionCode = 6
        versionName = "6"
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

    buildTypes {
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        release {
            isMinifyEnabled = false
            isShrinkResources = false
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
        implementation(coreKtx)
        implementation(appcompat)
        implementation(material)
        implementation(constraintlayout)
        implementation(fragment)
    }

    // Hilt
    Dependencies.hilt.apply {
        implementation(mainHilt)
        kapt(compileAndroid)
    }

    // ROOM
    Dependencies.room.apply {
        implementation(runtime)
        kapt(compiler)
        implementation(ktx)
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

    // Gson
    Dependencies.gson.apply {
        implementation(gson)
    }

    // DATA STORE
    Dependencies.dataStore.apply {
        implementation(dataStore)
    }

    // Recyclerview
    Dependencies.recyclerview.apply {
        implementation(recyclerview)
    }

    // Firebase
    Dependencies.firebase.apply {
        implementation(platform(bom))
        implementation(crashlytics)
        implementation(analytics)
        implementation(auth)
        implementation(playServicesAuth)
        implementation(firestore)
        implementation(coroutinesPlayServices)
    }

    // Timber
    Dependencies.timber.apply {
        implementation(timber)
    }

    // No INTERNET Connection
    Dependencies.connection.apply {
        implementation(oops)
    }
}

kapt {
    correctErrorTypes = true
}