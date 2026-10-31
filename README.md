# Easy English v0.6.0 — Biblioteca curricular mejorada

Aplicación Kotlin + Jetpack Compose para Android 10+; mantiene el diseño V3, avatar, menú lateral, iconos PNG coloridos, separación de 6 dp y negrita global.

## Biblioteca educativa

**156 unidades organizadas en siete paquetes JSON por nivel** (`app/src/main/assets/curriculum/`). Cada una contiene explicación particular única, regla general, advertencias, tres objetivos de aprendizaje, cuatro ejemplos bilingües, diálogo propio, material de lectura, dos comprobaciones objetivas, práctica escrita/oral, rúbrica y metadatos de origen y revisión. Los 7 paquetes sustituyen al archivo monolítico anterior; se carga únicamente el nivel seleccionado en memoria.

- 156 reglas individuales, 156 diálogos y **624 ejemplos bilingües** (parte de los ejemplos se comparten dentro de bloques).
- **312 comprobaciones de lectura** entre diálogos y clasificación temática.
- **39 preguntas de completar** por bloque, reutilizadas en las unidades del mismo bloque.
- **24 lecturas avanzadas** repartidas entre B1 y C2 (algunas compartidas dentro de un bloque).
- Presentación de **ocho pantallas**, lectura con respuesta contextual y búsqueda de unidades por texto.
- **Siete juegos:** comprensión, traducción, completar, diálogos, reconocer tema, ordenar y tarjetas.
- Generación local de combinaciones sin guardar cada ejercicio, con reducción de repeticiones inmediatas cuando hay ejemplos alternativos; esto no equivale a preguntas infinitas.
- Escucha con síntesis de voz Android y velocidades normal/lenta; el audio humano grabado sigue pendiente.
- Producción libre con guía de autocorrección, sin calificación automatizada de Speaking ni Writing.
- Seguimiento de aciertos y repaso espaciado local.

## Garantías y límites

Los textos son originales y siguen en **revisión editorial preliminar**. No existe aún una evaluación externa de docentes, usuarios o validez de clasificación. El código se revisa estructuralmente mediante `scripts/validate_curriculum.py` antes de compilar la APK.

Las lecciones son material inicial ampliado, no programas completos de cientos de horas. En niveles avanzados se reutilizan 24 lecturas por bloques y algunos distractores proceden del banco de frases del mismo nivel.

## Fuentes utilizadas como orientación (sin copiar contenido protegido)

- [MCER, Consejo de Europa](https://www.coe.int/en/web/common-european-framework-reference-languages/cefr-descriptors) — objetivos comunicativos.
- [American English, Departamento de Estado de EE. UU.](https://americanenglish.state.gov/resources/activate-games-learning-american-english) — práctica con juegos.
- [British Council LearnEnglish](https://learnenglish.britishcouncil.org/free-resources/grammar) — teoría y comprobación de comprensión.
- [Android data layer](https://developer.android.com/topic/architecture/data-layer) — organizar datos locales y carga de contenidos.

## Compilación

`python3 scripts/validate_curriculum.py`, luego `gradle :app:assembleDebug --stacktrace --no-daemon`. GitHub Actions genera el artefacto `Easy-English-debug-APK`. La APK debug es para pruebas, no para publicación comercial.

## Pendiente

Revisión docente de las 156 unidades, calibración de pruebas de clasificación, más materiales genuinamente distintos por unidad, comprensión auditiva con voces grabadas autorizadas, y pruebas de la APK en dispositivos reales.
