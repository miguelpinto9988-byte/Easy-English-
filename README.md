# Easy English

Proyecto Android nativo desarrollado con Kotlin y Jetpack Compose.

Este repositorio es la base inicial de la aplicación. La funcionalidad pedagógica se definirá por etapas.

## Requisitos

- Android 10 (API 29) o superior.
- Android SDK 37 para compilar.
- JDK 17 y Gradle 9.5.0.
- Kotlin 2.4.21, Android Gradle Plugin 9.3.1 y Compose BOM 2026.09.00.

## Compilación

Ejecuta `gradle :app:assembleDebug` desde la raíz, o el flujo **Generar APK de prueba** en la pestaña *Actions* de GitHub. En GitHub Actions, descarga el artefacto `Easy-English-debug-APK` cuando la compilación finalice correctamente.

La APK de depuración no está firmada para distribución en tiendas y debe usarse únicamente para pruebas.
