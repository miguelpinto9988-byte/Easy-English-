# Easy English · Android v0.5.0

Aplicación educativa nativa Kotlin + Jetpack Compose para **Android 10+ (API 29)**. Diseño V3: navegación lateral desde el avatar, 6 dp entre iconos PNG HD y texto, negrita global, zonas de seguridad para barras nativas de Android.

## Contenidos disponibles sin internet

- **156 unidades** A0–C2 como primera edición editorial, organizadas en siete etapas.
- **156 diálogos originales distintos**, más modelos bilingües.
- **39 reactivos cerrados de gramática** por bloque y **24 lecturas extendidas** para B1–C2.
- Para A0–A2, **60 reglas particulares** junto con explicaciones de bloque.
- Presentación de **seis pasos**: objetivo, regla particular, ejemplos, diálogo, matices y producción.
- Lectura de diálogos/textos más largos; explicación y escritura con rúbricas de autoevaluación.
- Listening con síntesis de voz estadounidense del motor Android: velocidad normal/lenta, oración y diálogo. Requiere motor y voz instalados; **no hay todavía audios humanos grabados**.
- Generador de **seis prácticas**: comprensión, traducción, completar, diálogo, ordenar y tarjetas. Usa banco controlado y aleatorización; **no genera conocimiento infinito**.
- Estadísticas locales mínimas y repasos espaciados. **No se almacenan las preguntas generadas** ni se envían datos del perfil.
- Los ejercicios cerrados se corrigen por coincidencia con el modelo. La escritura libre, pronunciación y respuestas alternativas **no se califican automáticamente**.
- Puede buscar paquetes opcionales desde Preferencias cuando haya internet; el material instalado es utilizable sin conexión.

## Acceso

Abrir la app, configurar perfil y elegir una unidad en Niveles. En la unidad aparecen Presentación, Prácticas, Lectura, Escucha y Escritura. El menú lateral abre tocando la foto de perfil.

## Estado pedagógico y garantías

**Primera edición revisable, sin validación docente externa.** Se han ampliado la estructura y los materiales, pero no constituyen cursos exhaustivos equivalentes a miles de horas, exámenes calibrados ni certificados MCER/ACTFL. La prueba de clasificación continúa siendo una demostración. Las lecturas avanzadas se comparten entre cuatro unidades del mismo bloque y se diferencian mediante diálogos propios.

- Protocolo de revisión: [docs/REVISION_PEDAGOGICA.md](docs/REVISION_PEDAGOGICA.md)
- Licencias y estrategia de audio: [docs/AUDIO_Y_LICENCIAS.md](docs/AUDIO_Y_LICENCIAS.md)

## Control de calidad y compilación

Ejecutar `python3 scripts/validate_curriculum.py` para verificar la consistencia del currículo. GitHub Actions ejecuta el validador antes de `gradle :app:assembleDebug --stacktrace --no-daemon`.

Versiones: Kotlin 2.4.21, Compose BOM 2026.09.00, Gradle 9.5.0, Android Gradle Plugin 9.3.1, JDK 17 y compileSdk 37. El artefacto `Easy-English-debug-APK` permite probar el proyecto, no publicarlo en tiendas.

## Fuentes académicas de orientación

La teoría, los diálogos y las tareas se redactaron originalmente y se guiaron por:
- [Consejo de Europa (MCER)](https://www.coe.int/en/web/common-european-framework-reference-languages)
- [ACTFL — Proficiency Guidelines 2024](https://www.actfl.org/proficiency-guidelines-overview)
- [American English — Games](https://americanenglish.state.gov/resources/activate-games-learning-american-english)
- [EEF — Feedback](https://educationendowmentfoundation.org.uk/education-evidence/guidance-reports/feedback)

No reproducir ni distribuir materiales de terceros sin permisos y licencias apropiados.
