package com.easyenglish.app.ui.study

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
    var prepStatus by remember(lesson.id) { mutableStateOf("") }
    val scope=rememberCoroutineScope()
    val frames=remember(lesson.id) { TheorySlidePlan.build(lesson) }
    val frame=frames[index]
    val fullContent=frame.fullText()
    val bitmap by produceState<ImageBitmap?>(null,lesson.id,index,translations) {
        value=null
        value=withContext(Dispatchers.IO) {
            val file=TheorySlideRenderer.imageFile(context,lesson,index,translations)
            BitmapFactory.decodeFile(file.absolutePath)?.asImageBitmap()
        }
    }

    Column(verticalArrangement=Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment=Alignment.CenterVertically,
            horizontalArrangement=Arrangement.spacedBy(8.dp)) {
            EasyIcon(Art.English,Modifier.size(48.dp))
            Column(Modifier.weight(1f)) {
                Text("TEORÍA VISUAL · PNG",color=Color(0xFF276DDD),fontSize=12.sp)
                Text(frame.title,color=Color(0xFF153B80),fontSize=19.sp)
            }
        }
        Text("16 diapositivas de esta unidad · de A0 a C2 · sin conexión.",
            color=Color(0xFF617797),fontSize=12.sp)
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement=Arrangement.spacedBy(5.dp)) {
            frames.forEach { page ->
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
                    contentDescription="Diapositiva "+(index+1)+" de 16: "+
                        TheorySlideRenderer.titles[index],
                    modifier=Modifier.fillMaxWidth().aspectRatio(900f/1480f),
                    contentScale=ContentScale.Fit)
            }
        }
        TextButton(onClick={expanded=true},modifier=Modifier.fillMaxWidth()) {
            EasyIcon(Art.Reading,Modifier.size(26.dp))
            Text("Leer el contenido completo de esta diapositiva",fontSize=12.sp)
        }
        if(expanded) {
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
        Row(verticalAlignment=Alignment.CenterVertically,
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
            Button(onClick={index++},enabled=index<TheorySlideRenderer.COUNT-1,
                modifier=Modifier.weight(1f)) {
                Text(if(index==TheorySlideRenderer.COUNT-1)"Terminada" else "Siguiente",fontSize=12.sp)
                EasyIcon(Art.Next,Modifier.size(22.dp))
            }
        }
        if(frame.audioText.isNotBlank()) {
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
        Button(
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
        if(prepStatus.isNotBlank()) {
            Text(prepStatus,color=Color(0xFF276DDD),fontSize=12.sp)
        }
        Text(
            "Los PNG se guardan temporalmente en el dispositivo. El contenido procede de "+
                "la teoría de la unidad; aún requiere revisión pedagógica externa.",
            color=Color(0xFF66799C),fontSize=11.sp,textAlign=TextAlign.Start
        )
    }
}
