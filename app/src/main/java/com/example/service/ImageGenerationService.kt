package com.example.service

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.example.model.AspectRatioOption
import com.example.model.FaceMode
import com.example.model.PresetFilters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class ImageGenerationService(private val context: Context) {

  private val httpClient = OkHttpClient.Builder()
    .connectTimeout(60, TimeUnit.SECONDS)
    .readTimeout(60, TimeUnit.SECONDS)
    .build()

  /**
   * Combines Settings Master Control Prompt + Home Image Prompt + Human Preset Filters.
   */
  fun constructFinalPrompt(
    controlPrompt: String,
    imagePrompt: String,
    presetFilters: PresetFilters,
    faceMode: FaceMode,
    lockedFacePrompt: String,
    negativePrompt: String = "",
    isShieldEnabled: Boolean = true
  ): String {
    val filterKeywords = presetFilters.toPromptKeywords()

    return buildString {
      // 1. Master Control Directive from Settings
      if (controlPrompt.isNotBlank()) {
        append("[Master AI Directive: ${controlPrompt.trim()}]. ")
      }

      // 2. Scene / Subject prompt entered in Home Box
      if (imagePrompt.isNotBlank()) {
        append("[Scene Description: ${imagePrompt.trim()}]. ")
      }

      // 3. Face Mode handling
      when (faceMode) {
        FaceMode.OPTION_3_LOCKED -> {
          if (lockedFacePrompt.isNotBlank()) {
            append("[Face Identity Lock: $lockedFacePrompt]. ")
          }
          append("Ensure exact same face identity and structure as reference. ")
        }
        FaceMode.OPTION_1_UPLOAD -> {
          append("Base the human facial features, eyes, and skin tone on the user provided face reference photo. ")
        }
        FaceMode.OPTION_2_NEW -> {
          append("Generate a completely unique and original human face structure. ")
        }
      }

      // 4. Preset Filters
      append("[Physical Attributes: $filterKeywords]. ")

      // 5. Anatomical Perfection & Anti-Deformity Shield
      if (isShieldEnabled) {
        append("Strict anatomical perfection: flawless facial symmetry, natural aligned mouth and teeth, exactly two arms, exactly two legs, authentic hands with exactly 5 fingers per hand, realistic natural human body proportions. ")
        if (negativePrompt.isNotBlank()) {
          append("[Negative Prompt - Avoid completely: ${negativePrompt.trim()}]. ")
        }
      }

      // 6. Quality & Photorealism
      append("Cinematic lighting, 8k resolution, authentic photorealism, detailed natural human skin texture, masterpiece.")
    }
  }

  fun generateImageUrl(
    finalPrompt: String,
    seed: Long,
    aspectRatio: AspectRatioOption
  ): String {
    val encoded = URLEncoder.encode(finalPrompt, StandardCharsets.UTF_8.toString())
    // Using Flux model on Pollinations
    return "https://image.pollinations.ai/prompt/$encoded?model=flux-realism&seed=$seed&width=${aspectRatio.width}&height=${aspectRatio.height}&nologo=true"
  }

  suspend fun downloadBitmap(imageUrl: String): Bitmap? = withContext(Dispatchers.IO) {
    try {
      val request = Request.Builder().url(imageUrl).build()
      val response = httpClient.newCall(request).execute()
      if (response.isSuccessful) {
        val inputStream = response.body?.byteStream()
        inputStream?.let { BitmapFactory.decodeStream(it) }
      } else {
        null
      }
    } catch (e: Exception) {
      e.printStackTrace()
      null
    }
  }

  suspend fun saveImageToGallery(bitmap: Bitmap, title: String): Result<String> = withContext(Dispatchers.IO) {
    try {
      val filename = "AI_Human_${System.currentTimeMillis()}.jpg"
      var outputStream: OutputStream? = null
      var imageUri: Uri? = null

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val contentValues = ContentValues().apply {
          put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
          put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
          put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/TextToImage")
        }
        val resolver = context.contentResolver
        imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        imageUri?.let { uri ->
          outputStream = resolver.openOutputStream(uri)
        }
      } else {
        val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/TextToImage"
        val file = File(imagesDir)
        if (!file.exists()) {
          file.mkdirs()
        }
        val imageFile = File(imagesDir, filename)
        outputStream = FileOutputStream(imageFile)
        imageUri = Uri.fromFile(imageFile)
      }

      outputStream?.use { stream ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 95, stream)
      }

      Result.success("படம் கேலரியில் வெற்றிகரமாக சேமிக்கப்பட்டது (Saved to Gallery)!")
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  suspend fun shareImage(bitmap: Bitmap): Boolean = withContext(Dispatchers.IO) {
    try {
      val cachePath = File(context.cacheDir, "images")
      cachePath.mkdirs()
      val file = File(cachePath, "shared_ai_human.jpg")
      val stream = FileOutputStream(file)
      bitmap.compress(Bitmap.CompressFormat.JPEG, 95, stream)
      stream.close()

      val contentUri: Uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
      )

      val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        setDataAndType(contentUri, context.contentResolver.getType(contentUri))
        putExtra(Intent.EXTRA_STREAM, contentUri)
        type = "image/jpeg"
      }
      val chooser = Intent.createChooser(shareIntent, "Share AI Image")
      chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      context.startActivity(chooser)
      true
    } catch (e: Exception) {
      e.printStackTrace()
      false
    }
  }
}
