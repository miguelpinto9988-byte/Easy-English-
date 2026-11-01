# Integración de diapositivas PNG horizontales — A0

## Estado real

Se preparó un paquete externo con **56 diapositivas PNG 1920 × 1080** distribuidas en siete unidades (ocho imágenes por unidad):

| Unidad | Tema | Diapositivas |
|---|---|---:|
| A0-01 | El alfabeto | 8 |
| A0-02 | Deletrear nombres | 8 |
| A0-03 | Sonidos de las letras | 8 |
| A0-04 | Escritura y pronunciación | 8 |
| A0-05 | Vocales del inglés americano | 8 |
| A0-06 | Consonantes difíciles | 8 |
| A0-08 | Saludos básicos | 8 |

**A0-07 está pendiente.** Su tema curricular es *Números del 0 al 100*, pero las ocho imágenes que anteriormente se etiquetaron «A0-07» contienen saludos, presentaciones y conversaciones. Se han corregido las etiquetas a «A0-08» y están listas para la unidad de saludos; nunca deben enseñarse como números.

## Código de la aplicación

El módulo `BundledTheorySlides.kt` busca ocho archivos PNG íntegros en:

`app/src/main/assets/presentations/a0/a0-XX/slide_01.png` ... `slide_08.png`

Al encontrarlos, la pestaña `TheoryPresentation`:
- muestra **exactamente las ocho imágenes originales** de la presentación en 16:9;
- permite avanzar, retroceder y elegir diapositiva directamente;
- permite abrir la imagen a pantalla completa, acercar con los dedos y desplazarla;
- conserva las imágenes en la instalación, sin depender de red;
- deja intactos `fox_mascot` y el icono oficial del lanzador.

Si faltan archivos, el visor sigue usando las infografías generadas por `TheorySlideRenderer`, evitando un fallo al abrir una unidad. Las unidades todavía sin un paquete de ocho PNG mantienen su visualización anterior.

## Importación necesaria de los archivos binarios

Los PNG originales se encuentran en el ZIP de integración entregado en la conversación. **El repositorio contiene el código lector, pero todavía no contiene los 56 binarios PNG.** Se debe extraer el ZIP en la raíz del repositorio, conservando sus rutas relativas. El archivo `presentations/index.json` incluido en el paquete permite comprobar que las siete unidades tienen sus ocho archivos.

Después de importarlos:
1. Ejecutar `python3 scripts/validate_bundled_presentations.py`. Debe imprimir **OK: 56 diapositivas PNG**; antes de importar informa **PENDING**.
2. Compilar la APK mediante GitHub Actions y comprobar el resultado.
3. Probar en dispositivo Android la navegación, los 8 PNG de cada unidad y el zoom.
4. No fusionar con `main` hasta validar manualmente la visualización y el contenido educativo.

**Esta documentación no debe interpretarse como confirmación de que los PNG ya están incluidos en una APK.**
