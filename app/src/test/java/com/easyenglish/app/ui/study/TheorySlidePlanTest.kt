package com.easyenglish.app.ui.study

import com.easyenglish.app.data.ClozeItem
import com.easyenglish.app.data.ReadingCheck
import com.easyenglish.app.data.StudyLesson
import com.easyenglish.app.data.StudyLine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TheorySlidePlanTest {
    private fun lesson() = StudyLesson(
        id="a1-03",level="A1",number=3,title="Verbo to be",
        objective="Usar to be para identificar personas y describir estados.",
        rule="I am, you are, he is, she is, it is, we are, they are.",
        attention="No combines she con are.",
        examples=listOf(
            StudyLine("I am a student.","Soy estudiante."),
            StudyLine("She is happy.","Ella está contenta."),
            StudyLine("They are friends.","Son amigos."),
            StudyLine("We are here.","Estamos aquí.")
        ),
        reading="Lee el texto",writing="Describe a una persona.",
        speaking="Di tres frases en voz alta.",
        specificRule="To be significa ser o estar, según el contexto.",
        dialogue=listOf("Teacher: Are you ready?","Student: Yes, I am."),
        readingExtended="Lee con atención.",
        rubric=listOf("Claridad","Ortografía","Vocabulario"),
        cloze=ClozeItem("She ___ happy.","is",listOf("is","am","are","be"),
            "Con she se utiliza is."),
        learningOutcomes=listOf("Describir personas","Hacer preguntas","Identificar estados"),
        contextualText="Sofia is a student. She is happy today.",
        readingChecks=listOf(
            ReadingCheck("c1","Who is a student?","Sofia","Sofia is a student.","Revisa el texto"),
            ReadingCheck("c2","Is she happy?","Yes","She is happy.","Revisa la frase")
        )
    )

    @Test fun allSixteenFramesHaveUniqueTitlesAndRealContent() {
        val lesson=lesson()
        val pages=TheorySlidePlan.build(lesson)
        assertEquals(16,pages.size)
        assertEquals((0 until 16).toList(),pages.map { it.index })
        assertEquals(16,pages.map { it.title }.distinct().size)
        assertTrue(pages.all { it.body.isNotBlank() && it.fullText().isNotBlank() })
        assertEquals(4,pages.count { it.kind=="example" })
        assertEquals(1,pages.count { it.kind=="reading" })
        assertEquals(1,pages.count { it.kind=="speaking" })
        assertEquals(1,pages.count { it.kind=="cloze" })
    }

    @Test fun modelsAndQuestionsStayAssociatedWithTheirOwnUnit() {
        val l=lesson()
        val pages=TheorySlidePlan.build(l)
        assertEquals(l.examples.map { it.english },pages.subList(4,8).map { it.english })
        assertEquals(l.examples.map { it.spanish },pages.subList(4,8).map { it.spanish })
        assertEquals(l.dialogue[0],pages[8].body)
        assertTrue(pages[10].body.contains("Sofia"))
        assertTrue(pages[11].fullText().contains("Who is a student?"))
        assertTrue(pages[14].fullText().contains("She ___ happy."))
        assertFalse(pages[14].fullText().contains("Solución del reto:"))
        assertTrue(pages[15].fullText().contains("Solución del reto: is."))
        assertEquals(6,pages.count { it.audioText.isNotBlank() })
    }
}
