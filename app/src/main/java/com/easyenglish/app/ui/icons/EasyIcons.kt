package com.easyenglish.app.ui.icons

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import com.easyenglish.app.R

/**
 * Ilustraciones PNG con transparencia real. Cada motivo ocupa una celda
 * transparente de 112x112 px dentro del atlas multicolor drawable-nodpi.
 *
 * IMPORTANTE: el atlas NO sustituye ni modifica fox_mascot ni el icono launcher.
 */
enum class Art {
    Home, Levels, Unit, Intensive, Games, Reading, Listening, Writing, Speaking,
    Progress, Settings, Review, Profile, Certificate, Target, Help, Notification,
    Bookmark, Back, Next, Sparkles, Award, Ribbon, SpeechDecor, Download,
    English, Audio, Check, Menu, Globe
}

private object ArtworkCache {
    private const val TILE = 112
    private const val COLUMNS = 6
    private var sheet: Bitmap? = null
    private val icons = mutableMapOf<Art, ImageBitmap>()

    @Synchronized
    fun image(context: Context, art: Art): ImageBitmap {
        icons[art]?.let { return it }
        val source = sheet ?: BitmapFactory.decodeResource(
            context.resources, R.drawable.ee_icon_atlas,
            BitmapFactory.Options().apply { inScaled = false }
        ).also { sheet = it }
        require(source.width == TILE * COLUMNS && source.height == TILE * 5) {
            "El atlas PNG de Easy English no tiene las dimensiones esperadas."
        }
        val index = art.ordinal
        val tile = Bitmap.createBitmap(
            source, (index % COLUMNS) * TILE, (index / COLUMNS) * TILE, TILE, TILE
        ).asImageBitmap()
        icons[art] = tile
        return tile
    }
}

@Composable
fun EasyIcon(
    art: Art,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    val context = LocalContext.current
    val bitmap = remember(context, art) { ArtworkCache.image(context, art) }
    Image(
        bitmap = bitmap,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = ContentScale.Fit
    )
}
