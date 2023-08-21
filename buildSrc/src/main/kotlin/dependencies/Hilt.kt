@file:Suppress("unused")

package dependencies

/**
 * Hilt - dependency injection
 */
object Hilt {

    const val mainHilt = "com.google.dagger:hilt-android:${Versions.mainHilt}"

    const val compileAndroid = "com.google.dagger:hilt-android-compiler:${Versions.compileAndroid}"
}