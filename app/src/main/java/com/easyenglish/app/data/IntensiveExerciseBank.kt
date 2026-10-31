package com.easyenglish.app.data

import kotlin.random.Random

/**
 * Amplia cada sesión intensiva de ocho a dieciséis ejercicios realizables.
 *
 * Los ocho primeros son los reactivos originales de la unidad. Los ocho
 * adicionales son ejercicios de traducción basados en pares bilingües del
 * currículo: cuatro de esta unidad y cuatro de repaso transversal del nivel.
 * Esto aumenta la práctica, no el número de frases originales ni constituye
 * una validación pedagógica externa.
 */
object IntensiveExerciseBank {
    const val EXTRA_PER_SESSION = 8

    fun build(
        lesson: StudyLesson,
        session: IntensiveSession,
        sessionIndex: Int,
        levelBank: List<StudyLine>
    ): List<IntensiveTask> {
        val local = lesson.examples.distinctBy { it.english to it.spanish }
        val localEnglish = local.map { it.english }.toSet()
        val review = levelBank.distinctBy { it.english to it.spanish }
            .filter { it.english !in localEnglish }
        require(local.isNotEmpty() && review.isNotEmpty()) {
            "Se necesitan modelos bilingües para ampliar la sesión."
        }

        val extra = (0 until EXTRA_PER_SESSION).map { i ->
            val isLocal = i < 4
            val pair = if (isLocal) {
                local[(sessionIndex + i) % local.size]
            } else {
                review[(lesson.number * 4 + sessionIndex * 4 + i - 4) % review.size]
            }
            val toSpanish = (sessionIndex + i) % 2 == 0
            val answer = if (toSpanish) pair.spanish else pair.english
            val distractors = levelBank.asSequence()
                .map { if (toSpanish) it.spanish else it.english }
                .filter { it.isNotBlank() && it != answer }
                .distinct().toList()
            require(distractors.size >= 3) {
                "Se requieren tres alternativas diferentes por pregunta."
            }

            val seed = lesson.id.hashCode() xor (sessionIndex * 257 + i * 31)
            val wrong = distractors.shuffled(Random(seed)).take(3)
            val options = (wrong + answer).shuffled(Random(seed xor 0x4E71))
            IntensiveTask(
                id = lesson.id + "-s" + (sessionIndex + 1) + "-extra" + (i + 1),
                kind = "choice",
                skill = "vocabulary",
                prompt = if (toSpanish) "Selecciona la traducción correcta al español."
                         else "Selecciona la expresión equivalente en inglés.",
                stimulus = if (toSpanish) pair.english else pair.spanish,
                answer = answer,
                options = options,
                hint = if (isLocal) "Recuerda los ejemplos de esta unidad."
                       else "Repaso de vocabulario de otras unidades del mismo nivel.",
                explanation = "Modelo bilingüe: " + pair.english + " — " + pair.spanish,
                audioText = pair.english
            )
        }
        return session.tasks + extra
    }
}
