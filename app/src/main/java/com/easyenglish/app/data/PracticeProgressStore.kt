package com.easyenglish.app.data

import android.content.Context
import org.json.JSONObject
import kotlin.math.max

data class ProgressSummary(
    val attempts: Int, val correct: Int,
    val weakSkills: List<String>, val dueUnits: Int
)

/**
 * Solo se persisten conteos de desempeño y próximos repasos,
 * nunca las preguntas generadas, los textos libres o los audios.
 */
class PracticeProgressStore(context: Context) {
    private val prefs=context.getSharedPreferences("easy_english_study_metrics",Context.MODE_PRIVATE)
    private val dayMs=86_400_000L

    @Synchronized
    fun record(lessonId: String, skill: String, success: Boolean) {
        require(Regex("[a-z0-9-]{1,32}").matches(lessonId))
        require(Regex("[a-z]{1,20}").matches(skill))
        val key=lessonId+":"+skill
        val obj=try { JSONObject(prefs.getString(key,"{}") ?: "{}") } catch (_: Exception) { JSONObject() }
        val prevStreak=obj.optInt("streak",0)
        val streak=if(success) (prevStreak+1).coerceAtMost(6) else 0
        val delay=if(!success) 0L else when(streak) {
            1 -> 1L
            2 -> 3L
            3 -> 7L
            4 -> 14L
            else -> 30L
        }
        obj.put("attempts",obj.optInt("attempts",0)+1)
        obj.put("correct",obj.optInt("correct",0)+(if(success)1 else 0))
        obj.put("streak",streak)
        obj.put("due",System.currentTimeMillis()+delay*dayMs)
        prefs.edit().putString(key,obj.toString()).apply()
    }

    fun summary(): ProgressSummary {
        var attempts=0
        var correct=0
        val missed=mutableMapOf<String,Int>()
        val due=mutableSetOf<String>()
        prefs.all.forEach { (key,value) ->
            if(value !is String || !key.contains(':'))return@forEach
            val obj=try { JSONObject(value) } catch (_: Exception) { return@forEach }
            attempts+=obj.optInt("attempts",0)
            correct+=obj.optInt("correct",0)
            val skill=key.substringAfter(':')
            missed[skill]=(missed[skill]?:0)+(obj.optInt("attempts",0)-obj.optInt("correct",0))
            if(obj.optLong("due",Long.MAX_VALUE)<=System.currentTimeMillis()) due.add(key.substringBefore(':'))
        }
        return ProgressSummary(
            attempts,correct,missed.toList().sortedByDescending { it.second }
                .filter { it.second>0 }.map { it.first }.take(3), due.size
        )
    }

    fun nextReviewLabel(lessonId: String, skill: String): String {
        val obj=try {
            JSONObject(prefs.getString(lessonId+":"+skill,"{}") ?: "{}")
        } catch (_: Exception) { JSONObject() }
        val due=obj.optLong("due",0L)
        if(due<=System.currentTimeMillis())return "Conviene repasar ahora."
        val days=max(1,((due-System.currentTimeMillis())/dayMs).toInt()+1)
        return "Próximo repaso sugerido: en "+days+" día(s)."
    }
}
