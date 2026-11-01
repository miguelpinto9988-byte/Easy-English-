# Matriz de pruebas funcionales — Easy English v0.8.0

Estado: validación estructural, pruebas unitarias y compilación Android automatizadas. Las pruebas de interfaz táctil en dispositivos reales y la revisión lingüística externa **siguen pendientes**.

## Recorridos que deben comprobarse en Android 10 o superior

| Pantalla | Prueba | Resultado esperado |
|---|---|---|
| Ruta intensiva | Abrir las 8 sesiones de una unidad A0, B1 y C2 | Cada sesión presenta 16 ejercicios y la unidad muestra 128 |
| Ruta intensiva | Resolver una pregunta objetiva y avanzar/regresar | Respuesta corregida, progreso actualizado, sin bloquearse |
| Ruta intensiva | Saltar una pregunta cerrada sin comprobar | No se cuenta como realizada |
| Ruta intensiva | Escribir una respuesta abierta, consultar el modelo y avanzar | Se contabiliza solo tras el intento y la consulta |
| Ruta intensiva | Confirmar práctica oral y revisar modelo | Se contabiliza sin grabar la voz |
| Ruta intensiva | Volver de sesión 2 a la 1 | Se muestra el ejercicio 16 de la sesión anterior |
| Juegos | Ejecutar 16 rondas de cada una de las 7 modalidades | Contador y botón Finalizar; sin repetir la misma frase en tarjetas, traducción, comprensión y orden |
| Juegos | Responder mal y bien, cambiar modalidad, reiniciar | Puntuación y racha coherentes; selección anterior no persiste |
| Lectura | Completar 16 preguntas, ir adelante y atrás | Las 16 respuestas quedan asociadas a su pregunta; se revela la solución al comprobar |
| Lectura | Revisar la lectura contextual de A0 y de C2 | El texto es legible y las opciones no se cortan |
| Escucha | Escuchar normal/lento con voz en inglés instalada | Reproducción en ambas velocidades |
| Escucha | Intentar escuchar sin voz inglesa instalada | Aviso de error; transcripción opcional accesible |
| Escucha | Resolver 16 preguntas y retroceder | Se conserva la selección y el número de aciertos |
| Escritura | Introducir textos distintos en los 16 ejercicios | Cada texto se conserva al retroceder dentro de la sesión |
| Escritura | Consultar referencia, volver atrás | No aparece como calificación automática |
| Sistema | Rotar pantalla o pasar la app a segundo plano | No se pierden respuestas visibles en el estado guardable |
| Sistema | Usar pantalla pequeña y fuente ampliada | Botones, respuestas y textos accesibles mediante desplazamiento |
| Sistema | Activar TalkBack y reducir animaciones | Navegación básica comprensible y sin bloqueos |

## Validación automatizada

1. `python3 scripts/validate_curriculum.py`: revisa los 7 paquetes, las 156 unidades, la estructura de las 9.984 tareas base y sus 9.984 ampliaciones; añade verificaciones de recursos suficientes para las otras 16 preguntas por pestaña.
2. `gradle :app:testDebugUnitTest`: JUnit revisa los 16 ejercicios de lectura y escucha, los 16 de escritura, 16 rondas en 7 juegos, opciones únicas y modelos nuevos sin duplicados.
3. `gradle :app:assembleDebug`: comprueba que el código se compila y se publica el artefacto de depuración.

## Limitaciones y hallazgos

- Las actividades nuevas reutilizan parte del contenido existente; se incorporan 72 pares bilingües redactados como refuerzo, pero no hay 16 frases lingüísticas independientes por cada una de las 156 unidades.
- La corrección automática de preguntas cerradas compara una solución modelo; se requiere revisar sinónimos, distractores plausibles y variantes correctas con docentes.
- Speaking y Writing no disponen de evaluación automática válida. El TTS depende del dispositivo; no se han añadido voces humanas.
- La ejecución correcta de JUnit y la compilación de la APK no prueban por sí solas la interacción con los botones, el comportamiento en dispositivos variados ni los criterios de accesibilidad.

## Criterio para fusionar

Se recomienda mantener este cambio como PR en borrador hasta verificar la APK al menos en un dispositivo Android real y revisar una muestra estratificada de preguntas con docentes, especialmente A0 y C2.