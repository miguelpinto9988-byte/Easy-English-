# Easy English — Android v0.3.0

App Android nativa con Kotlin y Jetpack Compose. Mínimo: **Android 10 (API 29)**.

## Pantallas

- Splash con zorrito y libro abierto (sin animación de abrir el libro).
- Creación y edición de perfil local, foto convertida a PNG.
- Menú lateral único, abierto desde avatar. Inicio, Niveles, Mi clase, Progreso, Editar perfil, Prueba, Preferencias y Descargas.
- Iconos PNG con separación de **6 dp** hasta la etiqueta.
- Material 3 con tipografía **siempre en negrita**.
- Estructura A0 a C2 con 156 **títulos de unidades**, todavía sin el contenido completo de las 156 lecciones.
- Teoría interactiva de ejemplo: verbo *to be*.
- Examen de muestra (no validado, no certifica ni asigna niveles MCER; Writing y Speaking no se califican).

## Descargas y privacidad

La aplicación puede utilizarse sin internet con lo que tiene instalado. Solo consulta el catálogo al seleccionar **Preferencias → Descargar contenidos → Buscar**. El catálogo oficial se publica en **content/manifest.json** de este repositorio. Descargas en HTTPS desde la ruta permitida de GitHub, con verificación SHA-256 y máximo de 8 MB por paquete; admite lecciones JSON y audios MP3.

Los perfiles se guardan de forma privada en el dispositivo; el módulo de descargas **no envía nombre, fotografía ni respuestas a un servidor**. La aplicación no solicita almacenamiento externo ni acceso permanente a la galería.

## Compilación

Java 17, Gradle 9.5.0, Android Gradle Plugin 9.3.1, Kotlin 2.4.21, SDK 37 y Compose BOM 2026.09.00.

Usar Android Studio o ejecutar: gradle :app:assembleDebug

El flujo de GitHub Actions **Generar APK de prueba** crea el artefacto descargable tras una compilación exitosa. La APK debug no es para publicar en tiendas.

## Próximos pasos

Realizar pruebas en Android 10+, desarrollar las 156 unidades, crear exámenes reales por destrezas y validar los instrumentos de clasificación. La prueba actual es **solo una demostración de interfaz**.
