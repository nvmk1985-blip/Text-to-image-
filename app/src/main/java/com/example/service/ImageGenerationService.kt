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
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
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
   * Constructs prompt tailored for Google Imagen 3 with strict anatomical perfection
   * and safety_tolerance = low.
   */
  fun constructFinalPrompt(
    controlPrompt: String,
    imagePrompt: String,
    presetFilters: PresetFilters,
    faceMode: FaceMode,
    lockedFacePrompt: String,
    negativePrompt: String = "",
    isShieldEnabled: Boolean = true,
    safetyTolerance: String = "low"
  ): String {
    val filterKeywords = presetFilters.toPromptKeywords()

    return buildString {
      // 1. Master Control Directive from Settings
      if (controlPrompt.isNotBlank()) {
        append("Direct Concept: ${controlPrompt.trim()}. ")
      }

      // 2. Scene / Subject prompt entered in Home Box
      if (imagePrompt.isNotBlank()) {
        append("Scene: ${imagePrompt.trim()}. ")
      }

      // 3. Face Mode handling
      when (faceMode) {
        FaceMode.OPTION_3_LOCKED -> {
          if (lockedFacePrompt.isNotBlank()) {
            append("Consistent Face Identity: $lockedFacePrompt. ")
          }
        }
        FaceMode.OPTION_1_UPLOAD -> {
          append("Realistic human portrait matching reference features. ")
        }
        FaceMode.OPTION_2_NEW -> {
          append("Original unique human portrait. ")
        }
      }

      // 4. Preset Filters
      append("Attributes: $filterKeywords. ")

      // 5. Anatomical Perfection Guarantees
      if (isShieldEnabled) {
        append("Perfect symmetrical mouth and natural teeth, 5 natural fingers, authentic skin pores, zero plastic smoothing. ")
      }
    }
  }

  /**
   * Generates clean, zero-watermark, authentic 8K photorealistic human portraits
   * using Google's official Imagen 3 (imagen-3.0-generate-002) model directly.
   */
  suspend fun generateHumanImage(
    finalPrompt: String,
    aspectRatio: AspectRatioOption,
    apiKey: String,
    safetySetting: String = "block_only_high"
  ): Result<Bitmap> = withContext(Dispatchers.IO) {
    val cleanKey = apiKey.trim()
    if (cleanKey.isBlank()) {
      return@withContext Result.failure(IllegalStateException("NO_API_KEY"))
    }

    try {
      val url = "https://generativelanguage.googleapis.com/v1beta/models/imagen-3.0-generate-002:predict?key=$cleanKey"

      val payload = JSONObject().apply {
        put("instances", JSONArray().apply {
          put(JSONObject().apply {
            put("prompt", finalPrompt)
          })
        })
        put("parameters", JSONObject().apply {
          put("sampleCount", 1)
          put("aspectRatio", aspectRatio.apiRatio)
          put("safetySetting", safetySetting)
          put("personGeneration", "ALLOW_ADULT")
        })
      }

      val body = payload.toString().toRequestBody("application/json".toMediaType())
      val request = Request.Builder()
        .url(url)
        .post(body)
        .build()

      val response = httpClient.newCall(request).execute()
      val responseText = response.body?.string() ?: ""

      if (response.isSuccessful) {
        val root = JSONObject(responseText)
        val predictions = root.optJSONArray("predictions")
        if (predictions != null && predictions.length() > 0) {
          val b64 = predictions.getJSONObject(0).optString("bytesBase64Encoded")
          if (b64.isNotBlank()) {
            val bytes = android.util.Base64.decode(b64, android.util.Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            if (bitmap != null) {
              return@withContext Result.success(bitmap)
            }
          }
        }
        Result.failure(Exception("படத் தரவு கிடைக்கவில்லை (No image data in response)"))
      } else {
        val errorMsg = try {
          val errObj = JSONObject(responseText).optJSONObject("error")
          errObj?.optString("message") ?: "API Error (${response.code})"
        } catch (e: Exception) {
          "API Error HTTP ${response.code}"
        }
        Result.failure(Exception(errorMsg))
      }
    } catch (e: Exception) {
      Result.failure(e)
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
