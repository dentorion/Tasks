plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("androidx.navigation.safeargs")
    id("com.google.dagger.hilt.android")
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")

}

android {
    namespace = "com.entin.lighttasks"
    compileSdk = Android.compileSdk
    buildToolsVersion = Android.buildTools

    defaultConfig {
        applicationId = "com.entin.lighttasks"
        minSdk = Android.minSdk
        targetSdk = Android.targetSdk
        versionCode = 25
        versionName = "Update target SDK 34"
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            signingConfig = signingConfigs.getByName("debug")
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
        viewBinding = true
        buildConfig = true
    }
    bundle {
        language {
            // Specifies that the app bundle should not support
            // configuration APKs for language resources. These
            // resources are instead packaged with each base and
            // dynamic feature APK.
            enableSplit = false
        }
    }
}

dependencies {

    // Main dependencies of project
    Dependencies.base.apply {
        implementation(coreKtx)
        implementation(appcompat)
        implementation(constraintlayout)
        implementation(material)
    }

    // Hilt
    Dependencies.hilt.apply {
        implementation(mainHilt)
        kapt(compileAndroid)
    }

    // ROOM
    Dependencies.room.apply {
        implementation(runtime)
        implementation(ktx)
        kapt(compiler)
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
        implementation(gson)
    }

    // Camera
    Dependencies.camera.apply {
        implementation(core)
        implementation(camera2)
        implementation(lifecycle)
        implementation(view)
        implementation(extensions)
    }

    // DATA STORE
    Dependencies.dataStore.apply {
        implementation(dataStore)
    }

    // Recyclerview
    Dependencies.recyclerview.apply {
        implementation(recyclerview)
    }

    // Coil images
    Dependencies.coil.apply {
        api(coil)
    }

    // Retrofit
    Dependencies.retrofit.apply {
        implementation(retrofit)
        implementation(gson)
        implementation(loggingInterceptor)
    }

    // LeakCanary
    Dependencies.leakCanary.apply {
        debugImplementation(canary)
    }

    // Hyperion
    Dependencies.hyperion.apply {
        debugImplementation (core)
        debugImplementation (buildConfig)
        debugImplementation (crash)
        debugImplementation (disk)
        debugImplementation (geigerCounter)
        debugImplementation (measurement)
        debugImplementation (phoenix)
        debugImplementation (recorder)
        debugImplementation (sharedPreferences)
        debugImplementation (timber)
    }

    // Crashlytics
    implementation(platform("com.google.firebase:firebase-bom:32.5.0"))
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-analytics")

}

// Allow references to generated code
kapt {
    correctErrorTypes = true
}

hilt {
    enableAggregatingTask = true
}
