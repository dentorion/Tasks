buildscript {
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:${Android.gradleVersion}")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Android.kotlinVersion}")

        // HILT
        classpath("com.google.dagger:hilt-android-gradle-plugin:${Android.hiltVersion}")

        // Safe Args
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:${Android.navigation}")

        // Firebase
        classpath ("com.google.firebase:firebase-crashlytics-gradle:${Android.firebaseCrashlyticsGradle}")
        classpath("com.google.gms:google-services:${Android.googleServices}")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}