# Easy English Android v0.7.0 — Biblioteca intensiva 5,5 veces

Aplicación Android 10+ en Kotlin y Jetpack Compose. Mantiene navegación V3 desde el avatar, iconos PNG HD, separación de 6 dp y letras en negrita.

## Ampliación comprobable

La biblioteca curricular anterior contenía aproximadamente **1.209.022 caracteres** en siete paquetes JSON. Ahora contiene **6.652.230 caracteres**, es decir **5,50 veces** el volumen de texto serializado anterior. Este dato mide el tamaño del banco y no significa cinco veces más frases originales completamente distintas.

- **156 unidades** repartidas en siete niveles, de A0 a C2.
- **8 sesiones por unidad**, cada una con **8 tareas** = **9.984 actividades estructuradas**.
- Sesiones: Descubre, Comprende, Aplica, Comunica, Consolida, Precisión, Interacción y Reto final.
- Actividades de gramática, vocabulario, lectura, escucha sintética de Android, producción oral, escritura y autoevaluación.
- El curso original proporciona 156 reglas particulares, 156 diálogos y 624 modelos bilingües. Algunas actividades reutilizan estos materiales para practicar distintas destrezas; no se han producido 9.984 frases independientes.
- Los ejercicios cerrados tienen una respuesta de referencia y opciones generadas a partir de bancos por nivel. Las tareas de escritura, habla y reflexión son abiertas y no se califican automáticamente.
- Las tareas y ejemplos son originales de Easy English. La calidad de cada pregunta sigue pendiente de revisión docente externa.
- Las ocho sesiones aparecen dentro de la pestaña Ruta intensiva en cada unidad. La finalización de tareas y las estadísticas se guardan en el dispositivo sin enviar las respuestas a servidores.
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
- app/src/main/java/com/easyenglish/app/ui/study/IntensiveLearning.kt — recorrido intensivo y práctica.
- app/src/main/java/com/easyenglish/app/ui/study/StudyExperience.kt — navegación entre pestañas.
- scripts/validate_curriculum.py — comprobación estructural de 9.984 tareas.
- docs/REVISION_PEDAGOGICA.md — protocolo de revisión pendiente.

Para compilar: python3 scripts/validate_curriculum.py y gradle :app:assembleDebug --stacktrace --no-daemon. GitHub Actions genera Easy-English-debug-APK cuando los pasos finalizan sin errores. Deben realizarse pruebas en dispositivos reales antes de distribución.

## Mejoras pendientes

Ampliar las frases realmente únicas por unidad; revisar distractores y explicaciones con especialistas; incorporar audios autorizados; probar Listening, Speaking, Reading y Writing con estudiantes; establecer criterios de evaluación con evidencias y probar accesibilidad.
