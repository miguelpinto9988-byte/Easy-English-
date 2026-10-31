# Easy English — Biblioteca de aprendizaje v0.4.0

Aplicación Android nativa en Kotlin + Jetpack Compose, compatible desde Android 10 (API 29). Mantiene el diseño V3: iconos PNG coloridos, menú lateral desde el avatar, negrita global y separación de 6 dp.

## Contenido de la primera edición interactiva

- 156 fichas curriculares: A0 (12) + A1–C2 (24 cada uno).
- 39 bloques de explicación gramatical, léxica y discursiva.
- Cada ficha muestra objetivo, regla, precaución, cuatro ejemplos bilingües, una actividad de comprensión lectora, consigna de escritura y práctica oral.
- Presentaciones interactivas de cuatro pasos (descubrimiento, regla, modelos y errores).
- Juegos de comprensión, traducción, orden de palabras y tarjetas de repaso.
- Generador local de preguntas a partir del corpus de frases; selección y orden aleatorios, sin almacenar preguntas preconstruidas ni enviar respuestas a internet.
- Listening mediante el motor Text-to-Speech disponible en el dispositivo, solicitando voz estadounidense. Puede requerir instalación previa de voz; no hay audios humanos nuevos incluidos.
- Escritura con guía de autoevaluación. **No se califica automáticamente** una producción libre.
- La navegación permite entrar a una ficha desde cualquiera de las 156 unidades.

El archivo `app/src/main/assets/lessons-v1.json` incluye el contenido inicial. Los 156 módulos son **fichas breves**, no cursos completos de varias horas, ni certificaciones oficiales. La explicación y los ejemplos pueden repetirse entre unidades del mismo bloque temático. Debe hacerse una revisión profesional de exactitud, secuenciación, naturalidad del español y adecuación de nivel, antes de presentar el currículo como validado.

## Prácticas y límites

El generador crea nuevas combinaciones en memoria usando el banco incluido: tiene variedad combinatoria, **no una cantidad literalmente infinita de conocimientos nuevos**. Los distractores para tareas de selección provienen de frases del mismo nivel. La opción de ordenar usa un orden textual de referencia; algunas variaciones lingüísticas válidas podrían no reconocerse. La puntuación por práctica solo sirve como retroalimentación de la sesión.

## Conexión y descargas

Los temas instalados funcionan sin internet. El área Preferencias → Descargar contenidos consulta un catálogo HTTPS en el repositorio solo cuando el estudiante lo solicita. Los archivos se guardan en almacenamiento privado, con verificación SHA-256. No se envían fotografías, nombres ni producciones del estudiante.

## Fuentes de consulta

Los textos de las lecciones son originales. Su diseño se informa en:
- Consejo de Europa — MCER y descriptores: https://www.coe.int/en/web/common-european-framework-reference-languages/cefr-descriptors
- American English (Departamento de Estado de EE. UU.) — materiales: https://americanenglish.state.gov/materials-teaching-english
- ACTFL Proficiency Guidelines 2024: https://www.actfl.org/proficiency-guidelines-overview
- British Council — referencia de enseñanza gramatical: https://learnenglish.britishcouncil.org/free-resources/grammar
- American English — ideas de juegos: https://americanenglish.state.gov/resources/activate-games-learning-american-english

No se han copiado textos extensos, ejercicios, audios ni imágenes de esos sitios. La incorporación de recursos externos queda condicionada a sus licencias y a revisión editorial.

## Compilación

Java 17, Gradle 9.5.0, Kotlin 2.4.21, Android Gradle Plugin 9.3.1, SDK 37 y Compose BOM 2026.09.00. En GitHub Actions: **Generar APK de prueba**.

## Próximas mejoras recomendadas

1. Ampliar cada ficha a lecciones extensas y distintas, con situaciones, diálogos, lecturas y tareas por nivel.
2. Añadir audio grabado con permisos claros de uso y recursos accesibles.
3. Revisar las 156 fichas con docentes y validar rúbricas de Writing y Speaking.
4. Construir pruebas adaptativas por destreza, almacenamiento seguro del progreso y ajuste curricular según desempeño.

Estado de verificación: la compilación automática prueba la integración del estudio interactivo en Android.
