# Teoría visual PNG · Easy English 0.11.0 (ampliación)

## Alcance

La pestaña **Presentación** de las 156 unidades A0–C2 utiliza ahora un visor de **dieciséis diapositivas infográficas PNG por unidad** (2.496 posibles, más variantes con y sin traducción). Las diapositivas se crean en el dispositivo a partir del currículo de la unidad y se guardan temporalmente en su caché, no en un servidor.

Las dieciséis diapositivas cubren: introducción y objetivos, regla, explicación, cuatro ejemplos separados, conversación, errores frecuentes, lectura, dos preguntas de comprensión, escritura, expresión oral, un ejercicio de completar y recapitulación. No se pierde el contenido de teoría almacenado en los JSON.

## Composición y uso

- Imagen vertical 900 × 1.480 px, adaptada a la anchura de teléfonos Android.
- Diseño multicolor con paneles, texto jerarquizado, segmentos visuales e iconos del sistema PNG vigente.
- Las diapositivas 4 y 5 incorporan fotografías de contexto en cuatro viñetas, procedentes de una ilustración fotográfica generada. **Son fotos genéricas de estudiantes, amistad, un perro y una ciudad**, no fotos nuevas específicas para cada una de las 156 unidades.
- Controles accesibles de **Anterior** y **Siguiente**, con contador y progreso visual.
- Botón **Leer el contenido completo**: abre el texto íntegro de la teoría en un diálogo desplazable cuando el PNG abrevia un párrafo largo.
- Alternancia **Mostrar traducciones** en ejemplos, con nueva imagen PNG generada cuando cambia.
- Lectura de un modelo en inglés mediante la función TTS existente, en ejemplos o diálogo.
- Uso sin conexión. La app reconstruye archivos PNG si Android borra su caché.
- No se modifican ni sustituyen el logotipo oficial del zorrito con el libro ni los recursos de lanzamiento.

## Implementación

- `app/src/main/java/com/easyenglish/app/ui/study/TheorySlideRenderer.kt`: genera y guarda las diapositivas PNG.
- `app/src/main/java/com/easyenglish/app/ui/study/TheoryPresentation.kt`: controles interactivos, carga en IO y visualización.
- `app/src/main/res/drawable-nodpi/ee_theory_photo_sheet.jpg`: pequeño mosaico fotográfico incluido en la APK.
- `scripts/validate_theory_slide_content.py`: revisa datos requeridos de las 156 unidades antes de compilar.
- `app/src/main/java/com/easyenglish/app/ui/study/StudyExperience.kt`: pestaña Presentación conectada al nuevo visor.

## Limitaciones y pruebas pendientes

- Las fotografías son ilustraciones de contexto genéricas y se reutilizan: **todavía no existe un banco fotográfico temático diferente para cada unidad**.
- El texto largo se adapta al panel mediante un máximo de líneas. Es necesario comprobar una muestra estratificada, especialmente unidades B2–C2 con reglas extensas, para evitar omisiones visuales. La teoría original permanece disponible en los datos y ahora puede abrirse íntegra con el botón de lectura, aunque esté abreviada dentro del PNG.
- La generación de PNG se basa en texto curricular previamente estructurado, no en una validación lingüística nueva. Se mantiene pendiente la revisión docente de ejemplos, traducciones y progresión.
- La compilación y las pruebas automáticas no sustituyen pruebas manuales de visualización, memoria, accesibilidad, lectura y audio en un teléfono real.
- Las imágenes PNG se encuentran en la caché interna de la app; esta versión no incorpora todavía un botón de exportación a la galería.

**Criterio recomendado para integrar en main:** GitHub Actions finaliza con éxito, comprobar ocho diapositivas de A0, A1, B1 y C2 y validar la interfaz con un dispositivo Android real.


## Ampliación 0.11.0

La clase `TheorySlidePlan` organiza **16 escenas distintas para cada unidad** usando únicamente los datos del currículo. `TheoryPresentation` ofrece acceso directo a cada página, audio contextual y un botón para generar las 16 imágenes en la caché local del teléfono. El contenido completo de cualquier texto largo sigue accesible en una ventana desplazable. La app no incorpora 2.496 archivos estáticos de gran tamaño: genera exactamente el PNG de cada unidad cuando se solicita, con posibilidad de preparar el paquete completo de esa unidad.

El control de calidad documentado es estructural y técnico. No debe interpretarse como una revisión docente individual de las 2.496 imágenes ni como una prueba de legibilidad en todas las pantallas Android.
