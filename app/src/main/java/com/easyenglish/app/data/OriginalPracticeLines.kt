package com.easyenglish.app.data

/**
 * Modelos adicionales redactados para Easy English.
 * Originales y revisables: su inclusión no acredita revisión docente ni nivel MCER.
 */
object OriginalPracticeLines {
    fun forLevel(level: String): List<StudyLine> {
        val pairs: List<Pair<String,String>> = when(level) {
            "A0" -> listOf(
                "The alphabet has twenty-six letters." to "El alfabeto tiene veintiséis letras.",
                "Can you say the letter B?" to "¿Puedes decir la letra B?",
                "My first name is Leo." to "Mi nombre es Leo.",
                "Spell it slowly, please." to "Deletréalo despacio, por favor.",
                "The word starts with M." to "La palabra comienza con M.",
                "How do you pronounce this word?" to "¿Cómo se pronuncia esta palabra?",
                "The vowel sounds different here." to "La vocal suena diferente aquí.",
                "Listen and repeat after me." to "Escucha y repite después de mí.",
                "There are ten pencils on the desk." to "Hay diez lápices sobre el escritorio.",
                "I can count from one to twenty." to "Puedo contar del uno al veinte.",
                "My number is forty-two." to "Mi número es cuarenta y dos.",
                "Please write the number seven." to "Por favor, escribe el número siete.",
                "Good morning, everyone." to "Buenos días a todos.",
                "Nice to meet you." to "Mucho gusto.",
                "See you next class." to "Nos vemos en la próxima clase.",
                "How are you today?" to "¿Cómo estás hoy?",
                "Open your notebook, please." to "Abre tu cuaderno, por favor.",
                "Please raise your hand." to "Por favor, levanta la mano.",
                "I do not understand yet." to "Todavía no entiendo.",
                "Could you repeat the question?" to "¿Podrías repetir la pregunta?",
                "This word means hello." to "Esta palabra significa hola.",
                "Where is the classroom?" to "¿Dónde está el salón de clases?",
                "What is your favorite color?" to "¿Cuál es tu color favorito?",
                "I am ready to practice." to "Ya puedo practicar."
            )
            "A1" -> listOf(
                "I usually get up at seven." to "Normalmente me levanto a las siete.",
                "She walks to school every day." to "Ella va caminando a la escuela todos los días.",
                "Does your brother play soccer?" to "¿Tu hermano juega fútbol?",
                "We are cooking dinner now." to "Estamos preparando la cena ahora.",
                "My parents live near the park." to "Mis padres viven cerca del parque.",
                "There is a library on this street." to "Hay una biblioteca en esta calle.",
                "I visited my friend yesterday." to "Visité a mi amigo ayer.",
                "Could I have a glass of water?" to "¿Me podría dar un vaso de agua?"
            )
            "A2" -> listOf(
                "I have never tried that dish before." to "Nunca antes he probado ese plato.",
                "Although it was raining, we went out." to "Aunque estaba lloviendo, salimos.",
                "I am going to study this weekend." to "Voy a estudiar este fin de semana.",
                "I used to take the bus to work." to "Antes iba al trabajo en autobús.",
                "Would you mind closing the window?" to "¿Te importaría cerrar la ventana?",
                "We have already finished the project." to "Ya hemos terminado el proyecto.",
                "The train leaves in ten minutes." to "El tren sale en diez minutos.",
                "She is better at cooking than I am." to "Ella cocina mejor que yo."
            )
            "B1" -> listOf(
                "If I had more time, I would learn another language." to "Si tuviera más tiempo, aprendería otro idioma.",
                "The movie was more interesting than I expected." to "La película fue más interesante de lo que esperaba.",
                "She explained why the meeting had been postponed." to "Ella explicó por qué se había pospuesto la reunión.",
                "We should consider both advantages and drawbacks." to "Deberíamos considerar tanto las ventajas como las desventajas.",
                "I have been working on this project since May." to "He estado trabajando en este proyecto desde mayo.",
                "Even though he was tired, he finished the report." to "Aunque estaba cansado, terminó el informe.",
                "The instructions were clear enough to follow." to "Las instrucciones eran lo bastante claras para seguirlas.",
                "Let me know if you need any additional information." to "Avísame si necesitas información adicional."
            )
            "B2" -> listOf(
                "The proposal addresses the main concerns raised during the review." to "La propuesta aborda las principales preocupaciones planteadas durante la revisión.",
                "Had we checked the figures earlier, we might have spotted the error." to "Si hubiéramos revisado las cifras antes, quizá habríamos detectado el error.",
                "The findings suggest a correlation, not necessarily a causal link." to "Los hallazgos sugieren una correlación, no necesariamente una relación causal.",
                "Her argument is persuasive, although some evidence is incomplete." to "Su argumento es convincente, aunque algunas pruebas son incompletas.",
                "Several alternatives were considered before the final decision." to "Se consideraron varias alternativas antes de la decisión final.",
                "By the time the session ended, most participants had reached an agreement." to "Cuando terminó la sesión, la mayoría de los participantes había llegado a un acuerdo.",
                "We need to distinguish short-term gains from lasting improvements." to "Debemos distinguir las ganancias a corto plazo de las mejoras duraderas.",
                "The policy may have unintended consequences for smaller businesses." to "La política podría tener consecuencias imprevistas para las empresas pequeñas."
            )
            "C1" -> listOf(
                "The report offers a nuanced account of the factors shaping the outcome." to "El informe ofrece una explicación matizada de los factores que influyeron en el resultado.",
                "Notwithstanding the initial objections, the committee endorsed the plan." to "A pesar de las objeciones iniciales, el comité respaldó el plan.",
                "What seems plausible in theory may prove impractical in practice." to "Lo que parece plausible en teoría puede resultar poco práctico.",
                "The evidence is compelling, yet it warrants closer scrutiny." to "Las pruebas son convincentes, pero merecen un examen más detenido.",
                "Under no circumstances should the results be interpreted as definitive." to "Bajo ninguna circunstancia deben interpretarse los resultados como definitivos.",
                "Her remarks underscored the need for a more inclusive approach." to "Sus comentarios subrayaron la necesidad de un enfoque más inclusivo.",
                "While the framework is robust, its implementation remains uneven." to "Aunque el marco es sólido, su aplicación sigue siendo desigual.",
                "The initiative was intended to bridge a persistent gap in access." to "La iniciativa pretendía reducir una brecha persistente en el acceso."
            )
            "C2" -> listOf(
                "The apparent consensus masked a profound divergence in underlying assumptions." to "El aparente consenso ocultaba una profunda divergencia en los supuestos de fondo.",
                "His ostensibly offhand remark carried implications far beyond the meeting." to "Su comentario aparentemente casual tenía implicaciones que iban mucho más allá de la reunión.",
                "The argument hinges on a distinction that is often overlooked." to "El argumento depende de una distinción que suele pasarse por alto.",
                "Far from being an anomaly, the pattern reflects a systemic tendency." to "Lejos de ser una anomalía, el patrón refleja una tendencia sistémica.",
                "The findings resist any simplistic attempt at categorization." to "Los hallazgos no admiten un intento simplista de clasificación.",
                "Her critique deftly dismantles the premise without dismissing its appeal." to "Su crítica desmonta hábilmente la premisa sin negar su atractivo.",
                "The account is persuasive precisely because it acknowledges its own limits." to "El relato resulta convincente precisamente porque reconoce sus propios límites.",
                "A rigorous appraisal must disentangle coincidence from causation." to "Una evaluación rigurosa debe distinguir la coincidencia de la causalidad."
            )
            else -> emptyList()
        }
        return pairs.map { StudyLine(it.first,it.second) }
    }
}
