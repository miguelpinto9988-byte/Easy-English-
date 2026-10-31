# Easy English Android v0.7.1 — Práctica intensiva de 16 ejercicios por sesión

Aplicación Android 10+ en Kotlin y Jetpack Compose. Mantiene navegación V3 desde el avatar, iconos PNG HD, separación de 6 dp y letras en negrita.

## Ampliación comprobable

La biblioteca curricular anterior contenía aproximadamente **1.209.022 caracteres** en siete paquetes JSON. Ahora contiene **6.652.230 caracteres**, es decir **5,50 veces** el volumen de texto serializado anterior. Este dato mide el tamaño del banco y no significa cinco veces más frases originales completamente distintas.

- **156 unidades** repartidas en siete niveles, de A0 a C2.
- **8 sesiones por unidad**, cada una con **16 ejercicios** = **19.968 ejercicios disponibles**.
- Cada sesión conserva **8 tareas base** del currículo y genera **8 ejercicios de repaso bilingüe** a partir de modelos originales: 4 de la unidad y 4 de otras unidades del mismo nivel. Son ejercicios adicionales de práctica, no 9.984 frases originales nuevas.
- Sesiones: Descubre, Comprende, Aplica, Comunica, Consolida, Precisión, Interacción y Reto final.
- Actividades de gramática, vocabulario, lectura, escucha sintética de Android, producción oral, escritura y autoevaluación.
- El curso original proporciona 156 reglas particulares, 156 diálogos y 624 modelos bilingües. Algunas actividades reutilizan estos materiales para practicar distintas destrezas; no se han producido 9.984 frases independientes.
- Los ejercicios cerrados tienen una respuesta de referencia y opciones generadas a partir de bancos por nivel. Las tareas de escritura, habla y reflexión son abiertas y no se califican automáticamente.
- Las tareas y ejemplos son originales de Easy English. La calidad de cada pregunta sigue pendiente de revisión docente externa.
- Las ocho sesiones aparecen en la pestaña Ruta intensiva. El avance contabiliza los **128 ejercicios** de cada unidad y no da por completados los que el alumno salta sin comprobar o contrastar. El progreso se guarda solo en el dispositivo.
- La biblioteca se carga por nivel desde los siete paquetes JSON. El banco de juegos aleatorios anterior se conserva.
- Los contenidos instalados funcionan sin internet. La reproducción de voz depende de un motor TTS con voz adecuada instalada en Android.

## Precauciones pedagógicas

La ampliación **no equivale a una certificación MCER ni a 9.984 reactivos calibrados**. Los ejercicios de ordenar comparan un modelo de referencia y podrían existir variantes correctas. Una gran parte de las tareas abiertas son guías de práctica y no textos lingüísticos independientes. No se han añadido grabaciones humanas originales.

Las referencias de diseño incluyen el Consejo de Europa (MCER), American English Activate del Departamento de Estado de Estados Unidos y la guía de práctica variada de AERO:

https://www.coe.int/en/web/common-european-framework-reference-languages
https://americanenglish.state.gov/resources/activate-games-learning-american-english
https://www.edresearch.edu.au/guides-resources/practice-guides/vary-practice

No se han reproducido capítulos completos ni paquetes de contenido ajenos.

## Código y compilación

- app/src/main/assets/curriculum/ — siete archivos JSON, esquema 5.
- app/src/main/java/com/easyenglish/app/data/StudyLibrary.kt — lectura de sesiones y ejercicios.
- app/src/main/java/com/easyenglish/app/data/IntensiveExerciseBank.kt — genera ocho ejercicios bilingües verificables por sesión desde el corpus existente.
- app/src/main/java/com/easyenglish/app/ui/study/IntensiveLearning.kt — recorrido intensivo y práctica.
- app/src/main/java/com/easyenglish/app/ui/study/StudyExperience.kt — navegación entre pestañas.
- scripts/validate_curriculum.py — comprobación estructural de 9.984 tareas originales y 9.984 ejercicios generados, incluyendo opciones válidas.
- docs/REVISION_PEDAGOGICA.md — protocolo de revisión pendiente.

Para compilar: python3 scripts/validate_curriculum.py y gradle :app:assembleDebug --stacktrace --no-daemon. GitHub Actions genera Easy-English-debug-APK cuando los pasos finalizan sin errores. Deben realizarse pruebas en dispositivos reales antes de distribución.

## Mejoras pendientes

Ampliar las frases realmente únicas por unidad; revisar distractores y explicaciones con especialistas; incorporar audios autorizados; probar Listening, Speaking, Reading y Writing con estudiantes; establecer criterios de evaluación con evidencias y probar accesibilidad.

## Alcance de la auditoría automática

La validación comprueba estructura, unicidad de identificadores y disponibilidad de cuatro opciones distintas con una respuesta correcta en cada ejercicio objetivo generado. GitHub Actions compila una APK de prueba. Estas verificaciones **no sustituyen** pruebas manuales de navegación, TTS y accesibilidad en teléfonos Android reales ni revisión pedagógica por docentes. Las pestañas de lectura, escucha, escritura y juegos conservan sus flujos propios; la ampliación de 16 ejercicios se refiere a las **sesiones de la Ruta intensiva**, no a 16 reactivos originales por cada tarea base.
