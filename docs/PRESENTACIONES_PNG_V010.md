# Teoría visual PNG · Easy English 0.10.0

## Alcance

La pestaña **Presentación** de las 156 unidades A0–C2 utiliza ahora un visor de **ocho diapositivas infográficas PNG por unidad** (1.248 posibles, más variantes con y sin traducción). Las diapositivas se crean en el dispositivo a partir del currículo de la unidad y se guardan temporalmente en su caché, no en un servidor.

Se conservan los ocho temas existentes: objetivo, regla específica, base teórica, ejemplos bilingües, conversación, errores frecuentes, lectura contextual y producción. No se pierde el contenido de teoría almacenado en los JSON.

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
