package com.easyenglish.app.ui.study

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import kotlinx.coroutines.withContext

/** Visor interactivo de los ocho PNG originales de la unidad. */
@Composable
fun TheoryPresentation(lesson: StudyLesson, onSpeak: (String, Float) -> Unit) {
    val context=LocalContext.current
    var index by rememberSaveable(lesson.id) { mutableStateOf(0) }
    var translations by rememberSaveable(lesson.id) { mutableStateOf(false) }
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
                Text(TheorySlideRenderer.titles[index],color=Color(0xFF153B80),fontSize=19.sp)
            }
        }
        Text("8 infografías de esta unidad, generadas desde el currículo y disponibles sin conexión.",
            color=Color(0xFF617797),fontSize=12.sp)
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
                    contentDescription="Diapositiva "+(index+1)+" de 8: "+
                        TheorySlideRenderer.titles[index],
                    modifier=Modifier.fillMaxWidth().aspectRatio(900f/1480f),
                    contentScale=ContentScale.Fit)
            }
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
        if(index==3 || index==4) {
            OutlinedButton(
                onClick={
                    val value=if(index==4) lesson.dialogue.joinToString(" ")
                        else lesson.examples.first().english
                    onSpeak(value,0.9f)
                },
                modifier=Modifier.fillMaxWidth()
            ) {
                EasyIcon(Art.Audio,Modifier.size(26.dp))
                Text("Escuchar modelo en inglés")
            }
        }
        Text(
            "Los PNG se guardan temporalmente en el dispositivo. El contenido procede de "+
                "la teoría de la unidad; aún requiere revisión pedagógica externa.",
            color=Color(0xFF66799C),fontSize=11.sp,textAlign=TextAlign.Start
        )
    }
}
