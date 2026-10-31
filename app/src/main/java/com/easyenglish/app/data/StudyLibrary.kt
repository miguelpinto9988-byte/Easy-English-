package com.easyenglish.app.data

import android.content.Context
import org.json.JSONObject
import kotlin.random.Random

data class StudyLine(val english: String, val spanish: String)
data class StudyLesson(
    val id: String, val level: String, val number: Int, val title: String,
    val objective: String, val rule: String, val attention: String,
    val examples: List<StudyLine>, val reading: String,
    val writing: String, val speaking: String
)
object StudyLibrary {
    private var data: List<StudyLesson>? = null
    @Synchronized
    fun lessons(context: Context): List<StudyLesson> {
        data?.let { return it }
        val json = context.assets.open("lessons-v1.json").bufferedReader().use { JSONObject(it.readText()) }
        val array = json.getJSONArray("lessons")
        val result = (0 until array.length()).map { i ->
            val o = array.getJSONObject(i)
            val arr = o.getJSONArray("examples")
            StudyLesson(
                id=o.getString("id"), level=o.getString("level"), number=o.getInt("number"),
                title=o.getString("title"), objective=o.getString("objective"),
                rule=o.getString("rule"), attention=o.getString("attention"),
                examples=(0 until arr.length()).map { j ->
                    val v=arr.getJSONObject(j)
                    StudyLine(v.getString("en"),v.getString("es"))
                }, reading=o.getString("reading"),
                writing=o.getString("writing"), speaking=o.getString("speaking")
            )
        }
        require(result.size==156)
        data=result
        return result
    }
    fun find(context: Context,level: String,unit: Int): StudyLesson? =
        lessons(context).firstOrNull { it.level==level && it.number==unit+1 }
    fun levelLines(context: Context,level: String): List<StudyLine> =
        lessons(context).filter { it.level==level }.flatMap { it.examples }
}
enum class StudyGame(val label: String) {
    MEANING("Comprensión"), TRANSLATION("Traducción"),
    ORDER("Ordenar"), MEMORY("Tarjetas")
}
data class GeneratedQuestion(
    val game: StudyGame, val prompt: String, val answer: String,
    val options: List<String>, val english: String, val spanish: String,
    val tokenOrder: List<String> = emptyList()
)
/** Ejercicios en memoria. Ninguna práctica se almacena ni consulta servidores. */
object PracticeGenerator {
    fun create(
        lesson: StudyLesson,
        bank: List<StudyLine>,
        mode: StudyGame,
        random: Random = Random.Default
    ): GeneratedQuestion {
        val line = lesson.examples.random(random)
        if (mode==StudyGame.MEMORY) {
            return GeneratedQuestion(mode,line.english,line.spanish,emptyList(),line.english,line.spanish)
        }
        if (mode==StudyGame.ORDER) {
            val words=line.english.trim().split(Regex("\\s+"))
            return GeneratedQuestion(mode,"Ordena las palabras para formar una oración.",
                line.english,emptyList(),line.english,line.spanish,words.shuffled(random))
        }
        val toSpanish=mode==StudyGame.MEANING
        val answer=if(toSpanish) line.spanish else line.english
        val distractors=bank.asSequence()
            .map { if(toSpanish) it.spanish else it.english }
            .filter { it!=answer }.distinct().shuffled(random).take(3).toList()
        return GeneratedQuestion(
            game=mode,
            prompt=if(toSpanish) "¿Qué significa esta oración?\n\n"+line.english
                else "Selecciona la traducción correcta:\n\n"+line.spanish,
            answer=answer,
            options=(distractors+answer).shuffled(random),
            english=line.english,spanish=line.spanish
        )
    }
}
