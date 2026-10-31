package com.easyenglish.app.ui.study

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color as GColor
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import com.easyenglish.app.R
import com.easyenglish.app.data.StudyLesson
import java.io.File
import java.io.FileOutputStream

/**
 * Genera ocho diapositivas PNG infográficas por unidad (156 unidades).
 * Se crean bajo demanda, desde el currículo offline, en la caché local.
 * El logotipo fox_mascot nunca se modifica ni se utiliza como sustituto.
 */
object TheorySlideRenderer {
    const val COUNT = TheorySlidePlan.COUNT
    private const val W = 900
    private const val H = 1480
    private const val VERSION = "theory_png_v2_16slides"
    private val NAVY = GColor.rgb(20,54,119)
    private val BLUE = GColor.rgb(39,108,221)
    private val INK = GColor.rgb(29,56,107)
    private val GREY = GColor.rgb(91,113,151)
    private val PURPLE = GColor.rgb(132,79,218)
    private val TEAL = GColor.rgb(4,156,160)
    private val ORANGE = GColor.rgb(255,184,64)
    private val PALE = GColor.rgb(239,247,255)
    private val MINT = GColor.rgb(227,249,241)
    private val WARM = GColor.rgb(255,247,228)
    private val WHITE = GColor.WHITE

    val titles = TheorySlidePlan.titles

    fun imageFile(context: Context, lesson: StudyLesson, index: Int, translations: Boolean): File {
        require(index in 0 until COUNT)
        val dir = File(context.cacheDir, VERSION).apply { mkdirs() }
        val file = File(dir, lesson.id + "_" + index + "_" + (if (translations) 1 else 0) + ".png")
        if (file.isFile && file.length() > 2048L) return file
        val bmp = render(context, lesson, index, translations)
        FileOutputStream(file).use { check(bmp.compress(Bitmap.CompressFormat.PNG, 100, it)) }
        bmp.recycle()
        return file
    }

    private fun render(context: Context, lesson: StudyLesson, index: Int, translations: Boolean): Bitmap {
        val bmp = Bitmap.createBitmap(W, H, Bitmap.Config.ARGB_8888)
        val c = Canvas(bmp)
        val brush = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        val frame = TheorySlidePlan.build(lesson)[index]
        val tint = listOf(BLUE,PURPLE,TEAL,ORANGE,BLUE,PURPLE,TEAL,BLUE)[index % 8]

        fun panel(x: Float, y: Float, w: Float, h: Float, r: Float, color: Int) {
            brush.shader = null
            brush.color = color
            c.drawRoundRect(RectF(x,y,x+w,y+h),r,r,brush)
        }
        fun gradient(x: Float,y: Float,w: Float,h: Float,a: Int,b: Int,r: Float) {
            brush.shader = LinearGradient(x,y,x+w,y+h,a,b,Shader.TileMode.CLAMP)
            c.drawRoundRect(RectF(x,y,x+w,y+h),r,r,brush)
            brush.shader = null
        }
        fun label(str: String,x: Int,y: Int,width: Int,size: Float,
                  color: Int = INK,bold: Boolean = false,limit: Int = 280): Int {
            val p = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                this.color = color
                textSize = size
                typeface = Typeface.create(
                    if(bold) "sans-serif-medium" else "sans-serif",
                    if(bold) Typeface.BOLD else Typeface.NORMAL
                )
            }
            val content = str.trim().ifEmpty { "Contenido en preparación" }
            val layout = StaticLayout.Builder.obtain(content,0,content.length,p,width)
                .setIncludePad(false).setLineSpacing(5f,1.04f)
                .setMaxLines((limit/(size*1.34f)).toInt().coerceAtLeast(1))
                .setEllipsize(TextUtils.TruncateAt.END).build()
            c.save()
            c.translate(x.toFloat(),y.toFloat())
            layout.draw(c)
            c.restore()
            return layout.height
        }
        fun icon(tile: Int,x: Int,y: Int,dimension: Int) {
            val opt=BitmapFactory.Options().apply { inScaled=false }
            val atlas=BitmapFactory.decodeResource(context.resources,R.drawable.ee_icon_atlas,opt)
                ?: return
            val col=tile%6; val row=tile/6
            c.drawBitmap(
                atlas,
                Rect(col*112,row*112,(col+1)*112,(row+1)*112),
                RectF(x.toFloat(),y.toFloat(),(x+dimension).toFloat(),(y+dimension).toFloat()),
                brush
            )
            atlas.recycle()
        }
        fun photoScenes(y: Int) {
            val opt=BitmapFactory.Options().apply { inScaled=false }
            val sheet=BitmapFactory.decodeResource(context.resources,
                R.drawable.ee_theory_photo_sheet,opt) ?: return
            repeat(4) { i ->
                val x=80+i*187
                val rect=RectF(x.toFloat(),y.toFloat(),x+165f,y+118f)
                val path=Path().apply { addRoundRect(rect,19f,19f,Path.Direction.CW) }
                panel(x-4f,y-4f,173f,126f,21f,WHITE)
                c.save()
                c.clipPath(path)
                c.drawBitmap(sheet,Rect((i%2)*64,(i/2)*64,(i%2+1)*64,(i/2+1)*64),
                    rect,brush)
                c.restore()
            }
            sheet.recycle()
        }
        fun heading(s: String,y: Int,iconId: Int) {
            icon(iconId,74,y-15,74)
            label(s,155,y,632,34f,NAVY,true,90)
        }
        fun example(y: Int,en: String,es: String,alternate: Boolean) {
            panel(70f,y.toFloat(),760f,146f,23f,if(alternate)WARM else PALE)
            label(en,97,y+21,697,31f,NAVY,true,64)
            label(if(translations)es else "Pulsa «Mostrar traducción» para comparar",
                97,y+85,696,if(translations)26f else 22f,GREY,false,48)
        }

        c.drawColor(GColor.rgb(247,250,255))
        gradient(0f,0f,W.toFloat(),309f,NAVY,tint,0f)
        brush.color=0x22FFFFFF
        c.drawCircle(820f,37f,180f,brush)
        c.drawCircle(20f,270f,120f,brush)
        label("EASY ENGLISH · "+lesson.level+" · UNIDAD "+lesson.number,
            70,51,752,28f,WHITE,true,48)
        label(frame.title,70,122,760,55f,WHITE,true,147)
        repeat(COUNT) { i ->
            panel(70f+i*46,271f,35f,9f,5f,if(i<=index)ORANGE else 0x7799BDFF)
        }
        panel(45f,335f,810f,1013f,31f,WHITE)
        // The page's wording comes from its unit-specific TheoryFrame.
        fun bullets(startY: Int, items: List<String>, theme: Int = PALE, gap: Int = 127) {
            items.take(4).forEachIndexed { i, item ->
                val y=startY+i*gap
                panel(70f,y.toFloat(),760f,(gap-12).toFloat(),21f,theme)
                icon(if(i%2==0) 27 else 22,87,y+20,57)
                label(item,158,y+24,637,30f,INK,false,gap-36)
            }
        }
        fun content(y: Int, height: Int, prose: String, theme: Int = PALE,
                    size: Float = 35f, color: Int = INK) {
            panel(69f,y.toFloat(),762f,height.toFloat(),25f,theme)
            label(prose,101,y+35,688,size,color,false,height-63)
        }
        fun tag(heading: String, y: Int, art: Int = frame.art) {
            icon(art,76,y-13,77)
            label(heading,157,y+2,650,34f,NAVY,true,92)
        }
        when(frame.kind) {
            "cover" -> {
                icon(25,342,414,208)
                label(frame.lead,95,647,711,54f,NAVY,true,160)
                label(frame.body,99,815,703,33f,GREY,false,170)
                tag("Tres logros que practicarás",1025,14)
                bullets(1114,frame.bullets.take(2),MINT,104)
            }
            "list" -> {
                tag(frame.lead,412,14)
                label(frame.body,90,542,725,32f,INK,false,144)
                bullets(721,frame.bullets,MINT,176)
            }
            "rule" -> {
                tag("Idea central de la unidad",418,2)
                content(553,393,frame.body,PALE,37f,NAVY)
                tag("Presta atención",985,15)
                content(1104,178,frame.note,WARM,29f,INK)
            }
            "explain" -> {
                tag("Conecta estas ideas",413,26)
                content(540,432,frame.body,PALE,34f,INK)
                tag("Cómo aplicarla",1020,2)
                content(1128,178,frame.note,MINT,27f,INK)
            }
            "example" -> {
                tag(frame.label,411,frame.art)
                content(529,235,frame.english,PALE,43f,NAVY)
                tag("Su significado",800,5)
                content(898,175,if(translations) frame.spanish
                    else "Piensa en la traducción y después activa «Mostrar traducciones».",
                    MINT,if(translations)34f else 27f,INK)
                label("EJEMPLO DE ESTA UNIDAD · FOTO ILUSTRATIVA",96,1137,680,23f,GREY,true,54)
                photoScenes(1192)
            }
            "dialogue" -> {
                tag("Escucha la conversación",418,8)
                content(557,248,frame.body,PALE,33f,NAVY)
                content(834,247,frame.note,MINT,33f,INK)
                label("IMÁGENES ILUSTRATIVAS DE CONTEXTO",95,1136,700,22f,GREY,true,47)
                photoScenes(1191)
            }
            "mistake" -> {
                tag(frame.lead,410,15)
                content(540,376,frame.body,WARM,37f,INK)
                tag("Regla para recordar",968,11)
                content(1082,207,frame.note,PALE,29f,NAVY)
            }
            "reading" -> {
                tag("Busca las pistas",405,5)
                content(522,667,frame.body,PALE,32f,INK)
                label(frame.note,87,1212,721,27f,TEAL,true,91)
            }
            "questions" -> {
                tag("Pregunta uno",416,14)
                content(533,260,frame.body,PALE,35f,NAVY)
                tag("Pregunta dos",833,14)
                content(943,254,frame.note,MINT,35f,INK)
                label("Vuelve a la lectura antes de comprobar tus respuestas.",
                    86,1218,725,26f,GREY,false,66)
            }
            "writing" -> {
                tag("Tu propuesta",408,7)
                content(527,330,frame.body,PALE,36f,NAVY)
                tag("Revisa tu texto",896,27)
                bullets(1014,frame.bullets,MINT,91)
            }
            "speaking" -> {
                tag("Habla con confianza",412,8)
                content(530,420,frame.body,PALE,37f,NAVY)
                icon(8,339,955,210)
                label(frame.note,99,1180,702,30f,INK,false,103)
            }
            "cloze" -> {
                tag("Completa la frase",415,15)
                content(538,289,frame.body,PALE,42f,NAVY)
                tag("Elige una de estas opciones",863,14)
                bullets(965,frame.bullets,WARM,86)
            }
            "recap" -> {
                tag("La regla que aprendiste",412,21)
                content(528,295,frame.body,PALE,34f,NAVY)
                tag("Solución del reto",860,27)
                content(974,225,frame.note,MINT,30f,INK)
                label("¡Has terminado! Ve a Prácticas para continuar.",
                    92,1240,710,27f,TEAL,true,78)
            }
        }
        panel(45f,1372f,810f,72f,19f,NAVY)
        label("DIAPOSITIVA "+(index+1)+" DE "+COUNT+" · SIN CONEXIÓN · "+lesson.level,
            80,1393,741,24f,WHITE,true,44)
        return bmp
    }
}
