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
import com.easyenglish.app.data.PracticeProgressStore
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
    val dialogueBank=remember(level) { StudyLibrary.levelDialogues(context,level) }
    val titleBank=remember(level) { StudyLibrary.levelTitles(context,level) }
    val progressStore=remember(context) { PracticeProgressStore(context) }
    var tab by rememberSaveable(level,unit) { mutableStateOf("Presentación") }
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
            TextButton(onClick=onBack) { Text("‹ Regresar a las unidades",color=Ocean) }
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
                    listOf("Presentación","Prácticas","Lectura","Escucha","Escritura").forEach { name ->
                        FilterChip(selected=tab==name,onClick={tab=name},label={Text(name,fontSize=12.sp)})
                    }
                }
            }
            when(tab){
                "Presentación" -> item { Presentation(lesson) }
                "Prácticas" -> item { Games(lesson,lines,dialogueBank,titleBank,progressStore) }
                "Lectura" -> item { Reading(lesson,dialogueBank,titleBank) }
                "Escucha" -> item { Listening(lesson,lines,speak,audioStatus) }
                "Escritura" -> item { Writing(lesson) }
            }
            item {
                Card(colors=CardDefaults.cardColors(containerColor=Soft),shape=RoundedCornerShape(15.dp)) {
                    Column(Modifier.padding(14.dp),verticalArrangement=Arrangement.spacedBy(6.dp)) {
                        Text("Aprender con criterio",color=Deep,fontSize=14.sp)
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
                    Text("PRESENTACIÓN INTERACTIVA · "+(slide+1)+"/8",color=Color(0xFFE9F3FF),fontSize=11.sp)
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
private fun Games(lesson: StudyLesson, bank: List<StudyLine>, dialogueBank: List<String>, titleBank: List<String>, tracker: PracticeProgressStore) {
    var mode by remember(lesson.id) { mutableStateOf(StudyGame.MEANING) }
    var round by remember(lesson.id) { mutableStateOf(0) }
    var points by remember(lesson.id) { mutableStateOf(0) }
    var streak by remember(lesson.id) { mutableStateOf(0) }
    val recent=remember(lesson.id,mode) { mutableStateListOf<String>() }
    val task=remember(lesson.id,mode,round) {
        PracticeGenerator.create(lesson,bank,mode,dialogueBank,titleBank,recent.toSet())
    }
    var selected by remember(task) { mutableStateOf("") }
    var validated by remember(task) { mutableStateOf(false) }
    var flip by remember(task) { mutableStateOf(false) }
    var order by remember(task) { mutableStateOf(listOf<Int>()) }
    val correct=if(mode==StudyGame.ORDER) {
        order.map { task.tokenOrder[it] }.joinToString(" ")==task.answer
    } else selected==task.answer

    Column(verticalArrangement=Arrangement.spacedBy(12.dp)) {
        Heading("Juegos con generación automática","¡Entrena de otra manera!")
        Text("Ejercicios variados de tu nivel. Los intentos se crean al momento y no ocupan almacenamiento permanente.",color=Muted,fontSize=12.sp)
        Row(Modifier.horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(6.dp)) {
            StudyGame.entries.forEach { game ->
                FilterChip(selected=mode==game,onClick={mode=game;round=0},label={Text(game.label,fontSize=12.sp)})
            }
        }
        Row(horizontalArrangement=Arrangement.spacedBy(10.dp)) {
            Text("⭐ Puntos: "+points,color=Ocean,fontSize=12.sp)
            Text("🔥 Racha: "+streak,color=Deep,fontSize=12.sp)
            Text(tracker.nextReviewLabel(lesson.id,"grammar"),color=Muted,fontSize=10.sp)
        }
        Card(colors=CardDefaults.cardColors(containerColor=Color.White),shape=RoundedCornerShape(19.dp)) {
            Column(Modifier.padding(16.dp),verticalArrangement=Arrangement.spacedBy(11.dp)) {
                if(mode==StudyGame.MEMORY){
                    Text("Tarjeta de repaso · Toca para descubrir el significado",color=Muted,fontSize=12.sp)
                    Box(Modifier.fillMaxWidth().height(150.dp).background(if(flip) Mint else Soft,RoundedCornerShape(15.dp))
                        .clickable { flip=!flip },contentAlignment=Alignment.Center) {
                        Text(if(flip)task.spanish else task.english,color=Deep,fontSize=20.sp,textAlign=TextAlign.Center,modifier=Modifier.padding(12.dp))
                    }
                    Text("Recupera el significado antes de voltear la tarjeta.",color=Muted,fontSize=11.sp)
                } else {
                    Text(if(mode==StudyGame.ORDER)"ORDENAR PALABRAS" else "ELIGE UNA RESPUESTA",color=Ocean,fontSize=11.sp)
                    Text(task.prompt,color=Deep,fontSize=18.sp,lineHeight=24.sp)
                    if(mode==StudyGame.ORDER) {
                        Card(colors=CardDefaults.cardColors(containerColor=Soft)) {
                            Text(
                                if(order.isEmpty())"Toca las palabras de abajo"
                                else order.map { task.tokenOrder[it] }.joinToString(" "),
                                modifier=Modifier.fillMaxWidth().padding(13.dp),
                                color=Deep,fontSize=14.sp
                            )
                        }
                        Row(Modifier.horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(6.dp)) {
                            task.tokenOrder.forEachIndexed { i,token ->
                                if(!order.contains(i)) OutlinedButton(onClick={if(!validated)order=order+i else Unit}) {
                                    Text(token,fontSize=11.sp)
                                }
                            }
                        }
                        OutlinedButton(onClick={order=emptyList()},enabled=!validated&&order.isNotEmpty()) { Text("Borrar orden") }
                    } else {
                        task.options.forEach { option ->
                            OutlinedButton(
                                onClick={if(!validated)selected=option else Unit},
                                modifier=Modifier.fillMaxWidth(),
                                enabled=!validated
                            ) {
                                Text((if(selected==option)"● " else "○ ")+option,fontSize=12.sp,textAlign=TextAlign.Start)
                            }
                        }
                    }
                    if(validated) {
                        Card(colors=CardDefaults.cardColors(containerColor=if(correct) Mint else Orange)) {
                            Column(Modifier.padding(12.dp)) {
                                Text(if(correct)"¡Correcto! +10 puntos" else "Casi. Esta es la solución:",color=Deep,fontSize=14.sp)
                                Text(task.english,color=Deep,fontSize=13.sp)
                                Text(task.spanish,color=Muted,fontSize=12.sp)
                                Text("Explicación: "+task.explanation,color=Muted,fontSize=11.sp)
                                Text("Regla: "+lesson.specificRule,color=Muted,fontSize=11.sp)
                            }
                        }
                    }
                    if(!validated) {
                        Button(
                            enabled=if(mode==StudyGame.ORDER) order.size==task.tokenOrder.size else selected.isNotBlank(),
                            onClick={validated=true;tracker.record(lesson.id,task.skill,correct);if(correct){points+=10;streak++}else streak=0},
                            modifier=Modifier.fillMaxWidth(),
                            colors=ButtonDefaults.buttonColors(containerColor=Ocean)
                        ) { Text("Comprobar respuesta") }
                    }
                }
                if(validated || mode==StudyGame.MEMORY) Button(
                    onClick={
                        recent.add(task.answer)
                        if(recent.size>3) recent.removeAt(0)
                        round++
                    },
                    modifier=Modifier.fillMaxWidth(),
                    colors=ButtonDefaults.buttonColors(containerColor=Ocean)
                ) { Text("Crear otra práctica ↻") }
            }
        }
    }
}

@Composable
private fun Reading(lesson: StudyLesson, dialogueBank: List<String>, titleBank: List<String>) {
    var translations by remember(lesson.id) { mutableStateOf(false) }
    var answered by remember(lesson.id) { mutableStateOf("") }
    var titleChoice by remember(lesson.id) { mutableStateOf("") }
    var reflection by rememberSaveable(lesson.id) { mutableStateOf("") }
    val correct=lesson.dialogue[1].substringAfter(": ").trim()
    val options=remember(lesson.id) {
        (dialogueBank.filter { it!=correct }.distinct().shuffled().take(3)+correct).shuffled()
    }
    Column(verticalArrangement=Arrangement.spacedBy(12.dp)) {
        Heading("Comprensión lectora","Lee el texto y el diálogo")
        Card(colors=CardDefaults.cardColors(containerColor=Color.White),shape=RoundedCornerShape(20.dp)) {
            Column(Modifier.padding(17.dp),verticalArrangement=Arrangement.spacedBy(13.dp)) {
                Text("LECTURA · "+lesson.level,color=Ocean,fontSize=11.sp)
                Text(lesson.contextualText,color=Deep,fontSize=15.sp,lineHeight=24.sp)
                if(lesson.level=="B2" || lesson.level=="C1" || lesson.level=="C2") {
                    Text("Identifica la tesis, la evidencia, una limitación y una inferencia posible.",
                        color=Muted,fontSize=12.sp)
                }
                HorizontalDivider()
                Text("DIÁLOGO",color=Ocean,fontSize=11.sp)
                lesson.dialogue.forEach { line -> Text(line,color=Deep,fontSize=14.sp) }
                OutlinedButton(onClick={translations=!translations}) {
                    Text(if(translations)"Ocultar modelos" else "Mostrar modelos bilingües")
                }
                if(translations)lesson.examples.take(2).forEach {
                    Text(it.english+" — "+it.spanish,color=Muted,fontSize=12.sp)
                }
            }
        }
        Card(colors=CardDefaults.cardColors(containerColor=Purple)) {
            Column(Modifier.padding(15.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) {
                Text("¿Qué respondió la segunda persona?",color=Deep,fontSize=15.sp)
                options.forEach { v ->
                    OutlinedButton(onClick={answered=v},modifier=Modifier.fillMaxWidth()) {
                        Text((if(answered==v)"● " else "○ ")+v,fontSize=12.sp)
                    }
                }
                if(answered.isNotBlank()) Text(
                    if(answered==correct)"Correcto: identificaste la respuesta explícita."
                        else "Revisa el diálogo. La respuesta es: "+correct,
                    color=Deep,fontSize=12.sp
                )
            }
        }
        Card(colors=CardDefaults.cardColors(containerColor=Mint)) {
            Column(Modifier.padding(14.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) {
                Text("Segunda comprobación de lectura",color=Deep,fontSize=15.sp)
                Text(lesson.readingChecks[1].prompt,color=Muted,fontSize=12.sp)
                val otherTitles=remember(lesson.id) {
                    (titleBank.filter { it!=lesson.title }.shuffled().take(3)+lesson.title).shuffled()
                }
                otherTitles.forEach { title ->
                    OutlinedButton(onClick={titleChoice=title},modifier=Modifier.fillMaxWidth()) {
                        Text((if(titleChoice==title)"● " else "○ ")+title,fontSize=12.sp)
                    }
                }
                if(titleChoice.isNotBlank()) Text(
                    if(titleChoice==lesson.title)"Correcto: identificaste el tema central."
                    else "Revisa el encabezado: "+lesson.title,
                    color=Deep,fontSize=12.sp
                )
            }
        }
        Card(colors=CardDefaults.cardColors(containerColor=Color.White)) {
            Column(Modifier.padding(14.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) {
                Text("Lectura crítica: reflexión libre",color=Deep,fontSize=15.sp)
                Text(if(lesson.level=="A0"||lesson.level=="A1"||lesson.level=="A2")
                    "¿Qué sucede en el diálogo? Responde con una oración."
                    else "Resume la idea principal del texto y explica qué información falta para verificarla.",
                    color=Muted,fontSize=12.sp)
                OutlinedTextField(
                    value=reflection,onValueChange={reflection=it.take(1000)},
                    modifier=Modifier.fillMaxWidth(),
                    label={Text("Mi interpretación")}
                )
                Text("Esta reflexión no se corrige automáticamente.",color=Muted,fontSize=11.sp)
            }
        }
    }
}

@Composable
private fun Listening(lesson: StudyLesson, bank: List<StudyLine>, speak: (String,Float)->Unit, status: String) {
    var index by remember(lesson.id) { mutableStateOf(0) }
    var reveal by remember(lesson.id,index) { mutableStateOf(false) }
    var choice by remember(lesson.id,index) { mutableStateOf("") }
    val line=lesson.examples[index%lesson.examples.size]
    val other=bank.map { it.spanish }.filter { it!=line.spanish }.distinct().take(3)
    val options=remember(lesson.id,index) { (other+line.spanish).shuffled() }
    Column(verticalArrangement=Arrangement.spacedBy(12.dp)) {
        Heading("Listening · inglés americano","Escucha y comprende")
        Card(colors=CardDefaults.cardColors(containerColor=Soft),shape=RoundedCornerShape(20.dp)) {
            Column(Modifier.fillMaxWidth().padding(18.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(12.dp)) {
                Text("🎧  Audio de práctica "+(index+1)+"/4",color=Deep,fontSize=17.sp)
                Row(horizontalArrangement=Arrangement.spacedBy(6.dp)) {
                    Button(onClick={speak(line.english,1f)},colors=ButtonDefaults.buttonColors(containerColor=Ocean)) {
                        Text("▶ Normal")
                    }
                    OutlinedButton(onClick={speak(line.english,0.7f)}) { Text("▶ Lento") }
                }
                OutlinedButton(onClick={speak(lesson.dialogue.joinToString(". "),1f)}) {
                    Text("▶ Escuchar el diálogo")
                }
                Text("Voz sintética de Android. Requiere que el dispositivo tenga instalada una voz compatible.",color=Muted,fontSize=11.sp,textAlign=TextAlign.Center)
                if(status.isNotBlank())Text(status,color=Color(0xFFA4532A),fontSize=11.sp)
                if(reveal)Text(line.english,color=Deep,fontSize=15.sp)
                OutlinedButton(onClick={reveal=!reveal}) { Text(if(reveal)"Ocultar transcripción" else "Ver transcripción") }
            }
        }
        Text("¿Qué escuchaste?",color=Deep,fontSize=16.sp)
        options.forEach { opt ->
            OutlinedButton(onClick={choice=opt},modifier=Modifier.fillMaxWidth()) {
                Text((if(choice==opt)"● " else "○ ")+opt,fontSize=12.sp)
            }
        }
        if(choice.isNotBlank()) {
            Text(if(choice==line.spanish)"¡Correcto! Identificaste el mensaje." else "Repite el audio. Solución: "+line.spanish,color=Deep,fontSize=12.sp)
        }
        Button(onClick={index=(index+1)%lesson.examples.size},modifier=Modifier.fillMaxWidth()) { Text("Otra escucha ↻") }
    }
}

@Composable
private fun Writing(lesson: StudyLesson) {
    var response by rememberSaveable(lesson.id) { mutableStateOf("") }
    var showModel by remember(lesson.id) { mutableStateOf(false) }
    val words=response.trim().split(Regex("\\s+")).count { it.isNotBlank() && response.isNotBlank() }
    Column(verticalArrangement=Arrangement.spacedBy(12.dp)) {
        Heading("Producción escrita","Escribe con tus propias palabras")
        Card(colors=CardDefaults.cardColors(containerColor=Color.White)) {
            Column(Modifier.padding(17.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
                Text(lesson.writing,color=Deep,fontSize=15.sp)
                OutlinedTextField(
                    value=response,onValueChange={response=it.take(3000)},
                    modifier=Modifier.fillMaxWidth().height(180.dp),
                    placeholder={Text("Escribe tu respuesta aquí...")},
                    label={Text("Mi redacción")}
                )
                Text(words.toString()+" palabras · borrador no enviado a ningún servidor",color=Muted,fontSize=11.sp)
                Text("Rúbrica orientativa de "+lesson.level,color=Ocean,fontSize=14.sp)
                lesson.rubric.forEach { Text("□ "+it,color=Deep,fontSize=12.sp) }
                Text("Lista de revisión personal",color=Ocean,fontSize=14.sp)
                listOf(
                    "¿La idea se entiende sin ayuda?",
                    "¿Usaste vocabulario y estructuras del tema?",
                    "¿Revisaste orden, concordancia y puntuación?",
                    "¿Puedes explicar por qué elegiste esa forma?"
                ).forEach { Text("□ "+it,color=Deep,fontSize=12.sp) }
                OutlinedButton(onClick={showModel=!showModel}) { Text(if(showModel)"Ocultar ejemplo" else "Mostrar un modelo") }
                if(showModel){
                    Card(colors=CardDefaults.cardColors(containerColor=Mint)) {
                        Column(Modifier.padding(11.dp),verticalArrangement=Arrangement.spacedBy(6.dp)) {
                            Text(lesson.examples.take(2).joinToString(" ") { it.english },color=Deep,fontSize=14.sp)
                            Text("Este modelo no es la única respuesta correcta.",color=Muted,fontSize=11.sp)
                        }
                    }
                }
            }
        }
        Text("Sin calificación automática de redacción: una corrección lingüística seria requiere rúbricas y evaluación adecuada.",color=Muted,fontSize=11.sp)
    }
}
