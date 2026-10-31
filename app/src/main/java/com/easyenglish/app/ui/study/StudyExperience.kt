package com.easyenglish.app.ui.study

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.easyenglish.app.R
import com.easyenglish.app.data.GeneratedQuestion
import com.easyenglish.app.data.PracticeGenerator
import com.easyenglish.app.data.StudyGame
import com.easyenglish.app.data.StudyLesson
import com.easyenglish.app.data.StudyLibrary
import com.easyenglish.app.data.StudyLine
import com.easyenglish.app.data.SupplementaryPractice
import com.easyenglish.app.data.PracticeProgressStore
import com.easyenglish.app.ui.icons.Art
import com.easyenglish.app.ui.icons.EasyIcon
import java.util.Locale

private val Ocean=Color(0xFF205ECB)
private val Deep=Color(0xFF153B80)
private val Muted=Color(0xFF66799C)
private val Soft=Color(0xFFEAF2FF)
private val Mint=Color(0xFFE3FAF2)
private val Orange=Color(0xFFFFF1DF)
private val Purple=Color(0xFFF1E9FF)

@Composable
fun StudyExperience(level: String, unit: Int, onBack: () -> Unit) {
    val context=LocalContext.current
    val lesson=remember(level,unit) { StudyLibrary.find(context,level,unit) }
    val lines=remember(level) { StudyLibrary.levelLines(context,level) }
    val levelLessons=remember(level) { StudyLibrary.byLevel(context,level) }
    val dialogueBank=remember(level) { StudyLibrary.levelDialogues(context,level) }
    val titleBank=remember(level) { StudyLibrary.levelTitles(context,level) }
    val progressStore=remember(context) { PracticeProgressStore(context) }
    var tab by rememberSaveable(level,unit) { mutableStateOf("Ruta intensiva") }
    var ttsReady by remember { mutableStateOf(false) }
    var audioStatus by remember { mutableStateOf("") }
    val speech=remember(context) {
        TextToSpeech(context) { ttsReady = it==TextToSpeech.SUCCESS }
    }
    DisposableEffect(speech) { onDispose { speech.stop(); speech.shutdown() } }
    val speak: (String,Float) -> Unit = { text,rate ->
        if (!ttsReady) audioStatus="El motor de voz aún no está disponible."
        else {
            val status=speech.setLanguage(Locale.US)
            if (status==TextToSpeech.LANG_MISSING_DATA || status==TextToSpeech.LANG_NOT_SUPPORTED) {
                audioStatus="Instala una voz de inglés estadounidense en los ajustes de voz de Android."
            } else {
                speech.setSpeechRate(rate)
                speech.speak(text,TextToSpeech.QUEUE_FLUSH,null,"easy-english-study")
                audioStatus=""
            }
        }
    }

    LazyColumn(
        modifier=Modifier.fillMaxSize(),
        contentPadding=androidx.compose.foundation.layout.PaddingValues(18.dp),
        verticalArrangement=Arrangement.spacedBy(13.dp)
    ) {
        item {
            TextButton(onClick=onBack) {
                EasyIcon(Art.Back,Modifier.size(23.dp))
                Text("Regresar a las unidades",color=Ocean)
            }
        }
        if(lesson==null){
            item { Text("Esta unidad todavía no está disponible.",color=Deep) }
        } else {
            item {
                Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(6.dp)) {
                    Image(painterResource(R.drawable.fox_mascot),null,Modifier.size(66.dp))
                    Column(Modifier.weight(1f)) {
                        Text(level+" · UNIDAD "+(unit+1),fontSize=11.sp,color=Ocean)
                        Text(lesson.title,fontSize=23.sp,lineHeight=28.sp,color=Deep)
                        Text("Teoría + práctica · Sin conexión",fontSize=11.sp,color=Muted)
                    }
                }
            }
            item {
                Row(Modifier.horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(6.dp)) {
                    listOf("Ruta intensiva","Presentación","Prácticas","Lectura","Escucha","Escritura").forEach { name ->
                        val art=when(name) {
                            "Ruta intensiva"->Art.Intensive
                            "Presentación"->Art.English
                            "Prácticas"->Art.Games
                            "Lectura"->Art.Reading
                            "Escucha"->Art.Listening
                            else->Art.Writing
                        }
                        FilterChip(selected=tab==name,onClick={tab=name},label={
                            Row(verticalAlignment=Alignment.CenterVertically,
                                horizontalArrangement=Arrangement.spacedBy(4.dp)) {
                                EasyIcon(art,Modifier.size(25.dp))
                                Text(name,fontSize=12.sp)
                            }
                        })
                    }
                }
            }
            when(tab){
                "Ruta intensiva" -> item { IntensiveLearning(lesson,speak,audioStatus) }
                "Presentación" -> item { Presentation(lesson) }
                "Prácticas" -> item { Games(lesson,levelLessons,progressStore) }
                "Lectura" -> item { Reading(lesson,levelLessons) }
                "Escucha" -> item { Listening(lesson,levelLessons,speak,audioStatus) }
                "Escritura" -> item { Writing(lesson) }
            }
            item {
                Card(colors=CardDefaults.cardColors(containerColor=Soft),shape=RoundedCornerShape(15.dp)) {
                    Column(Modifier.padding(14.dp),verticalArrangement=Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment=Alignment.CenterVertically) {
                            EasyIcon(Art.Help,Modifier.size(28.dp))
                            Text("Aprender con criterio",color=Deep,fontSize=14.sp)
                        }
                        Text("Estas fichas son originales y están en revisión pedagógica. Las actividades objetivas dan corrección automática; escritura y pronunciación requieren revisión humana para una evaluación real.",color=Muted,fontSize=11.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun Heading(label: String, text: String) {
    Column(verticalArrangement=Arrangement.spacedBy(5.dp)) {
        Text(label.uppercase(),fontSize=11.sp,color=Ocean,letterSpacing=1.sp)
        Text(text,fontSize=20.sp,color=Deep,lineHeight=25.sp)
    }
}

@Composable
private fun AreaHeading(art: Art, label: String, subtitle: String) {
    Row(verticalAlignment=Alignment.CenterVertically,
        horizontalArrangement=Arrangement.spacedBy(9.dp)) {
        EasyIcon(art,Modifier.size(49.dp))
        Heading(label,subtitle)
    }
}

@Composable
private fun Presentation(lesson: StudyLesson) {
    var slide by remember(lesson.id) { mutableStateOf(0) }
    var showTranslation by remember(lesson.id,slide) { mutableStateOf(false) }
    val steps=listOf("Objetivo del tema","Regla y estructura","Explicación general",
        "Ejemplos bilingües","Diálogo contextual","Errores frecuentes",
        "Comprende el texto","Tu producción")
    Card(colors=CardDefaults.cardColors(containerColor=Color.White),shape=RoundedCornerShape(23.dp)) {
        Column(Modifier.fillMaxWidth(),verticalArrangement=Arrangement.spacedBy(14.dp)) {
            Box(Modifier.fillMaxWidth().background(
                Brush.horizontalGradient(listOf(Ocean,Color(0xFF8A65DA)))
            ).padding(20.dp)) {
                Column(verticalArrangement=Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment=Alignment.CenterVertically,
                        horizontalArrangement=Arrangement.spacedBy(7.dp)) {
                        EasyIcon(Art.English,Modifier.size(35.dp))
                        Text("PRESENTACIÓN INTERACTIVA · "+(slide+1)+"/8",
                            color=Color(0xFFE9F3FF),fontSize=11.sp)
                    }
                    Text(steps[slide],color=Color.White,fontSize=22.sp)
                    LinearProgressIndicator(progress={ (slide+1)/8f },modifier=Modifier.fillMaxWidth(),
                        color=Color(0xFFB1F0E2),trackColor=Color(0x6679ABF0))
                }
            }
            Column(Modifier.padding(horizontal=18.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
                when(slide) {
                    0 -> {
                        Heading("Aprenderás a...",lesson.title)
                        lesson.learningOutcomes.forEach { outcome ->
                            Text("✓ "+outcome,color=Deep,fontSize=13.sp,lineHeight=19.sp)
                        }
                    }
                    1 -> {
                        Heading("Regla del tema","Cuándo utilizarla")
                        Text(lesson.specificRule,color=Deep,fontSize=15.sp,lineHeight=22.sp)
                        Card(colors=CardDefaults.cardColors(containerColor=Mint)) {
                            Text("Consejo: "+lesson.attention,modifier=Modifier.padding(13.dp),color=Deep,fontSize=13.sp)
                        }
                    }
                    2 -> {
                        Heading("Base teórica","Relación con otras estructuras")
                        Text(lesson.rule,color=Deep,fontSize=15.sp,lineHeight=23.sp)
                        Text("Compara esta regla con la explicación particular de la unidad.",
                            color=Muted,fontSize=12.sp)
                    }
                    3 -> {
                        Heading("Modelos interactivos","Observa y compara")
                        lesson.examples.forEachIndexed { i,p ->
                            Card(colors=CardDefaults.cardColors(containerColor=if(i%2==0) Soft else Orange)) {
                                Column(Modifier.padding(11.dp),verticalArrangement=Arrangement.spacedBy(4.dp)) {
                                    Text(p.english,color=Deep,fontSize=14.sp)
                                    if(showTranslation)Text(p.spanish,color=Muted,fontSize=12.sp)
                                }
                            }
                        }
                        OutlinedButton(onClick={showTranslation=!showTranslation}) {
                            Text(if(showTranslation)"Ocultar traducciones" else "Revelar traducciones")
                        }
                    }
                    4 -> {
                        Heading("Conversación original","Aprende en una situación")
                        Card(colors=CardDefaults.cardColors(containerColor=Soft)) {
                            Column(Modifier.padding(15.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
                                lesson.dialogue.forEachIndexed { i,line ->
                                    Text(line,color=if(i==0) Deep else Ocean,fontSize=15.sp)
                                }
                            }
                        }
                        Text("Identifica la pregunta, la respuesta y el propósito comunicativo.",color=Muted,fontSize=12.sp)
                    }
                    5 -> {
                        Heading("Matices y errores","Por qué importa la regla")
                        Text(lesson.attention,color=Deep,fontSize=15.sp,lineHeight=22.sp)
                        Card(colors=CardDefaults.cardColors(containerColor=Orange)) {
                            Text(lesson.specificRule,modifier=Modifier.padding(12.dp),color=Deep,fontSize=12.sp)
                        }
                    }
                    6 -> {
                        Heading("Comprensión","Localiza la información")
                        Text(lesson.contextualText.take(900),color=Deep,fontSize=13.sp,lineHeight=21.sp)
                        Text(lesson.readingChecks[0].prompt,color=Ocean,fontSize=13.sp)
                        Text("Practica más en la pestaña Lectura.",color=Muted,fontSize=11.sp)
                    }
                    else -> {
                        Heading("Aplica y explica","Ahora te toca")
                        Text(lesson.writing,color=Deep,fontSize=14.sp)
                        Text(lesson.speaking,color=Muted,fontSize=13.sp)
                        Text("Rúbrica para revisar tu respuesta:",color=Ocean,fontSize=13.sp)
                        lesson.rubric.forEach { Text("• "+it,color=Deep,fontSize=12.sp) }
                    }
                }
            }
            Row(Modifier.fillMaxWidth().padding(horizontal=17.dp,vertical=12.dp),
                horizontalArrangement=Arrangement.spacedBy(9.dp)) {
                OutlinedButton(onClick={slide=(slide-1).coerceAtLeast(0)},
                    enabled=slide>0,modifier=Modifier.weight(1f)) { Text("Anterior") }
                Button(onClick={slide=(slide+1)%8},modifier=Modifier.weight(1f),
                    colors=ButtonDefaults.buttonColors(containerColor=Ocean)) {
                    Text(if(slide==7)"Repasar" else "Siguiente")
                }
            }
        }
    }
}


@Composable
private fun Games(lesson: StudyLesson, levelLessons: List<StudyLesson>, tracker: PracticeProgressStore) {
    var mode by rememberSaveable(lesson.id) { mutableStateOf(StudyGame.MEANING) }
    var round by rememberSaveable(lesson.id, mode) { mutableStateOf(0) }
    var finished by rememberSaveable(lesson.id, mode) { mutableStateOf(false) }
    var points by rememberSaveable(lesson.id, mode) { mutableStateOf(0) }
    var streak by rememberSaveable(lesson.id, mode) { mutableStateOf(0) }
    val task=remember(lesson.id,mode,round) {
        SupplementaryPractice.game(lesson,levelLessons,mode,round)
    }
    var selected by remember(lesson.id,mode,round) { mutableStateOf("") }
    var validated by remember(lesson.id,mode,round) { mutableStateOf(false) }
    var flip by remember(lesson.id,mode,round) { mutableStateOf(false) }
    var reviewed by remember(lesson.id,mode,round) { mutableStateOf(false) }
    var order by remember(lesson.id,mode,round) { mutableStateOf(listOf<Int>()) }
    val correct=if(mode==StudyGame.ORDER) {
        order.map { task.tokenOrder[it] }.joinToString(" ")==task.answer
    } else selected==task.answer

    Column(verticalArrangement=Arrangement.spacedBy(12.dp)) {
        AreaHeading(Art.Games,"Juegos · 16 ejercicios por modalidad","Práctica y repaso del nivel")
        Text("Cada juego tiene una serie de 16 rondas. Parte de los modelos se reutiliza entre unidades.",
            color=Muted,fontSize=12.sp)
        Row(Modifier.horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(6.dp)) {
            StudyGame.entries.forEach { game ->
                FilterChip(selected=mode==game,
                    onClick={mode=game},
                    label={Text(game.label,fontSize=12.sp)})
            }
        }
        LinearProgressIndicator(
            progress={ if(finished) 1f else round.toFloat()/SupplementaryPractice.COUNT },
            modifier=Modifier.fillMaxWidth())
        Text((if(finished)16 else round+1).toString()+
            " de 16 · "+mode.label+" · Puntos: "+points+" · Racha: "+streak,
            color=Ocean,fontSize=12.sp)
        if(finished) {
            Card(colors=CardDefaults.cardColors(containerColor=Mint)) {
                Column(Modifier.padding(16.dp),verticalArrangement=Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment=Alignment.CenterVertically) {
                        EasyIcon(Art.Award,Modifier.size(51.dp))
                        Text("Serie de 16 ejercicios finalizada",color=Deep,fontSize=17.sp)
                    }
                    Text("Tu puntuación en esta serie: "+points,color=Deep,fontSize=13.sp)
                    Button(onClick={round=0;finished=false;points=0;streak=0},
                        modifier=Modifier.fillMaxWidth()) { EasyIcon(Art.Review,Modifier.size(25.dp)); Text("Repetir esta serie") }
                }
            }
        } else {
            Card(colors=CardDefaults.cardColors(containerColor=Color.White),
                shape=RoundedCornerShape(19.dp)) {
                Column(Modifier.padding(16.dp),verticalArrangement=Arrangement.spacedBy(11.dp)) {
                    if(mode==StudyGame.MEMORY) {
                        Text("Tarjeta "+(round+1)+" de 16 · Intenta recordar antes de descubrir.",
                            color=Muted,fontSize=12.sp)
                        Box(Modifier.fillMaxWidth().height(150.dp)
                            .background(if(flip) Mint else Soft,RoundedCornerShape(15.dp))
                            .clickable { flip=!flip },contentAlignment=Alignment.Center) {
                            Text(if(flip)task.spanish else task.english,color=Deep,fontSize=20.sp,
                                textAlign=TextAlign.Center,modifier=Modifier.padding(12.dp))
                        }
                        if(flip&&!reviewed) {
                            OutlinedButton(onClick={
                                tracker.record(lesson.id,task.skill,false)
                                streak=0;reviewed=true
                            },modifier=Modifier.fillMaxWidth()) { Text("Necesito repasarla") }
                            Button(onClick={
                                tracker.record(lesson.id,task.skill,true)
                                points+=10;streak++;reviewed=true
                            },modifier=Modifier.fillMaxWidth()) { Text("Recordé la respuesta") }
                        }
                    } else {
                        Text(if(mode==StudyGame.ORDER)"ORDENAR PALABRAS" else "ELIGE UNA RESPUESTA",
                            color=Ocean,fontSize=11.sp)
                        Text(task.prompt,color=Deep,fontSize=17.sp,lineHeight=23.sp)
                        if(mode==StudyGame.ORDER) {
                            Card(colors=CardDefaults.cardColors(containerColor=Soft)) {
                                Text(if(order.isEmpty())"Toca las palabras de abajo"
                                    else order.map { task.tokenOrder[it] }.joinToString(" "),
                                    modifier=Modifier.fillMaxWidth().padding(13.dp),color=Deep,fontSize=14.sp)
                            }
                            Row(Modifier.horizontalScroll(rememberScrollState()),
                                horizontalArrangement=Arrangement.spacedBy(6.dp)) {
                                task.tokenOrder.forEachIndexed { i,token ->
                                    if(!order.contains(i)) OutlinedButton(
                                        onClick={order=order+i},enabled=!validated) {
                                        Text(token,fontSize=11.sp)
                                    }
                                }
                            }
                            OutlinedButton(onClick={order=emptyList()},
                                enabled=!validated&&order.isNotEmpty()) { Text("Borrar orden") }
                        } else {
                            task.options.forEach { option ->
                                OutlinedButton(onClick={selected=option},enabled=!validated,
                                    modifier=Modifier.fillMaxWidth()) {
                                    Text((if(selected==option)"● " else "○ ")+option,fontSize=12.sp)
                                }
                            }
                        }
                        if(validated) {
                            Card(colors=CardDefaults.cardColors(containerColor=if(correct)Mint else Orange)) {
                                Column(Modifier.padding(12.dp)) {
                                    Text(if(correct)"✓ Respuesta del modelo correcta"
                                        else "Compara con la respuesta de referencia",color=Deep,fontSize=14.sp)
                                    Text(task.answer,color=Deep,fontSize=13.sp)
                                    Text(task.explanation,color=Muted,fontSize=12.sp)
                                }
                            }
                        } else Button(onClick={
                            validated=true
                            tracker.record(lesson.id,task.skill,correct)
                            if(correct){points+=10;streak++}else streak=0
                        },enabled=if(mode==StudyGame.ORDER)order.size==task.tokenOrder.size
                            else selected.isNotBlank(),modifier=Modifier.fillMaxWidth()) {
                            Text("Comprobar respuesta")
                        }
                    }
                    if(validated || (mode==StudyGame.MEMORY && reviewed)) {
                        Button(onClick={
                            if(round < SupplementaryPractice.COUNT-1) round++ else finished=true
                        },modifier=Modifier.fillMaxWidth()) {
                            Text(if(round==SupplementaryPractice.COUNT-1)"Finalizar serie"
                                else "Siguiente ejercicio")
                        }
                    }
                }
            }
        }
        Text("Las puntuaciones se basan en respuestas modelo; la variedad de distractores y los textos "
            +"requieren revisión educativa externa.",color=Muted,fontSize=11.sp)
    }
}

@Composable
private fun Reading(lesson: StudyLesson, levelLessons: List<StudyLesson>) {
    val items=remember(lesson.id) { SupplementaryPractice.reading(lesson,levelLessons) }
    var index by rememberSaveable(lesson.id) { mutableStateOf(0) }
    var answers by rememberSaveable(lesson.id) {
        mutableStateOf(List(SupplementaryPractice.COUNT) { "" })
    }
    var checked by rememberSaveable(lesson.id) {
        mutableStateOf(List(SupplementaryPractice.COUNT) { false })
    }
    val item=items[index]
    val completed=checked.count { it }
    val correct=items.indices.count { checked[it] && answers[it]==items[it].answer }
    Column(verticalArrangement=Arrangement.spacedBy(12.dp)) {
        AreaHeading(Art.Reading,"Reading · 16 ejercicios","Comprensión y lectura bilingüe")
        Card(colors=CardDefaults.cardColors(containerColor=Color.White),
            shape=RoundedCornerShape(17.dp)) {
            Column(Modifier.padding(15.dp),verticalArrangement=Arrangement.spacedBy(10.dp)) {
                Text("TEXTO DE LA UNIDAD · "+lesson.level,color=Ocean,fontSize=11.sp)
                Text(lesson.contextualText,color=Deep,fontSize=14.sp,lineHeight=22.sp)
                HorizontalDivider()
                lesson.dialogue.forEach { Text(it,color=Deep,fontSize=13.sp) }
            }
        }
        LinearProgressIndicator(progress={completed.toFloat()/SupplementaryPractice.COUNT},
            modifier=Modifier.fillMaxWidth())
        Text("Ejercicio "+(index+1)+" de 16 · Comprobados: "+completed+
            " · Respuestas correctas: "+correct,color=Ocean,fontSize=12.sp)
        Card(colors=CardDefaults.cardColors(containerColor=Purple)) {
            Column(Modifier.padding(15.dp),verticalArrangement=Arrangement.spacedBy(10.dp)) {
                Text(item.prompt,color=Deep,fontSize=16.sp)
                if(item.stimulus.isNotBlank())Text(item.stimulus,color=Deep,fontSize=14.sp,lineHeight=22.sp)
                item.options.forEach { option ->
                    OutlinedButton(onClick={
                        answers=answers.toMutableList().also { it[index]=option }
                    },modifier=Modifier.fillMaxWidth(),enabled=!checked[index]) {
                        Text((if(answers[index]==option)"● " else "○ ")+option,fontSize=12.sp)
                    }
                }
                if(checked[index]) {
                    Text(if(answers[index]==item.answer)"✓ Correcto" else "Solución modelo: "+item.answer,
                        color=Deep,fontSize=13.sp)
                    Text(item.explanation,color=Muted,fontSize=12.sp)
                } else Button(
                    onClick={checked=checked.toMutableList().also { it[index]=true }},
                    enabled=answers[index].isNotBlank(),modifier=Modifier.fillMaxWidth()) {
                    Text("Comprobar")
                }
            }
        }
        Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick={index--},enabled=index>0,modifier=Modifier.weight(1f)) {
                Text("Anterior")
            }
            Button(onClick={index++},enabled=index<SupplementaryPractice.COUNT-1,
                modifier=Modifier.weight(1f)) { Text("Siguiente") }
        }
        Text("Los dos primeros ejercicios comprueban el diálogo y el tema. Los restantes "
            +"trabajan equivalencias bilingües de la unidad y del nivel.",
            color=Muted,fontSize=11.sp)
    }
}

@Composable
private fun Listening(
    lesson: StudyLesson, levelLessons: List<StudyLesson>,
    speak: (String,Float)->Unit, status: String
) {
    val items=remember(lesson.id) { SupplementaryPractice.listening(lesson,levelLessons) }
    var index by rememberSaveable(lesson.id) { mutableStateOf(0) }
    var answers by rememberSaveable(lesson.id) {
        mutableStateOf(List(SupplementaryPractice.COUNT) { "" })
    }
    var checked by rememberSaveable(lesson.id) {
        mutableStateOf(List(SupplementaryPractice.COUNT) { false })
    }
    var reveal by remember(lesson.id,index) { mutableStateOf(false) }
    val item=items[index]
    val done=checked.count { it }
    val correct=items.indices.count { checked[it] && answers[it]==items[it].answer }
    Column(verticalArrangement=Arrangement.spacedBy(12.dp)) {
        AreaHeading(Art.Listening,"Listening · 16 ejercicios","Escucha e identifica el mensaje")
        LinearProgressIndicator(progress={done.toFloat()/SupplementaryPractice.COUNT},
            modifier=Modifier.fillMaxWidth())
        Text("Ejercicio "+(index+1)+" de 16 · Comprobados: "+done+
            " · Correctos: "+correct,color=Ocean,fontSize=12.sp)
        Card(colors=CardDefaults.cardColors(containerColor=Soft)) {
            Column(Modifier.padding(16.dp),verticalArrangement=Arrangement.spacedBy(11.dp)) {
                Text(item.prompt,color=Deep,fontSize=16.sp)
                Row(horizontalArrangement=Arrangement.spacedBy(7.dp)) {
                    Button(onClick={speak(item.audioText,1f)},modifier=Modifier.weight(1f)) {
                        EasyIcon(Art.Audio,Modifier.size(23.dp)); Text("Normal")
                    }
                    OutlinedButton(onClick={speak(item.audioText,0.75f)},modifier=Modifier.weight(1f)) {
                        EasyIcon(Art.Audio,Modifier.size(23.dp)); Text("Lento")
                    }
                }
                Text("Voz sintética de Android: necesita motor de inglés instalado.",
                    color=Muted,fontSize=11.sp)
                if(status.isNotBlank())Text(status,color=Color(0xFFA4532A),fontSize=12.sp)
                OutlinedButton(onClick={reveal=!reveal},modifier=Modifier.fillMaxWidth()) {
                    Text(if(reveal)"Ocultar transcripción" else "Mostrar transcripción accesible")
                }
                if(reveal) {
                    Text(item.audioText,color=Deep,fontSize=14.sp)
                    Text("Con la transcripción visible, esta actividad practica lectura, no escucha.",
                        color=Muted,fontSize=11.sp)
                }
            }
        }
        item.options.forEach { opt ->
            OutlinedButton(onClick={
                answers=answers.toMutableList().also { it[index]=opt }
            },enabled=!checked[index],modifier=Modifier.fillMaxWidth()) {
                Text((if(answers[index]==opt)"● " else "○ ")+opt,fontSize=12.sp)
            }
        }
        if(checked[index]) {
            Card(colors=CardDefaults.cardColors(containerColor=Mint)) {
                Column(Modifier.padding(12.dp)) {
                    Text(if(answers[index]==item.answer)"✓ Correcto"
                        else "Respuesta de referencia: "+item.answer,color=Deep,fontSize=13.sp)
                    Text(item.explanation,color=Muted,fontSize=12.sp)
                }
            }
        } else Button(
            onClick={checked=checked.toMutableList().also { it[index]=true }},
            enabled=answers[index].isNotBlank(),modifier=Modifier.fillMaxWidth()) {
            Text("Comprobar")
        }
        Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick={index--},enabled=index>0,modifier=Modifier.weight(1f)) {
                Text("Anterior")
            }
            Button(onClick={index++},enabled=index<SupplementaryPractice.COUNT-1,
                modifier=Modifier.weight(1f)) { Text("Siguiente") }
        }
    }
}

@Composable
private fun Writing(lesson: StudyLesson) {
    val exercises=remember(lesson.id) { SupplementaryPractice.writing(lesson) }
    var index by rememberSaveable(lesson.id) { mutableStateOf(0) }
    var drafts by rememberSaveable(lesson.id) {
        mutableStateOf(List(SupplementaryPractice.COUNT) { "" })
    }
    var showModel by remember(lesson.id,index) { mutableStateOf(false) }
    val item=exercises[index]
    val done=drafts.count { it.isNotBlank() }
    val words=drafts[index].trim().split(Regex("\\s+"))
        .count { it.isNotBlank() && drafts[index].isNotBlank() }
    Column(verticalArrangement=Arrangement.spacedBy(12.dp)) {
        AreaHeading(Art.Writing,"Writing · 16 ejercicios","Escribe y conserva tus borradores")
        LinearProgressIndicator(progress={done.toFloat()/SupplementaryPractice.COUNT},
            modifier=Modifier.fillMaxWidth())
        Text("Ejercicio "+(index+1)+" de 16 · Con borrador: "+done+
            " · Palabras actuales: "+words,color=Ocean,fontSize=12.sp)
        Card(colors=CardDefaults.cardColors(containerColor=Color.White)) {
            Column(Modifier.padding(16.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
                Text(item.prompt,color=Deep,fontSize=15.sp)
                OutlinedTextField(
                    value=drafts[index],
                    onValueChange={ value ->
                        drafts=drafts.toMutableList().also { it[index]=value.take(3000) }
                    },
                    modifier=Modifier.fillMaxWidth().height(180.dp),
                    placeholder={Text("Escribe tu respuesta aquí...")},
                    label={Text("Mi redacción")}
                )
                Text("Borrador guardado localmente en esta sesión; no se transmite.",
                    color=Muted,fontSize=11.sp)
                Text("Rúbrica de autoevaluación",color=Ocean,fontSize=14.sp)
                lesson.rubric.forEach { Text("□ "+it,color=Deep,fontSize=12.sp) }
                OutlinedButton(onClick={showModel=!showModel}) {
                    Text(if(showModel)"Ocultar referencia" else "Mostrar referencia orientativa")
                }
                if(showModel) {
                    Card(colors=CardDefaults.cardColors(containerColor=Mint)) {
                        Column(Modifier.padding(11.dp)) {
                            Text(item.reference,color=Deep,fontSize=13.sp)
                            Text("Puede haber múltiples respuestas correctas. No se califica automáticamente.",
                                color=Muted,fontSize=11.sp)
                        }
                    }
                }
            }
        }
        Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick={index--},enabled=index>0,modifier=Modifier.weight(1f)) {
                Text("Anterior")
            }
            Button(onClick={index++},enabled=index<SupplementaryPractice.COUNT-1,
                modifier=Modifier.weight(1f)) { Text("Siguiente") }
        }
        Text("La corrección lingüística requiere evaluación con una rúbrica; "
            +"el indicador de avance solo cuenta borradores no vacíos.",color=Muted,fontSize=11.sp)
    }
}
