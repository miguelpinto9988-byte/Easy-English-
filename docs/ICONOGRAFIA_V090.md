# Iconografía Easy English — revisión v0.9.0

## Protección del logotipo

El **zorrito con el libro** permanece sin modificación, tanto en
`app/src/main/AndroidManifest.xml` (icono de lanzamiento) como en el encabezado,
las clases y la pantalla de presentación. No se modifica ni elimina
`app/src/main/res/drawable*/fox_mascot*`.

## Atlas PNG integrado

`app/src/main/res/drawable-nodpi/ee_icon_atlas.png` contiene **30 imágenes
multicolor con transparencia** en una cuadrícula de 6 columnas × 5 filas.
Cada cuadro ocupa 112 × 112 píxeles. `ui/icons/EasyIcons.kt` selecciona cada
imagen por su posición, conserva el canal alfa y comparte la caché de mapas de bits.

Estos recursos PNG son **adaptaciones rasterizadas para interfaz** basadas en los
motivos ilustrados aprobados; no son las ilustraciones PNG originales completas
de 1254 × 1254 píxeles. Las ilustraciones grandes siguen disponibles por separado
para usos que requieren mayor resolución. No se utiliza el icono alternativo
con la letra E para reemplazar el logotipo del zorrito.

## Ubicaciones

| Imagen | Ubicación y función |
|---|---|
| Home / Levels / Unit | Menú lateral, inicio, navegación y unidades |
| Intensive | Pestaña y cabecera de la ruta intensiva |
| Games | Pestaña de juegos y ejercicios |
| Reading / Listening / Writing | Pestañas y cabeceras de cada destreza |
| Speaking / Audio | Modelos orales y controles de reproducción |
| Progress / Review | Estadísticas, repaso y retroalimentación |
| Settings / Profile / Download / Target | Preferencias, foto predeterminada, descargas y clasificación |
| Back / Next | Retroceso y avance en actividades |
| Help / Check / Award | Pistas, resultados y partidas completadas |
| Menu / Sparkles / English | Acceso al menú, decoración de presentación y teoría |
| Certificate / Notification / Bookmark / Ribbon / SpeechDecor / Globe | Recursos disponibles para futuras pantallas; no se muestran como funciones activas inexistentes |

El cambio conserva los iconos de nivel **A0, A1, A2, B1, B2, C1 y C2**
porque comunican la clasificación con letras específicas y no corresponden
a ninguno de los nuevos motivos ilustrativos.

## Comprobaciones

`python3 scripts/validate_icon_atlas.py` comprueba formato PNG, integridad,
transparencia, coordenadas de todos los elementos y presencia del arte en cada
cuadro. GitHub Actions ejecuta ese control antes de las pruebas unitarias
y la compilación de la APK.

**Pendientes:** comprobar los iconos en teléfonos de alta densidad, con
tamaños de fuente grandes, modo oscuro y lector de pantalla. El diseño gráfico
no modifica las reglas ni los resultados del currículo.
