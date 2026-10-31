package com.easyenglish.app.ui.study

import com.easyenglish.app.data.StudyLesson

/** Deterministic, curriculum-grounded sixteen-slide lesson. */
data class TheoryFrame(
    val index: Int,
    val kind: String,
    val title: String,
    val label: String,
    val lead: String,
    val body: String,
    val note: String = "",
    val bullets: List<String> = emptyList(),
    val english: String = "",
    val spanish: String = "",
    val audioText: String = "",
    val art: Int = 2
) {
    fun fullText(): String = sequenceOf(
        title, label, lead, body, note, english, spanish,
        bullets.joinToString("\n")
    ).filter { it.isNotBlank() }.joinToString("\n\n")
}

object TheorySlidePlan {
    const val COUNT = 16
    val titles = listOf(
        "Tu nueva unidad", "Objetivos de aprendizaje", "Regla esencial",
        "Explicación paso a paso", "Ejemplo uno", "Ejemplo dos",
        "Ejemplo tres", "Ejemplo cuatro", "Diálogo en contexto",
        "Evita estos errores", "Lectura contextual", "Comprueba la lectura",
        "Tu producción escrita", "Practica en voz alta", "Reto de completar",
        "Resumen y siguiente paso"
    )

    fun build(lesson: StudyLesson): List<TheoryFrame> {
        require(lesson.examples.size >= 4) { "Faltan ejemplos: \${lesson.id}" }
        require(lesson.dialogue.size == 2) { "Falta diálogo: \${lesson.id}" }
        require(lesson.readingChecks.size >= 2) { "Falta comprensión: \${lesson.id}" }
        require(lesson.learningOutcomes.size >= 3) { "Faltan objetivos: \${lesson.id}" }
        val frames = mutableListOf<TheoryFrame>()
        fun add(kind: String, label: String, lead: String, body: String,
                note: String = "", bullets: List<String> = emptyList(),
                english: String = "", spanish: String = "", audioText: String = "",
                art: Int = 2) {
            val i = frames.size
            frames += TheoryFrame(i,kind,titles[i],label,lead,body,note,
                bullets,english,spanish,audioText,art)
        }
        add("cover","EMPEZAMOS","Unidad \${lesson.number}: \${lesson.title}",
            lesson.objective,"Nivel \${lesson.level} · Una presentación de 16 diapositivas",
            bullets=lesson.learningOutcomes.take(3),art=25)
        add("list","AL FINAL PODRÁS","Tres metas para avanzar",
            lesson.objective,bullets=lesson.learningOutcomes.take(3),art=14)
        add("rule","GRAMÁTICA","Regla principal",
            lesson.specificRule,lesson.attention,art=2)
        add("explain","COMPRENDE","Cómo se relacionan las ideas",
            lesson.rule,"Compara con la regla propia de esta unidad: \${lesson.specificRule}",
            art=26)
        lesson.examples.take(4).forEachIndexed { i,pair ->
            add("example","MODELO \${i+1} DE 4",
                "Observa la frase y su significado",
                "¿Qué estructura o palabra reconoces?",
                note="Compara el inglés y el español. Después, crea una variante propia.",
                english=pair.english,spanish=pair.spanish,audioText=pair.english,
                art=listOf(5,16,8,25)[i])
        }
        add("dialogue","CONVERSACIÓN","Dos personas en contexto",
            lesson.dialogue[0],lesson.dialogue[1],
            audioText=lesson.dialogue.joinToString(" ") { it.substringAfter(": ").trim() },art=8)
        add("mistake","ATENCIÓN","Un error que debes evitar",
            lesson.attention,lesson.specificRule,art=11)
        add("reading","LECTURA","Lee el texto con atención",
            lesson.contextualText,
            "Encuentra al menos una palabra o estructura relacionada con \${lesson.title}.",art=5)
        add("questions","COMPRENSIÓN","Comprueba si has entendido",
            lesson.readingChecks[0].prompt,
            lesson.readingChecks[1].prompt,
            bullets=listOf("Vuelve a la lectura si tienes dudas.",
                "Contrasta tus respuestas con los modelos de la unidad."),art=14)
        add("writing","WRITING","Escribe con tus propias palabras",
            lesson.writing,
            bullets=lesson.rubric.take(3),art=7)
        add("speaking","SPEAKING","Practica la pronunciación",
            lesson.speaking,
            "Escucha el diálogo original y practica sin grabar tu voz.",
            audioText=lesson.dialogue.joinToString(" ") { it.substringAfter(": ").trim() },art=8)
        add("cloze","RETO FINAL","Completa la frase del modelo",
            lesson.cloze.prompt,"Piensa antes de ver la solución.",
            bullets=lesson.cloze.options,art=15)
        add("recap","REPASO","Lo importante de \${lesson.title}",
            lesson.specificRule,
            "Solución del reto: \${lesson.cloze.answer}. \${lesson.cloze.explanation}",
            bullets=lesson.learningOutcomes.take(3),art=21)
        check(frames.size == COUNT)
        return frames
    }
}
