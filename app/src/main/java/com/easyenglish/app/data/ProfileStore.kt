package com.easyenglish.app.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import org.json.JSONObject
import java.io.File

data class UserProfile(
    val name: String = "",
    val ageRange: String = "Prefiero no indicar",
    val priorStudy: String = "Un poco",
    val goal: String = "Interés personal",
    val photoPath: String = "",
    val level: String = "A0",
    val saved: Boolean = false,
    val placementSkipped: Boolean = false
)

/** El perfil permanece local; nunca se envía al catálogo de descargas. */
class ProfileStore(private val context: Context) {
    private val prefs = context.getSharedPreferences("easy_english_local_profile", Context.MODE_PRIVATE)
    fun load(): UserProfile = try {
        val o = JSONObject(prefs.getString("profile", "{}") ?: "{}")
        UserProfile(
            name = o.optString("name", ""),
            ageRange = o.optString("ageRange", "Prefiero no indicar"),
            priorStudy = o.optString("priorStudy", "Un poco"),
            goal = o.optString("goal", "Interés personal"),
            photoPath = o.optString("photoPath", ""),
            level = o.optString("level", "A0"),
            saved = o.optBoolean("saved", false),
            placementSkipped = o.optBoolean("placementSkipped", false)
        )
    } catch (_: Exception) { UserProfile() }

    fun save(profile: UserProfile) {
        val o = JSONObject()
            .put("name", profile.name)
            .put("ageRange", profile.ageRange)
            .put("priorStudy", profile.priorStudy)
            .put("goal", profile.goal)
            .put("photoPath", profile.photoPath)
            .put("level", profile.level)
            .put("saved", profile.saved)
            .put("placementSkipped", profile.placementSkipped)
        prefs.edit().putString("profile", o.toString()).apply()
    }
    fun setReduceMotion(value: Boolean) { prefs.edit().putBoolean("reduceMotion", value).apply() }
    fun reduceMotion() = prefs.getBoolean("reduceMotion", false)
    fun setEnableAudio(value: Boolean) { prefs.edit().putBoolean("enableAudio", value).apply() }
    fun enableAudio() = prefs.getBoolean("enableAudio", true)

    /** Copia la imagen seleccionada a PNG privado y limita el tamaño para evitar cargas excesivas. */
    fun copyPhotoAsPng(uri: Uri): String? {
        return try {
            val stream = context.contentResolver.openInputStream(uri) ?: return null
            val buffer = java.io.ByteArrayOutputStream()
            stream.use { input ->
                val block = ByteArray(8192)
                while (true) {
                    val count = input.read(block)
                    if (count < 0) break
                    if (buffer.size() + count > 8 * 1024 * 1024) return null
                    buffer.write(block, 0, count)
                }
            }
            val data = buffer.toByteArray()
            val original = BitmapFactory.decodeByteArray(data, 0, data.size) ?: return null
            val factor = minOf(1f, 512f / maxOf(original.width, original.height))
            val png = if (factor >= 1f) original else Bitmap.createScaledBitmap(
                original,
                (original.width * factor).toInt().coerceAtLeast(1),
                (original.height * factor).toInt().coerceAtLeast(1),
                true
            )
            val destination = File(context.filesDir, "avatar_" + System.currentTimeMillis() + ".png")
            destination.outputStream().use {
                if (!png.compress(Bitmap.CompressFormat.PNG, 100, it)) return null
            }
            if (png !== original) png.recycle()
            original.recycle()
            destination.absolutePath
        } catch (_: Exception) { null }
    }
}
