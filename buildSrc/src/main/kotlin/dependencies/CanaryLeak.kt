@file:Suppress("unused")

package dependencies

/**
 * Canary Leak
 */
object CanaryLeak {

    const val canary = "com.squareup.leakcanary:leakcanary-android:${Versions.canary}"
}