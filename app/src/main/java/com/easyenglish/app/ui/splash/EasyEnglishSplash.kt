package com.easyenglish.app.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.easyenglish.app.R
import com.easyenglish.app.data.ProfileStore
import kotlin.math.abs
import kotlin.math.sin

/**
 * Pantalla de entrada. El libro SIEMPRE es estático: no hay animación de apertura.
 * La barra muestra el avance de la presentación, no descargas ni carga de red.
 * Cuando se integren tareas de inicialización, conectar aquí su progreso real.
 */
private const val SPLASH_DURATION_MS = 4400
private const val APP_TITLE = "Easy English"

private val englishWords = listOf(
    "hello", "learn", "speak", "listen", "read",
    "write", "book", "words", "grammar", "vocabulary",
    "practice", "discover", "improve", "imagine", "success"
)

private val wordColors = listOf(
    Color(0xFF2B7EE0), Color(0xFFE0A44F), Color(0xFF6755CA),
    Color(0xFF159EBB), Color(0xFF4A7DD8)
)

@Composable
fun EasyEnglishSplash(onFinished: () -> Unit) {
    val context = LocalContext.current
    val reduceMotion = remember { ProfileStore(context).reduceMotion() }
    val timeline = remember { Animatable(0f) }
    val latestOnFinished by rememberUpdatedState(onFinished)

    LaunchedEffect(Unit) {
        timeline.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = if (reduceMotion) 300 else SPLASH_DURATION_MS, easing = LinearEasing)
        )
        latestOnFinished()
    }

    val progress = timeline.value

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF061946), Color(0xFF155EB5), Color(0xFF0B2A75))
                )
            )
    ) {
        val availableWidth = maxWidth
        val availableHeight = maxHeight

        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x6642BBFF), Color.Transparent),
                    center = Offset(size.width * 0.5f, size.height * 0.55f),
                    radius = size.width * 0.75f
                ),
                radius = size.width * 0.75f,
                center = Offset(size.width * 0.5f, size.height * 0.55f)
            )
            repeat(46) { index ->
                val x = size.width * (((index * 67 + 19) % 101) / 101f)
                val y = size.height * (((index * 43 + 11) % 97) / 97f)
                val glow = (0.25f + 0.4f *
                    (0.5f + 0.5f * sin(progress * 9f + index * 1.21f)))
                    .coerceIn(0f, 0.8f)
                drawCircle(
                    color = Color.White.copy(alpha = glow),
                    radius = if (index % 6 == 0) 3.5.dp.toPx() else 1.5.dp.toPx(),
                    center = Offset(x, y)
                )
            }
        }

        // Quince palabras salen visualmente desde la zona del libro y
        // ascienden de forma escalonada hasta ocupar cinco filas.
        englishWords.forEachIndexed { index, word ->
            val start = 0.055f + (index * 0.038f)
            val travel = ((progress - start) / 0.31f).coerceIn(0f, 1f)
            val eased = travel * travel * (3f - 2f * travel)
            val column = index % 3
            val row = index / 3
            val targetX = availableWidth * listOf(0.035f, 0.365f, 0.67f)[column]
            val targetY = availableHeight * (0.055f + row * 0.068f)
            val originX = availableWidth * 0.43f
            val originY = availableHeight * 0.56f
            val x = originX + (targetX - originX) * eased
            val y = originY + (targetY - originY) * eased
            val opacity = (travel * 5f).coerceIn(0f, 1f)

            if (opacity > 0f) {
                Text(
                    text = word,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(x = x, y = y)
                        .graphicsLayer {
                            alpha = opacity
                            scaleX = 0.72f + 0.28f * eased
                            scaleY = 0.72f + 0.28f * eased
                        }
                        .clip(RoundedCornerShape(20.dp))
                        .background(wordColors[index % wordColors.size])
                        .border(1.dp, Color.White.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 7.dp, vertical = 5.dp)
                )
            }
        }

        val mascotSize = minOf(availableWidth * 0.80f, 330.dp)
        val floatY = sin(progress * 17f) * 5.dp.value
        val blink = listOf(0.18f, 0.49f, 0.79f).maxOf { center ->
            (1f - abs(progress - center) / 0.012f).coerceIn(0f, 1f)
        }

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = 14.dp)
                .size(mascotSize)
                .graphicsLayer {
                    translationY = floatY.dp.toPx()
                    rotationZ = sin(progress * 10f) * 1.5f
                }
        ) {
            Image(
                painter = painterResource(R.drawable.fox_mascot),
                contentDescription = "Zorrito de Easy English con su libro azul",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
            if (blink > 0.01f) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val eyeWidth = size.width * 0.067f
                    val eyeHeight = size.height * 0.030f * blink
                    listOf(
                        Offset(size.width * 0.390f, size.height * 0.375f),
                        Offset(size.width * 0.610f, size.height * 0.410f)
                    ).forEach { center ->
                        drawOval(
                            color = Color(0xFFF3E6D9).copy(alpha = blink),
                            topLeft = Offset(center.x - eyeWidth / 2f, center.y - eyeHeight / 2f),
                            size = Size(eyeWidth, eyeHeight)
                        )
                        drawLine(
                            color = Color(0xFF173E85).copy(alpha = blink),
                            start = Offset(center.x - eyeWidth * 0.36f, center.y),
                            end = Offset(center.x + eyeWidth * 0.36f, center.y),
                            strokeWidth = 2.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            val shownLetters = (((progress - 0.25f) / 0.48f) * APP_TITLE.length)
                .toInt()
                .coerceIn(0, APP_TITLE.length)
            Text(
                text = APP_TITLE.take(shownLetters),
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 34.sp,
                textAlign = TextAlign.Center,
                minLines = 1,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(22.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color(0x885074C9))
                    .border(1.dp, Color(0xFF8CBFFF), RoundedCornerShape(50))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress.coerceIn(0f, 1f))
                        .fillMaxHeight()
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF357AFF), Color(0xFF35EBEE))
                            )
                        )
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Cargando...",
                fontSize = 12.sp,
                color = Color(0xFFDBE9FF),
                letterSpacing = 2.sp
            )
        }
    }
}
