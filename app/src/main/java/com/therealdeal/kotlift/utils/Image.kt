package com.therealdeal.kotlift.utils

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.SystemClock
import android.provider.MediaStore
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException
import kotlin.math.min

@SuppressLint("ObsoleteSdkInt")
private fun uriToBitmap(imageUri: Uri, contentResolver: ContentResolver): Bitmap {
    return when {
        Build.VERSION.SDK_INT < 28 -> {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
        }
        else -> {
            val source = ImageDecoder.createSource(contentResolver, imageUri)
            ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                decoder.isMutableRequired = true
            }
        }
    }
}

/**
 * Downscales a bitmap so its longest side is at most [maxDimension],
 * preserving aspect ratio. A profile picture never needs to be full camera resolution.
 */
private fun Bitmap.downscale(maxDimension: Int = 1024): Bitmap {
    val largestSide = maxOf(width, height)
    if (largestSide <= maxDimension) return this

    val scale = maxDimension.toFloat() / largestSide
    val newWidth = (width * scale).toInt()
    val newHeight = (height * scale).toInt()
    return Bitmap.createScaledBitmap(this, newWidth, newHeight, true)
}

suspend fun saveImageToStorage(
    imageUri: Uri,
    contentResolver: ContentResolver,
    name: String = "IMG_${SystemClock.uptimeMillis()}"
): Uri = withContext(Dispatchers.IO) {
    val bitmap = uriToBitmap(imageUri, contentResolver).downscale(maxDimension = 1024)

    val values = ContentValues().apply {
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        put(MediaStore.Images.Media.DISPLAY_NAME, name)
    }

    val savedImageUri =
        contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    val outputStream = savedImageUri?.let { contentResolver.openOutputStream(it) }
        ?: throw FileNotFoundException()

    outputStream.use {
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, it)
    }
    bitmap.recycle()

    savedImageUri
}

suspend fun deleteImageFromStorage(context: Context, uri: Uri) = withContext(Dispatchers.IO) {
    try {
        when (uri.scheme) {
            ContentResolver.SCHEME_CONTENT -> {
                context.contentResolver.delete(uri, null, null)
            }
            ContentResolver.SCHEME_FILE -> {
                val file = File(uri.path ?: return@withContext)
                if (file.exists()) {
                    file.delete()
                }
            }
        }
    } catch (e: Exception) {
        Log.e("ImageUtils", "Failed to delete image: ${e.message}", e)
    }
}