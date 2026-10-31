package com.easyenglish.app.data

import android.content.Context
import org.json.JSONObject
import kotlin.random.Random

data class StudyLine(val english: String, val spanish: String)
data class ClozeItem(val prompt: String, val answer: String, val options: List<String>, val explanation: String)
data class StudyLesson(
    val id: String, val level: String, val number: Int, val title: String,
    val objective: String, val rule: String, val attention: String,
    val examples: List<StudyLine>, val reading: String, val writing: String, val speaking: String,
    val specificRule: String, val dialogue: List<String>,
    val readingExtended: String, val rubric: List<String>, val cloze: ClozeItem,
    val editorialStatus: String = "draft_unreviewed"
)

object StudyLibrary {
    private var cache: List<StudyLesson>? = null

    @Synchronized
    fun lessons(context: Context): List<StudyLesson> {
        cache?.let { return it }
        val json=context.assets.open("lessons-v3.json").bufferedReader().use { JSONObject(it.readText()) }
        require(json.getInt("schema")==3) { "Versión curricular incorrecta" }
        val a=json.getJSONArray("lessons")
        val output=(0 until a.length()).map { i ->
            val o=a.getJSONObject(i)
            val examples=o.getJSONArray("examples")
            val dialogue=o.getJSONArray("dialogue")
            val rubric=o.getJSONArray("rubric")
            val c=o.getJSONObject("cloze")
            val options=c.getJSONArray("options")
            StudyLesson(
                id=o.getString("id"),level=o.getString("level"),number=o.getInt("number"),
                title=o.getString("title"),objective=o.getString("objective"),
                rule=o.getString("rule"),attention=o.getString("attention"),
                examples=(0 until examples.length()).map { k ->
                    val e=examples.getJSONObject(k)
                    StudyLine(e.getString("en"),e.getString("es"))
                },
                reading=o.getString("reading"),
                writing=o.getString("writing"),
                speaking=o.getString("speaking"),
                specificRule=o.getString("specificRule"),
                dialogue=(0 until dialogue.length()).map(dialogue::getString),
                readingExtended=o.getString("readingExtended"),
                rubric=(0 until rubric.length()).map(rubric::getString),
                cloze=ClozeItem(
                    c.getString("prompt"),c.getString("answer"),
                    (0 until options.length()).map(options::getString),c.getString("explanation")
                ),
                editorialStatus=o.optString("reviewStatus","draft_unreviewed")
            )
        }
        require(output.size==156 && output.map { it.id }.distinct().size==156)
        require(output.all { it.examples.size>=4 && it.dialogue.size==2 && it.cloze.answer in it.cloze.options })
        cache=output
        return output
    }

    fun find(context: Context,level: String,unit: Int): StudyLesson? =
        lessons(context).firstOrNull { it.level==level && it.number==unit+1 }

    fun levelLines(context: Context,level: String): List<StudyLine> =
        lessons(context).filter { it.level==level }.flatMap { it.examples }

    fun levelDialogues(context: Context,level: String): List<String> =
        lessons(context).filter { it.level==level }
            .map { it.dialogue[1].substringAfter(": ").trim() }.distinct()
}

enum class StudyGame(val label: String) {
    MEANING("Comprensión"),TRANSLATION("Traducción"),CLOZE("Completar"),
    DIALOGUE("Diálogos"),ORDER("Ordenar"),MEMORY("Tarjetas")
}
data class GeneratedQuestion(
    val game: StudyGame, val prompt: String, val answer: String,
    val options: List<String>, val english: String, val spanish: String,
    val tokenOrder: List<String> = emptyList(),
    val explanation: String = "",
    val skill: String = "vocabulary"
)

/**
 * Ejercicios aleatorios usando solamente pares y reactivos aprobados editorialmente.
 * Variedad combinatoria finita. No genera gramática sin revisión ni envía respuestas.
 */
object PracticeGenerator {
    fun create(
        lesson: StudyLesson, bank: List<StudyLine>, mode: StudyGame,
        dialogueBank: List<String> = emptyList(), random: Random = Random.Default
    ): GeneratedQuestion {
        val line=lesson.examples.random(random)
        if(mode==StudyGame.MEMORY) return GeneratedQuestion(
            mode,line.english,line.spanish,emptyList(),line.english,line.spanish,
            skill="retrieval"
        )
        if(mode==StudyGame.ORDER) {
            val tokens=line.english.trim().split(Regex("\\s+"))
            return GeneratedQuestion(
                mode,"Ordena como en el modelo de la lección.",line.english,emptyList(),
                line.english,line.spanish,tokens.shuffled(random),
                "La secuencia se compara con el modelo; otras formulaciones pueden ser válidas.",
                "syntax"
            )
        }
        if(mode==StudyGame.CLOZE) {
            val c=lesson.cloze
            return GeneratedQuestion(mode,"Completa la oración:\n\n"+c.prompt,
                c.answer,c.options.shuffled(random),c.prompt,c.answer,
                explanation=c.explanation,skill="grammar")
        }
        if(mode==StudyGame.DIALOGUE) {
            val answer=lesson.dialogue[1].substringAfter(": ").trim()
            val options=(dialogueBank.filter { it!=answer }.distinct().shuffled(random).take(3)+answer).shuffled(random)
            return GeneratedQuestion(
                mode,"¿Qué respondió la segunda persona?\n\n"+lesson.dialogue[0],
                answer,options,lesson.dialogue.joinToString(" "),answer,
                explanation="Comprueba la segunda intervención del diálogo.",skill="reading")
        }
        val spanish=mode==StudyGame.MEANING
        val answer=if(spanish)line.spanish else line.english
        val wrong=bank.asSequence()
            .map { if(spanish) it.spanish else it.english }
            .filter { it.isNotBlank() && it!=answer }.distinct().shuffled(random).take(3).toList()
        return GeneratedQuestion(
            mode,
            if(spanish)"¿Qué significa esta oración?\n\n"+line.english
                else "Selecciona la traducción correcta:\n\n"+line.spanish,
            answer,(wrong+answer).shuffled(random),line.english,line.spanish,
            explanation="Compara el significado con los modelos de la unidad.",skill="vocabulary"
        )
    }
}
