package com.entin.lighttasks.presentation.util

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.camera.core.ImageProxy
import coil.request.Disposable
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageCache @Inject constructor(
    private val application: Application,
) {

    /**
     * Save photo to internal store
     */
    fun saveImage(
        imageName: String,
        image: ImageProxy,
        dismiss: () -> Unit,
        setNameToTask: (String) -> Unit,
    ) {
        val buffer = image.planes[ZERO].buffer
        val bytes = ByteArray(buffer.remaining()).apply {
            buffer.get(this)
        }
        try {
            val output = getFile(imageName)
            FileOutputStream(output).use {
                it.write(bytes)
                setNameToTask(imageName)
            }
        } catch (exc: IOException) {
            Log.e("Global", "Can't save image: ${exc.message}", exc)
        }

        dismiss()
    }

    /**
     * Delete photo from internal store
     */
    fun deleteImage(name: String) {
        val fileToDelete = getFile(name)
        if (fileToDelete.exists()) {
            fileToDelete.delete()
        }
    }

    /**
     * Delete all unused photos from internal store
     */
    fun deleteUnusedPhotos(listPhotoNames: List<String>) {
        val allPhotos = getImageFileDir().listFiles()
        ?.filter {
            it.canRead() && it.isFile && it.name.startsWith(IMG_PREFIX)
        }?.map {
            it.name
        } ?: listOf()

        allPhotos.subtract(listPhotoNames.toSet()).toList().forEach {
            deleteImage(it)
        }
    }

    /**
     * Coil - show photo
     */
    fun showPhoto(name: String, setPhotoView: (Uri) -> Disposable) =
        setPhotoView(Uri.fromFile(getFile(name)))

    /**
     * Image name
     */
    fun generateName() = "$IMG_PREFIX${
        SimpleDateFormat(DATE_FORMAT_NAME, Locale.US).format(System.currentTimeMillis())
    }$FORMAT_JPG"

    /**
     * Get photo file procedure
     */
    private fun getFile(imageName: String): File = File(getImageFileDir(), imageName)

    /**
     * Default application internal storage folder
     */
    private fun getImageFileDir(): File = application.applicationContext.filesDir
}
