package com.kompsia.sketchcolor.export

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object GalleryExporter {

    enum class ImageFormat(val displayName: String, val mimeType: String, val extension: String, val compressFormat: Bitmap.CompressFormat) {
        PNG("PNG", "image/png", "png", Bitmap.CompressFormat.PNG),
        JPEG("JPEG", "image/jpeg", "jpg", Bitmap.CompressFormat.JPEG)
    }

    fun saveToGallery(context: Context, bitmap: Bitmap, format: ImageFormat): Uri? {
        val time = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val fileName = "SketchColor_$time.${format.extension}"
        val resolver = context.contentResolver
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, format.mimeType)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/SketchColor Studio")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values) ?: return null
        resolver.openOutputStream(uri)?.use { out ->
            bitmap.compress(format.compressFormat, if (format == ImageFormat.PNG) 100 else 96, out)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.clear()
            values.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(uri, values, null, null)
        }
        return uri
    }
}
