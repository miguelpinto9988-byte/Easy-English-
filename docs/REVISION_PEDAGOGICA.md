# Protocolo de revisión pedagógica — Easy English

Estado: **pendiente de evaluación externa**. Este archivo define controles y criterios; no acredita que se haya realizado validación con docentes ni estudiantes.

## Alcance de esta versión

La base `lessons-v3.json` reúne 156 unidades, 156 diálogos, 39 ejercicios de completar, 24 textos avanzados originales, ejemplos bilingües, objetivos y rúbricas orientativas. Algunas explicaciones siguen compartidas entre unidades del mismo bloque y deben individualizarse en futuras revisiones.

## Criterios editoriales por unidad

1. **Correspondencia con nivel:** comprobar vocabulario, longitud textual, función comunicativa y secuencia de complejidad.
2. **Precisión lingüística:** reglas, matices, excepciones y traducciones; revisar variantes estadounidenses sin considerar otros acentos como errores.
3. **Coherencia situacional:** diálogo verosímil, información suficiente, ausencia de contradicciones y contexto cultural adecuado.
4. **Lectura y audio:** verificar inteligibilidad, objetivos, transcripciones y progresión de comprensión literal a inferencial según el nivel.
5. **Evaluación justa:** garantizar una única respuesta en ejercicios cerrados; admitir alternativas correctas en respuestas libres.
6. **Diseño accesible:** tamaño de letra, negrita, contraste, motor de voz, subtítulos y alternativas cuando no se disponga de micrófono.
7. **Privacidad:** sin enviar datos identificables al módulo de descarga; no guardar ejercicios individuales cuando no sea necesario.
8. **Trazabilidad:** registrar autoría del material, fecha y versión; comprobar la licencia de cualquier recurso externo.

## Rúbrica orientativa por destreza

| Destreza | Criterios clave |
|---|---|
| Listening | idea principal, detalles relevantes, inferencia proporcional al nivel |
| Reading | información explícita, relaciones, interpretación, evidencia textual |
| Speaking | inteligibilidad, logro del propósito, fluidez contextual y adecuación |
| Writing | cumplimiento de tarea, organización, recursos lingüísticos, cohesión |

En producción oral o escrita usar una escala de cuatro categorías: inicial, en desarrollo, adecuado y sólido, con descriptores propios y ejemplos ancla por nivel. No convertir resultados internos en una certificación MCER.

## Piloto recomendado

- Revisión doble y documentada por especialistas de inglés como segunda lengua.
- Pruebas con estudiantes de cada nivel y diversos dispositivos Android 10+.
- Registro de ambigüedades, distracciones incorrectas, tiempos y carga cognitiva.
- Medición de acuerdo entre evaluadores para Speaking y Writing.
- Revisión de posibles sesgos de acento, edad, variedad lingüística y accesibilidad.
- Análisis posterior para revisar dificultad y criterios de corte; no definir niveles de colocación por porcentajes arbitrarios.

## Control automatizado

`python3 scripts/validate_curriculum.py` verifica estructura, diálogos, cantidad de unidades, rúbricas y preguntas cerradas. **No verifica** naturalidad lingüística, exactitud semántica, fiabilidad del audio ni validez pedagógica. Son responsabilidades de revisión humana.

## Fuentes marco

- [Consejo de Europa — MCER, volúmen complementario](https://www.coe.int/en/web/common-european-framework-reference-languages)
- [ACTFL — Proficiency Guidelines 2024](https://www.actfl.org/proficiency-guidelines-overview)
- [American English — juegos para aprender inglés](https://americanenglish.state.gov/resources/activate-games-learning-american-english)
- [Education Endowment Foundation — retroalimentación](https://educationendowmentfoundation.org.uk/education-evidence/guidance-reports/feedback)

Las referencias sirven para fundamentar el diseño; la aplicación emplea textos de elaboración original, no reproducciones extensas de los materiales citados.
