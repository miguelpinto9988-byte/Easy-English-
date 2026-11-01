package com.easyenglish.app.ui.study

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.easyenglish.app.data.StudyLesson

/**
 * Reads real, horizontal educational PNGs packed in the Android assets.
 * One source file equals one slide; no PDF, Canvas or generated raster substitute.
 *
 * Paths: assets/presentations/a0/a0-01/slide_01.png ... slide_08.png
 * Lessons without a complete eight-PNG bundle retain the existing dynamic viewer.
 * The fox mascot / launcher resources are deliberately outside this module.
 */
object BundledTheorySlides {
    const val COUNT = 8
    private val supportedIds = (1..6).map { "a0-" + it.toString().padStart(2, '0') }.toSet() + "a0-08"

    fun folder(lesson: StudyLesson): String =
        "presentations/" + lesson.level.lowercase() + "/" + lesson.id

    fun hasCompleteBundle(context: Context, lesson: StudyLesson): Boolean {
        if (lesson.id !in supportedIds) return false
        return try {
            val entries = context.assets.list(folder(lesson)).orEmpty().toSet()
            (1..COUNT).all { i -> slideName(i) in entries }
        } catch (_: Exception) {
            false
        }
    }

    private fun slideName(index: Int): String =
        "slide_" + index.toString().padStart(2, '0') + ".png"

    fun read(context: Context, lesson: StudyLesson, zeroBasedIndex: Int): Bitmap {
        require(zeroBasedIndex in 0 until COUNT)
        require(hasCompleteBundle(context, lesson)) {
            "La unidad " + lesson.id + " no contiene sus ocho PNG."
        }
        return context.assets.open(folder(lesson) + "/" + slideName(zeroBasedIndex + 1)).use { stream ->
            val options = BitmapFactory.Options().apply { inScaled = false }
            val image = BitmapFactory.decodeStream(stream, null, options)
                ?: error("No se pudo leer la diapositiva " + (zeroBasedIndex + 1))
            check(image.width == 1920 && image.height == 1080) {
                "La diapositiva no tiene formato horizontal 1920 × 1080."
            }
            image
        }
    }
}
