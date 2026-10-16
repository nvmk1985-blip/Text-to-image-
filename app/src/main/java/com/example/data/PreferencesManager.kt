package com.example.data

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
  private val prefs: SharedPreferences = context.getSharedPreferences("app_settings_prefs", Context.MODE_PRIVATE)

  companion object {
    private const val KEY_CONTROL_PROMPT = "key_control_prompt"
    private const val KEY_NEGATIVE_PROMPT = "key_negative_prompt"
    private const val KEY_SHIELD_ENABLED = "key_shield_enabled"
    private const val KEY_AI_MODEL = "key_ai_model"
    private const val KEY_LAST_SEED = "key_last_seed"
    private const val KEY_IS_FACE_LOCKED = "key_is_face_locked"
    private const val KEY_LOCKED_FACE_PROMPT = "key_locked_face_prompt"

    // Default control prompt demonstrating user's specific example
    const val DEFAULT_CONTROL_PROMPT = "மனிதனை மரமாக நினைத்துக்கொள். இயற்கை கூறுகள் மற்றும் மனித உடலின் தத்ரூபமான இணைவு. Perfect human anatomy, flawless facial symmetry, strictly two arms and two legs, exactly 5 fingers per hand, natural aligned mouth and teeth."

    const val DEFAULT_NEGATIVE_PROMPT = "crooked mouth, distorted jaw, unnatural teeth, extra limbs, 3 arms, 4 arms, multiple legs, extra fingers, missing fingers, mutated hands, bad anatomy, deformed face, fused fingers, disfigured proportions, blurry artifacts, plastic doll look"
  }

  var controlPrompt: String
    get() = prefs.getString(KEY_CONTROL_PROMPT, DEFAULT_CONTROL_PROMPT) ?: DEFAULT_CONTROL_PROMPT
    set(value) = prefs.edit().putString(KEY_CONTROL_PROMPT, value).apply()

  var negativePrompt: String
    get() = prefs.getString(KEY_NEGATIVE_PROMPT, DEFAULT_NEGATIVE_PROMPT) ?: DEFAULT_NEGATIVE_PROMPT
    set(value) = prefs.edit().putString(KEY_NEGATIVE_PROMPT, value).apply()

  var isAntiDeformityShieldEnabled: Boolean
    get() = prefs.getBoolean(KEY_SHIELD_ENABLED, true)
    set(value) = prefs.edit().putBoolean(KEY_SHIELD_ENABLED, value).apply()

  var aiModel: String
    get() = prefs.getString(KEY_AI_MODEL, "FLUX.1 Realism (Low Moderation)") ?: "FLUX.1 Realism (Low Moderation)"
    set(value) = prefs.edit().putString(KEY_AI_MODEL, value).apply()

  var lastSeed: Long
    get() = prefs.getLong(KEY_LAST_SEED, 424242L)
    set(value) = prefs.edit().putLong(KEY_LAST_SEED, value).apply()

  var isFaceLocked: Boolean
    get() = prefs.getBoolean(KEY_IS_FACE_LOCKED, false)
    set(value) = prefs.edit().putBoolean(KEY_IS_FACE_LOCKED, value).apply()

  var lockedFacePrompt: String
    get() = prefs.getString(KEY_LOCKED_FACE_PROMPT, "") ?: ""
    set(value) = prefs.edit().putString(KEY_LOCKED_FACE_PROMPT, value).apply()

  fun resetControlPrompt() {
    controlPrompt = DEFAULT_CONTROL_PROMPT
  }

  fun resetNegativePrompt() {
    negativePrompt = DEFAULT_NEGATIVE_PROMPT
  }

  fun getCustomOptions(categoryKey: String): List<String> {
    val raw = prefs.getString("custom_options_$categoryKey", "") ?: ""
    if (raw.isBlank()) return emptyList()
    return raw.split("||").filter { it.isNotBlank() }
  }

  fun addCustomOption(categoryKey: String, option: String) {
    val clean = option.trim()
    if (clean.isBlank()) return
    val current = getCustomOptions(categoryKey).toMutableList()
    if (!current.contains(clean)) {
      current.add(clean)
      prefs.edit().putString("custom_options_$categoryKey", current.joinToString("||")).apply()
    }
  }

  fun removeCustomOption(categoryKey: String, option: String) {
    val current = getCustomOptions(categoryKey).toMutableList()
    current.remove(option)
    prefs.edit().putString("custom_options_$categoryKey", current.joinToString("||")).apply()
  }
}
