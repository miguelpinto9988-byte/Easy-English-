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
    const val COUNT = 8
    private const val W = 900
    private const val H = 1480
    private const val VERSION = "theory_png_v1"
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

    val titles = listOf(
        "Tu objetivo", "La regla, paso a paso", "Entiende la estructura",
        "Ejemplos bilingües", "Conversación en contexto",
        "Errores y consejos", "Lectura guiada", "Practica lo aprendido"
    )

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
        val tint = listOf(BLUE,PURPLE,TEAL,BLUE,ORANGE,PURPLE,TEAL,BLUE)[index]

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
        label(titles[index],70,122,750,59f,WHITE,true,148)
        repeat(COUNT) { i ->
            panel(70f+i*94,271f,76f,9f,5f,if(i<=index)ORANGE else 0x7799BDFF)
        }
        panel(45f,335f,810f,1013f,31f,WHITE)
        when(index) {
            0 -> {
                heading("¿Qué aprenderás?",379,14)
                label(lesson.title,80,510,730,54f,tint,true,160)
                label(lesson.objective,80,663,730,37f,INK,false,174)
                heading("Al terminar podrás:",883,12)
                lesson.learningOutcomes.take(3).forEachIndexed { i,outcome ->
                    icon(27,80,967+i*105,44)
                    label(outcome,140,981+i*105,660,29f,INK,false,88)
                }
            }
            1 -> {
                heading("La idea principal",378,2)
                panel(70f,497f,760f,395f,24f,PALE)
                label(lesson.specificRule,99,537,698,37f,NAVY,false,319)
                heading("Un consejo",923,15)
                panel(70f,1016f,760f,233f,22f,MINT)
                label(lesson.attention,100,1052,700,32f,INK,false,179)
            }
            2 -> {
                heading("Cómo funciona",379,26)
                panel(70f,493f,760f,378f,23f,PALE)
                label(lesson.rule,99,536,704,35f,INK,false,314)
                heading("Observa el modelo",922,2)
                example(1014,lesson.examples[0].english,lesson.examples[0].spanish,false)
                example(1178,lesson.examples[1].english,lesson.examples[1].spanish,true)
            }
            3 -> {
                heading("Mira y compara",378,2)
                lesson.examples.take(4).forEachIndexed { i,pair ->
                    example(483+i*174,pair.english,pair.spanish,i%2==1)
                }
                photoScenes(1213)
            }
            4 -> {
                heading("Un diálogo realista",378,25)
                panel(70f,502f,760f,282f,26f,PALE)
                icon(8,93,527,73)
                label(lesson.dialogue[0],184,546,600,34f,INK,true,190)
                panel(70f,819f,760f,281f,26f,MINT)
                icon(25,93,844,73)
                label(lesson.dialogue[1],184,854,600,34f,INK,true,202)
                label("Localiza la pregunta y la respuesta.",
                    94,1142,715,29f,GREY,false,55)
                photoScenes(1201)
            }
            5 -> {
                heading("Evita este error",378,15)
                panel(70f,505f,760f,372f,23f,WARM)
                label(lesson.attention,99,548,700,37f,INK,false,290)
                heading("Compara con la regla",923,11)
                panel(70f,1020f,760f,252f,24f,PALE)
                label(lesson.specificRule,99,1055,700,29f,INK,false,190)
            }
            6 -> {
                heading("Lee en contexto",378,5)
                panel(70f,508f,760f,607f,23f,PALE)
                label(lesson.contextualText,98,549,702,33f,INK,false,514)
                label("Vuelve a leer y encuentra la estructura estudiada.",
                    89,1189,720,29f,GREY,false,110)
            }
            7 -> {
                heading("Ahora te toca",379,18)
                panel(70f,503f,760f,262f,24f,MINT)
                label(lesson.writing,101,552,697,35f,NAVY,false,182)
                heading("Practica en voz alta",810,8)
                panel(70f,904f,760f,241f,24f,PALE)
                label(lesson.speaking,101,952,697,34f,INK,false,178)
                label("Continúa en «Prácticas» para reforzar el tema.",
                    90,1198,710,28f,GREY,false,100)
            }
        }
        panel(45f,1372f,810f,72f,19f,NAVY)
        label("DIAPOSITIVA "+(index+1)+" DE "+COUNT+" · SIN CONEXIÓN · "+lesson.level,
            80,1393,741,24f,WHITE,true,44)
        return bmp
    }
}
