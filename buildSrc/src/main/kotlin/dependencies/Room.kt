@file:Suppress("unused")

package dependencies

object Room {

    /**
     * [Android Room Runtime](https://mvnrepository.com/artifact/androidx.room/room-runtime)
     */
    const val runtime = "androidx.room:room-runtime:${Versions.room}"

    /**
     * [Android Room Compiler](https://mvnrepository.com/artifact/androidx.room/room-compiler)
     */
    const val compiler = "androidx.room:room-compiler:${Versions.room}"

    /**
     * [Android Room Kotlin Extensions](https://mvnrepository.com/artifact/androidx.room/room-ktx)
     */
    const val ktx = "androidx.room:room-ktx:${Versions.room}"

    /**
     * Optional - Paging 3 Integration
     */
    const val paging3 = "androidx.room:room-paging:${Versions.room}"
}
