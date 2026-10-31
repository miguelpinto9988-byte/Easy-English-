package com.easyenglish.app.data

import kotlin.random.Random

/** Prácticas locales: los modelos reutilizados no representan frases originales nuevas. */
data class ChoiceExercise(
    val id: String,
    val prompt: String,
    val stimulus: String,
    val answer: String,
    val options: List<String>,
    val explanation: String,
    val audioText: String = ""
)
data class WritingExercise(val id: String, val prompt: String, val reference: String)

object SupplementaryPractice {
    const val COUNT = 16

    private fun seed(lesson: StudyLesson, slot: Int) = lesson.id.hashCode() xor (slot * 1237)

    private fun options(answer: String, pool: List<String>, seed: Int): List<String> {
        val wrong = pool.filter { it.isNotBlank() && it != answer }
            .distinct().shuffled(Random(seed)).take(3)
        require(wrong.size == 3) { "Se necesitan cuatro alternativas diferentes" }
        return (wrong + answer).shuffled(Random(seed xor 0x254E))
    }

    private fun allLines(levelLessons: List<StudyLesson>): List<StudyLine> =
        levelLessons.flatMap { it.examples }.distinctBy { it.english to it.spanish }

    /** Dos comprobaciones del diálogo/texto y catorce microlecturas bilingües. */
    fun reading(lesson: StudyLesson, levelLessons: List<StudyLesson>): List<ChoiceExercise> {
        val lines = allLines(levelLessons)
        val dialogueAnswers = levelLessons
            .map { it.dialogue[1].substringAfter(": ").trim() }.distinct()
        val titlePool = levelLessons.map { it.title }.distinct()
        val answerDialogue = lesson.dialogue[1].substringAfter(": ").trim()
        val result = mutableListOf(
            ChoiceExercise(
                lesson.id + "-read-1",
                "Lee el diálogo e identifica la respuesta de la segunda persona.",
                lesson.dialogue.joinToString("\n"),
                answerDialogue,
                options(answerDialogue, dialogueAnswers, seed(lesson, 1)),
                "Comprueba la segunda intervención del diálogo."
            ),
            ChoiceExercise(
                lesson.id + "-read-2",
                "Lee el texto y elige el tema central de esta unidad.",
                lesson.contextualText,
                lesson.title,
                options(lesson.title, titlePool, seed(lesson, 2)),
                "El título resume el objetivo del texto."
            )
        )
        val local = lesson.examples.distinctBy { it.english to it.spanish }
        val localEnglish = local.map { it.english }.toSet()
        val review = lines.filter { it.english !in localEnglish }
        require(local.size >= 4 && review.size >= 6)
        repeat(14) { i ->
            val own = i < 8
            val line = if (own) local[i % local.size] else review[(lesson.number + i - 8) % review.size]
            val toSpanish = i % 2 == 0
            val answer = if (toSpanish) line.spanish else line.english
            val values = lines.map { if (toSpanish) it.spanish else it.english }
            result.add(
                ChoiceExercise(
                    lesson.id + "-read-" + (i + 3),
                    if (toSpanish) "Lee y selecciona el significado en español."
                    else "Lee y selecciona la expresión equivalente en inglés.",
                    if (toSpanish) line.english else line.spanish,
                    answer,
                    options(answer, values, seed(lesson, i + 3)),
                    if (own) "Modelo de esta unidad." else "Lectura de repaso del mismo nivel."
                )
            )
        }
        check(result.size == COUNT)
        return result
    }

    /** Doce frases y cuatro intervenciones de diálogo: escuchar y elegir. */
    fun listening(lesson: StudyLesson, levelLessons: List<StudyLesson>): List<ChoiceExercise> {
        val lines = allLines(levelLessons)
        val local = lesson.examples.distinctBy { it.english to it.spanish }
        val localEnglish = local.map { it.english }.toSet()
        val review = lines.filter { it.english !in localEnglish }
        require(local.size >= 4 && review.size >= 8)
        val result = mutableListOf<ChoiceExercise>()
        repeat(12) { i ->
            val line = if (i < 4) local[i] else review[(lesson.number + i - 4) % review.size]
            result.add(
                ChoiceExercise(
                    lesson.id + "-listen-" + (i + 1),
                    "Escucha el mensaje y selecciona su significado.",
                    "",
                    line.spanish,
                    options(line.spanish, lines.map { it.spanish }, seed(lesson, i + 101)),
                    if (i < 4) "Audio del modelo de la unidad." else "Escucha de repaso del nivel.",
                    line.english
                )
            )
        }
        val replies = levelLessons.map { it.dialogue[1].substringAfter(": ").trim() }.distinct()
        require(replies.size >= 4)
        repeat(4) { i ->
            val answer = replies[(lesson.number + i) % replies.size]
            result.add(
                ChoiceExercise(
                    lesson.id + "-listen-" + (i + 13),
                    "Escucha la intervención y elige su transcripción.",
                    "",
                    answer,
                    options(answer, replies, seed(lesson, i + 113)),
                    "Repite el diálogo y compara lo que has escuchado.",
                    answer
                )
            )
        }
        check(result.size == COUNT)
        return result
    }

    /** Escritura abierta: 16 consignas contextualizadas sin corrección automática. */
    fun writing(lesson: StudyLesson): List<WritingExercise> {
        val en = lesson.examples.map { it.english }
        val es = lesson.examples.map { it.spanish }
        val dialogue = lesson.dialogue
        val prompts = listOf(
            "Escribe dos frases propias sobre «" + lesson.title + "».",
            "Traduce al inglés sin copiar: " + es[0],
            "Traduce al español: " + en[1],
            "Reescribe este modelo cambiando un detalle: " + en[0],
            "Escribe una pregunta relacionada con el tema «" + lesson.title + "».",
            "Responde en inglés a la pregunta que acabas de redactar.",
            "Continúa este diálogo con otra intervención: " + dialogue[1],
            "Escribe un diálogo de cuatro intervenciones sobre «" + lesson.title + "».",
            "Utiliza esta expresión en otra situación: " + en[2],
            "Explica con palabras propias cuándo utilizarías esta estructura.",
            "Escribe un ejemplo correcto siguiendo esta regla: " + lesson.specificRule.take(180),
            "Compara dos expresiones de la unidad y explica en qué se diferencian.",
            "Describe brevemente una situación real donde usarías «" + lesson.title + "».",
            "Resume en tus palabras la lectura de esta unidad.",
            "Escribe una respuesta al siguiente objetivo: " + lesson.learningOutcomes.first() + ".",
            "Redacta un texto breve que combine vocabulario, estructura y contexto de esta unidad."
        )
        val references = listOf(
            en.take(2).joinToString(" "), en[0], es[1], en[0],
            dialogue[0], dialogue[1], dialogue.joinToString("\n"), dialogue.joinToString("\n"),
            en[2], lesson.specificRule, en[3], en.take(2).joinToString(" / "),
            lesson.contextualText.take(350), lesson.contextualText.take(350),
            en.take(2).joinToString(" "), en.joinToString(" ")
        )
        return prompts.mapIndexed { i, prompt ->
            WritingExercise(lesson.id + "-write-" + (i + 1), prompt, references[i])
        }.also { check(it.size == COUNT) }
    }

    /** Cada modalidad de juegos ofrece 16 rondas, con refuerzo de otras unidades. */
    fun game(
        lesson: StudyLesson,
        levelLessons: List<StudyLesson>,
        mode: StudyGame,
        round: Int
    ): GeneratedQuestion {
        require(round in 0 until COUNT)
        val selected = levelLessons[(lesson.number - 1 + round) % levelLessons.size]
        val bank = allLines(levelLessons)
        val dialogues = levelLessons.map { it.dialogue[1].substringAfter(": ").trim() }.distinct()
        val titles = levelLessons.map { it.title }.distinct()
        val rng = Random(seed(lesson, mode.ordinal * 100 + round))
        // A0 tiene doce unidades: cuatro cloze extra del texto original.
        if (mode == StudyGame.CLOZE && round >= levelLessons.size) {
            val model = bank[(lesson.number + round) % bank.size]
            val words = model.english.split(Regex("\\s+"))
                .map { it.trim('?', '!', '.', ',', ';', ':') }.filter { it.length >= 2 }
            val word = words[(round - levelLessons.size) % words.size]
            val alternatives = bank.flatMap { it.english.split(Regex("\\s+")) }
                .map { it.trim('?', '!', '.', ',', ';', ':') }
                .filter { it.isNotBlank() && it != word }.distinct()
            val masked = model.english.replaceFirst(word, "_____")
            return GeneratedQuestion(
                mode,
                "Completa exactamente como en el modelo:\n\n" + masked +
                    "\n\nSignificado: " + model.spanish,
                word,
                options(word, alternatives, seed(lesson, round)),
                model.english,
                model.spanish,
                explanation = "Se comprueba el modelo de referencia; pueden existir otras frases válidas.",
                skill = "grammar"
            )
        }
        return PracticeGenerator.create(selected, bank, mode, dialogues, titles, random = rng)
    }
}
