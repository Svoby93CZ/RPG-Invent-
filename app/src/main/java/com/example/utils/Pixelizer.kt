package com.example.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream

object Pixelizer {

    /**
     * Shrinks a bitmap to a target blocky resolution
     * using nearest-neighbor interpolation (filter = false) to produce true RPG retro pixel art.
     * To maximize both pixel art blockiness and detailed fidelity, we downscale to a 32x32 retro grid,
     * then upscale back to 128x128 with crisp nearest-neighbor filtering.
     */
    fun pixelize(src: Bitmap, size: Int = 128): Bitmap {
        val width = src.width
        val height = src.height
        val minDim = Math.min(width, height)
        
        // Center crop the bitmap first to keep square ratio for the slot icons
        val cropX = if (width > minDim) (width - minDim) / 2 else 0
        val cropY = if (height > minDim) (height - minDim) / 2 else 0
        val cropped = Bitmap.createBitmap(src, cropX, cropY, minDim, minDim)
        
        // 32x32 provides the perfect balance of medieval RPG classic look and high detailed resolution
        val retroGridRes = if (size in 16..48) size else 32
        val shrunk = Bitmap.createScaledBitmap(cropped, retroGridRes, retroGridRes, false)
        
        // Upscale back to 128x128 using nearest-neighbor (filter = false) to produce sharp, solid square blocks
        return Bitmap.createScaledBitmap(shrunk, 128, 128, false)
    }

    /**
     * Compress bitmap to PNG byte array and encode as Base64 string for database storage.
     */
    fun toBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    /**
     * Translate base64 string back to loadable Android Bitmap.
     */
    fun fromBase64(base64Str: String): Bitmap? {
        return try {
            val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            null
        }
    }
}
