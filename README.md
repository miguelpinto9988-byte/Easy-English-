# Easy English

Aplicación Android nativa con **Kotlin** y **Jetpack Compose**. Este repositorio contiene una base ejecutable; las lecciones, ejercicios y demás funcionalidades se incorporarán cuando se definan sus requisitos.

## Compatibilidad y herramientas

- Android mínimo: **Android 10 (API 29)**
- SDK de compilación: **Android 17 / API 37**
- Kotlin y Compose Compiler: **2.4.21**
- Android Gradle Plugin: **9.3.1**
- Gradle: **9.5.0**
- Compose BOM: **2026.09.00**
- Java: **JDK 17**
- UI: **Material 3**

## Cómo ejecutar

1. Abre el proyecto con Android Studio compatible con AGP 9.3.1, SDK 37 y JDK 17.
2. Instala Gradle 9.5.0 (o usa el flujo de GitHub Actions).
3. Ejecuta `gradle :app:assembleDebug`.
4. El APK de prueba se genera en `app/build/outputs/apk/debug/app-debug.apk`.

## Compilar con GitHub Actions

En la pestaña **Actions**, ejecuta el flujo **Generar APK de prueba**, o publica cambios en `main`. Después de una ejecución exitosa, descarga el artefacto `Easy-English-debug-APK` en la página de la ejecución.

La APK de depuración utiliza una clave de pruebas y **no** es una versión de producción. Para publicar la app se requerirán firma de lanzamiento, pruebas y definición del producto.

## Estructura

- `app/src/main/java/`: código Kotlin y pantallas Compose.
- `app/src/main/res/`: recursos Android (XML).
- `app/build.gradle.kts`: configuración Android.
- `.github/workflows/`: automatización de compilaciones (YAML).
