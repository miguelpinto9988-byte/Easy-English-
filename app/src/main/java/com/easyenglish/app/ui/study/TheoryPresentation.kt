package com.easyenglish.app.ui.study

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.easyenglish.app.data.StudyLesson
import com.easyenglish.app.ui.icons.Art
import com.easyenglish.app.ui.icons.EasyIcon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Visor interactivo de dieciséis PNG por unidad, sin conexión. */
@Composable
fun TheoryPresentation(lesson: StudyLesson, onSpeak: (String, Float) -> Unit) {
    val context=LocalContext.current
    var index by rememberSaveable(lesson.id) { mutableStateOf(0) }
    var translations by rememberSaveable(lesson.id) { mutableStateOf(false) }
    var expanded by rememberSaveable(lesson.id,index) { mutableStateOf(false) }
    var preparing by remember(lesson.id) { mutableStateOf(false) }
    var enlarged by remember(lesson.id,index) { mutableStateOf(false) }
    var prepStatus by remember(lesson.id) { mutableStateOf("") }
    val scope=rememberCoroutineScope()
    val frames=remember(lesson.id) { TheorySlidePlan.build(lesson) }
    val bundled=remember(lesson.id) { BundledTheorySlides.hasCompleteBundle(context,lesson) }
    val slideCount=if(bundled) BundledTheorySlides.COUNT else TheorySlidePlan.COUNT
    val frame=frames[index]
    val fullContent=frame.fullText()
    val slideHeading=if(bundled) "Diapositiva "+(index+1)+" de 8 · "+lesson.title else frame.title
    val bitmap by produceState<ImageBitmap?>(null,lesson.id,index,translations) {
        value=null
        value=withContext(Dispatchers.IO) {
            if(bundled) {
                BundledTheorySlides.read(context,lesson,index).asImageBitmap()
            } else {
                val file=TheorySlideRenderer.imageFile(context,lesson,index,translations)
                BitmapFactory.decodeFile(file.absolutePath)?.asImageBitmap()
            }
        }
    }

    Column(verticalArrangement=Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment=Alignment.CenterVertically,
            horizontalArrangement=Arrangement.spacedBy(8.dp)) {
            EasyIcon(Art.English,Modifier.size(48.dp))
            Column(Modifier.weight(1f)) {
                Text("TEORÍA VISUAL · PNG",color=Color(0xFF276DDD),fontSize=12.sp)
                Text(slideHeading,color=Color(0xFF153B80),fontSize=19.sp)
            }
        }
        Text(if(bundled)
                "8 PNG originales horizontales · 1920 × 1080 · sin conexión."
            else "16 infografías generadas de esta unidad · sin conexión.",
            color=Color(0xFF617797),fontSize=12.sp)
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement=Arrangement.spacedBy(5.dp)) {
            frames.take(slideCount).forEach { page ->
                FilterChip(
                    selected=index==page.index,
                    onClick={index=page.index},
                    label={Text((page.index+1).toString(),fontSize=12.sp)}
                )
            }
        }
        Card(shape=RoundedCornerShape(18.dp),
            colors=CardDefaults.cardColors(containerColor=Color.White)) {
            if(bitmap==null) {
                Column(Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment=Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Text("Preparando diapositiva PNG...",fontSize=12.sp)
                }
            } else {
                Image(bitmap=bitmap!!,
                    contentDescription=if(bundled)
                        "Imagen PNG "+(index+1)+" de 8. "+lesson.title
                    else "Infografía "+(index+1)+" de 16. "+frame.title,
                    modifier=Modifier.fillMaxWidth().aspectRatio(if(bundled) 16f/9f else 900f/1480f),
                    contentScale=ContentScale.Fit)
            }
        }
        if(bundled && bitmap!=null) {
            TextButton(onClick={enlarged=true},modifier=Modifier.fillMaxWidth()) {
                EasyIcon(Art.Reading,Modifier.size(24.dp))
                Text("Ampliar diapositiva · pellizca para acercar",fontSize=12.sp)
            }
        }
        if(bundled && enlarged && bitmap!=null) {
            Dialog(
                onDismissRequest={enlarged=false},
                properties=DialogProperties(usePlatformDefaultWidth=false)
            ) {
                var zoom by remember(index) { mutableFloatStateOf(1f) }
                var shift by remember(index) { mutableStateOf(Offset.Zero) }
                val gestures=rememberTransformableState { zoomChange, panChange, _ ->
                    zoom=(zoom*zoomChange).coerceIn(1f,5f)
                    shift=if(zoom==1f) Offset.Zero else shift+panChange
                }
                Box(Modifier.fillMaxSize().background(Color.Black),
                    contentAlignment=Alignment.Center) {
                    Image(
                        bitmap=bitmap!!,
                        contentDescription="Diapositiva "+(index+1)+" ampliada",
                        modifier=Modifier.fillMaxWidth()
                            .graphicsLayer(
                                scaleX=zoom,scaleY=zoom,
                                translationX=shift.x,translationY=shift.y
                            ).transformable(gestures),
                        contentScale=ContentScale.Fit
                    )
                    TextButton(
                        onClick={enlarged=false},
                        modifier=Modifier.align(Alignment.TopEnd)
                    ) {
                        Text("Cerrar",color=Color.White)
                    }
                }
            }
        }
        if(!bundled) TextButton(onClick={expanded=true},modifier=Modifier.fillMaxWidth()) {
            EasyIcon(Art.Reading,Modifier.size(26.dp))
            Text("Leer el contenido completo de esta diapositiva",fontSize=12.sp)
        }
        if(expanded && !bundled) {
            AlertDialog(
                onDismissRequest={expanded=false},
                title={Text(frame.title)},
                text={
                    Column(Modifier.heightIn(max=470.dp).verticalScroll(rememberScrollState())) {
                        Text(fullContent,fontSize=15.sp,color=Color(0xFF153B80))
                    }
                },
                confirmButton={
                    TextButton(onClick={expanded=false}) { Text("Cerrar") }
                }
            )
        }
        if(!bundled) Row(verticalAlignment=Alignment.CenterVertically,
            horizontalArrangement=Arrangement.spacedBy(10.dp)) {
            Switch(checked=translations,onCheckedChange={translations=it})
            Text("Mostrar traducciones en los ejemplos",
                modifier=Modifier.weight(1f),fontSize=12.sp,
                color=Color(0xFF153B80))
        }
        Row(horizontalArrangement=Arrangement.spacedBy(7.dp)) {
            OutlinedButton(onClick={index--},enabled=index>0,modifier=Modifier.weight(1f)) {
                EasyIcon(Art.Back,Modifier.size(22.dp))
                Text("Anterior",fontSize=12.sp)
            }
            Button(onClick={index++},enabled=index<slideCount-1,
                modifier=Modifier.weight(1f)) {
                Text(if(index==slideCount-1)"Terminada" else "Siguiente",fontSize=12.sp)
                EasyIcon(Art.Next,Modifier.size(22.dp))
            }
        }
        if(!bundled && frame.audioText.isNotBlank()) {
            OutlinedButton(
                onClick={
                    onSpeak(frame.audioText,0.9f)
                },
                modifier=Modifier.fillMaxWidth()
            ) {
                EasyIcon(Art.Audio,Modifier.size(26.dp))
                Text("Escuchar modelo en inglés")
            }
        }
        if(!bundled) Button(
            enabled=!preparing,
            onClick={
                scope.launch {
                    preparing=true
                    prepStatus="Preparando las 16 imágenes PNG..."
                    try {
                        withContext(Dispatchers.IO) {
                            for(i in 0 until TheorySlidePlan.COUNT) {
                                TheorySlideRenderer.imageFile(context,lesson,i,true)
                            }
                        }
                        prepStatus="16 diapositivas PNG preparadas para esta unidad."
                    } catch(e: Exception) {
                        prepStatus="No se pudo terminar la preparación: "+(e.message ?: "error")
                    } finally {
                        preparing=false
                    }
                }
            },
            modifier=Modifier.fillMaxWidth()
        ) {
            EasyIcon(Art.Download,Modifier.size(24.dp))
            Text(if(preparing)"Preparando diapositivas..." else "Preparar las 16 diapositivas PNG")
        }
        if(!bundled && prepStatus.isNotBlank()) {
            Text(prepStatus,color=Color(0xFF276DDD),fontSize=12.sp)
        }
        Text(
            if(bundled) "Estos ocho PNG horizontales forman parte de la presentación instalada. "+
                "Las traducciones y ejercicios aparecen dentro de cada imagen."
            else "Los PNG se guardan temporalmente en el dispositivo. El contenido procede de "+
                "la teoría de la unidad; aún requiere revisión pedagógica externa.",
            color=Color(0xFF66799C),fontSize=11.sp,textAlign=TextAlign.Start
        )
    }
}
