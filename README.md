# Easy English — Android

App nativa Android con Kotlin y Jetpack Compose. Requiere **Android 10 (API 29)** o posterior.

## Pantalla de inicio (V4)

La app muestra el zorrito con su libro en posición fija; **se eliminó por completo la animación del libro abriéndose**.

- Mascota de zorro con gafas azules y bufanda, integrada como PNG optimizado.
- Parpadeos y leve movimiento de la mascota.
- Fondo azul con destellos animados.
- 15 palabras de vocabulario que ascienden, una tras otra, desde la zona del libro.
- Texto `Easy English` que se escribe progresivamente.
- Barra de progreso animada.

La animación dura aproximadamente 4,4 segundos y se ejecuta cuando se inicia una actividad nueva. **La barra representa la duración de esta presentación**, no el avance real de una descarga ni de una carga de red; todavía no hay tareas de inicialización para medir. Una futura integración podrá sincronizarla con tareas reales y reducir el tiempo de espera.

## Compilación

Requisitos: Android SDK 37, Java 17, Gradle 9.5.0, Android Gradle Plugin 9.3.1 y Kotlin 2.4.21.

Con Gradle instalado: `gradle :app:assembleDebug`.

También puedes usar el flujo de [GitHub Actions](../../actions): **Generar APK de prueba**. Una vez completado correctamente, descarga el artefacto `Easy-English-debug-APK`.

El APK de depuración no está firmado con credenciales de publicación; debe usarse solo para pruebas. La compilación y las pruebas en dispositivos Android 10+ todavía necesitan verificarse.

## Estructura

- `app/src/main/java/com/easyenglish/app/ui/splash/EasyEnglishSplash.kt`: secuencia en Jetpack Compose.
- `app/src/main/res/drawable-nodpi/fox_mascot.png`: recurso de la mascota.
- `app/src/main/java/com/easyenglish/app/MainActivity.kt`: transición a la pantalla inicial.
- `.github/workflows/android-apk.yml`: compilación automática de APK de prueba.
