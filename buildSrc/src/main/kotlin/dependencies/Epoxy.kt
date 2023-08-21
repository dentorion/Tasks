@file:Suppress("unused")

package dependencies

object Epoxy {

    /**
     * Epoxy Recycler View
     */
    const val androidEpoxy = "com.airbnb.android:epoxy:${Versions.epoxy}"

    /** Add the annotation processor if you are using Epoxy's annotations (recommended) **/
    const val epoxyProcessor = "com.airbnb.android:epoxy-processor:${Versions.epoxy}"
}