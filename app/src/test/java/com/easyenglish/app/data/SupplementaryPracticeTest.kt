package com.easyenglish.app.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SupplementaryPracticeTest {
    private fun lesson(number: Int): StudyLesson {
        val id="a0-"+number.toString().padStart(2,'0')
        val examples=(0 until 4).map { i ->
            StudyLine(
                "Lesson "+number+" example "+i+" is useful.",
                "La leccion "+number+" ejemplo "+i+" es util."
            )
        }
        return StudyLesson(
            id=id,level="A0",number=number,title="Tema "+number,
            objective="Comunicarse",rule="Regla general "+number,
            attention="Revisar errores",examples=examples,
            reading="Lectura",writing="Escribe en ingles",speaking="Habla en ingles",
            specificRule="Explicacion particular de la regla "+number,
            dialogue=listOf("Teacher: Question "+number+"?","Student: Answer "+number+"."),
            readingExtended="Lectura avanzada",rubric=listOf("Claridad","Precision","Contexto"),
            cloze=ClozeItem("Completa frase "+number, "word"+number,
                listOf("wrong1","wrong2","wrong3","word"+number),"Explicacion"),
            learningOutcomes=listOf("Aprender el tema", "Usar vocabulario", "Hablar"),
            contextualText="Texto contextual completo de la unidad "+number,
            readingChecks=listOf(
                ReadingCheck("r1","Pregunta 1","Answer","Evidencia","Explicacion"),
                ReadingCheck("r2","Pregunta 2","Tema","Evidencia","Explicacion")
            )
        )
    }

    private fun levels() = (1..12).map(::lesson)

    @Test fun readingHasSixteenAnswerableQuestions() {
        val lessons=levels()
        val items=SupplementaryPractice.reading(lessons.first(),lessons)
        assertEquals(16,items.size)
        assertEquals(16,items.map { it.id }.distinct().size)
        for (item in items) {
            assertTrue(item.prompt.isNotBlank())
            assertEquals(4,item.options.distinct().size)
            assertEquals(1,item.options.count { it==item.answer })
        }
        // La misma frase local se pregunta en ambas direcciones, no dos veces igual.
        assertEquals(8,items.drop(2).take(8).map { it.stimulus+"|"+it.answer }.distinct().size)
    }

    @Test fun listeningHasSixteenAnswerableAudios() {
        val lessons=levels()
        val items=SupplementaryPractice.listening(lessons.first(),lessons)
        assertEquals(16,items.size)
        assertEquals(16,items.map { it.id }.distinct().size)
        for (item in items) {
            assertTrue(item.audioText.isNotBlank())
            assertEquals(4,item.options.distinct().size)
            assertEquals(1,item.options.count { it==item.answer })
        }
    }

    @Test fun writingHasSixteenDifferentPrompts() {
        val prompts=SupplementaryPractice.writing(lesson(1))
        assertEquals(16,prompts.size)
        assertEquals(16,prompts.map { it.prompt }.distinct().size)
        assertTrue(prompts.all { it.reference.isNotBlank() })
    }

    @Test fun everyGameModeProvidesSixteenUsableRounds() {
        val lessons=levels()
        for (mode in StudyGame.entries) {
            val rounds=(0 until 16).map { index ->
                SupplementaryPractice.game(lessons.first(),lessons,mode,index)
            }
            assertEquals(16,rounds.size)
            for (question in rounds) {
                assertTrue(question.prompt.isNotBlank())
                assertTrue(question.answer.isNotBlank())
                if(mode == StudyGame.ORDER) {
                    assertTrue(question.tokenOrder.size >= 2)
                } else if(mode == StudyGame.MEMORY) {
                    assertTrue(question.spanish.isNotBlank())
                } else {
                    assertEquals(4,question.options.distinct().size)
                    assertEquals(1,question.options.count { it==question.answer })
                }
            }
        }
    }
}
