package com.easyenglish.app.data

data class LearningLevel(val code: String, val name: String, val description: String, val units: List<String>)

/** Mapa curricular preliminar: 156 títulos, no 156 lecciones finalizadas. */
object LevelCatalog {
    private fun level(code: String, name: String, detail: String, topics: String) =
        LearningLevel(code, name, detail, topics.split("|"))
    val levels = listOf(
        level("A0","Preparación","Primer contacto con el idioma",
            "El alfabeto|Deletrear nombres|Sonidos de las letras|Escritura y pronunciación|Vocales del inglés americano|Consonantes difíciles|Números del 0 al 100|Saludos básicos|Instrucciones de clase|Palabras frecuentes|Preguntas esenciales|Proyecto de iniciación"),
        level("A1","Principiante","Comunicación y bases gramaticales",
            "Saludos y presentaciones|Pronombres personales|Verbo to be|Preguntas con what y where|Familia y amigos|Adjetivos posesivos|Have y has|Describir personas|Artículos a an the|Sustantivos plurales|Demostrativos|There is y there are|Presente simple afirmativo|Rutinas y horarios|Preguntas con do y does|Adverbios de frecuencia|Comidas y bebidas|Some y any|Can y can't|Peticiones e imperativos|Preposiciones de lugar|Mi ciudad y direcciones|Horas y transporte|Proyecto comunicativo A1"),
        level("A2","Elemental","Situaciones habituales y experiencias",
            "Presente continuo|Presente simple y continuo|Verbos de estado|Actividades del momento|Pasado simple regular|Pasado simple irregular|Preguntas con did|Narraciones cortas|Futuro con going to|Futuro con will|Invitaciones y planes|Organizar una salida|Comparativos|Superlativos|Expresiones de cantidad|Comparaciones as as|Consejos con should|Obligaciones y normas|Permisos y solicitudes|Primer condicional|Experiencias present perfect|Ever y never|Already y yet|Proyecto A2"),
        level("B1","Intermedio","Más independencia comunicativa",
            "Present perfect|Present perfect y pasado simple|For y since|Experiencias y resultados|Pasado continuo|Past perfect|Used to|Narrar historias|Futuros y predicciones|Condicional cero|Primer condicional|Segundo condicional|Modales de posibilidad|Voz pasiva inicial|Oraciones relativas|Descripciones detalladas|Reported speech|Gerundios e infinitivos|Phrasal verbs comunes|Aclarar ideas|Expresar opiniones|Redacción de párrafos|Comprensión inferencial|Proyecto B1"),
        level("B2","Intermedio alto","Argumentos y mayor fluidez",
            "Present perfect continuous|Narraciones avanzadas|Future continuous|Future perfect|Deducción en pasado|Tercer condicional|Condicionales mixtos|Wish e if only|Relativas avanzadas|Cláusulas de participio|Reported speech avanzado|Patrones verbales|Determinantes complejos|Conectores de argumentos|Collocations|Phrasal verbs avanzados|Debates|Informes formales|Presentaciones|Negociación|Medios y noticias|Lectura inferencial|Comprensión de habla natural|Proyecto B2"),
        level("C1","Avanzado","Precisión académica y profesional",
            "Inversión gramatical|Cleft sentences|Estructuras enfáticas|Elipsis|Nominalización|Hedging|Expresar postura|Modales avanzados|Combinaciones léxicas|Pragmática|Registro y estilo|Matices e ironía|Lectura académica|Síntesis de fuentes|Citación y referencias|Pensamiento crítico|Escritura persuasiva|Informes profesionales|Ensayos complejos|Presentaciones avanzadas|Pronunciación y fluidez|Negociación profesional|Comunicación intercultural|Proyecto C1"),
        level("C2","Dominio","Matices complejos y alta precisión",
            "Pragmática experta|Significados implícitos|Ironía retórica|Humor contextual|Discurso complejo|Tomar postura|Persuasión avanzada|Debate especializado|Voz y estilo|Edición lingüística|Géneros discursivos|Intertextualidad|Variedades de inglés|Acentos regionales|Habla espontánea rápida|Medios complejos|Discusiones técnicas|Discurso académico|Mediación avanzada|Negociaciones complejas|Síntesis de fuentes|Exposición espontánea|Comunicación precisa|Proyecto C2")
    )
    init { require(levels.sumOf { it.units.size } == 156) }
}
