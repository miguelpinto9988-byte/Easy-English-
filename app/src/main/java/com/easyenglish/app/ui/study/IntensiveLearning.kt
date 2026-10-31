package com.easyenglish.app.ui.study

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.easyenglish.app.data.PracticeProgressStore
import com.easyenglish.app.data.StudyLesson
import kotlin.random.Random

private val Navy=Color(0xFF143878)
private val Blue=Color(0xFF266DDD)
private val Muted=Color(0xFF647797)
private val Pale=Color(0xFFE9F3FF)
private val Mint=Color(0xFFE0F6EF)
private val Amber=Color(0xFFFFF0DA)

@Composable
fun IntensiveLearning(lesson: StudyLesson, onSpeak: (String,Float)->Unit) {
    val context=LocalContext.current
    val stats=remember(context) { PracticeProgressStore(context) }
    val prefs=remember(lesson.id) {
        context.getSharedPreferences("easy_english_intensive_completion",0)
    }
    val completed=remember(lesson.id) {
        mutableStateListOf<String>().apply {
            addAll(prefs.getStringSet("done:"+lesson.id,emptySet()) ?: emptySet())
        }
    }
    var sessionIndex by rememberSaveable(lesson.id) { mutableStateOf(0) }
    var taskIndex by rememberSaveable(lesson.id,sessionIndex) { mutableStateOf(0) }
    val session=lesson.intensiveSessions[sessionIndex]
    val task=session.tasks[taskIndex]
    var selected by remember(task.id) { mutableStateOf("") }
    var checked by remember(task.id) { mutableStateOf(false) }
    var showModel by remember(task.id) { mutableStateOf(false) }
    var writing by remember(task.id) { mutableStateOf("") }
    var showHint by remember(task.id) { mutableStateOf(false) }
    val words=remember(task.id) { task.answer.trim().split(Regex("\\s+")).filter(String::isNotEmpty) }
    val mixed=remember(task.id) { words.mapIndexed { index, word -> index to word }.shuffled(Random(task.id.hashCode())) }
    var chosen by remember(task.id) { mutableStateOf(listOf<Int>()) }
    val closed=task.kind in setOf("choice","listen","read","arrange")
    val provided=if(task.kind=="arrange")chosen.joinToString(" ") { words[it] } else selected
    val correct=provided.trim().replace(Regex("\\s+")," ")==task.answer.trim().replace(Regex("\\s+")," ")

    Column(verticalArrangement=Arrangement.spacedBy(12.dp)) {
        Text("RUTA INTENSIVA · OCHO SESIONES",color=Blue,fontSize=11.sp)
        Text("64 actividades en esta unidad",color=Navy,fontSize=20.sp)
        Text("Las preguntas de opción y orden se corrigen automáticamente. Las respuestas orales o escritas se comparan con un modelo; no reciben calificación automática.",
            color=Muted,fontSize=12.sp)
        LinearProgressIndicator(progress={completed.size.coerceAtMost(64)/64f},modifier=Modifier.fillMaxWidth())
        Text(completed.size.coerceAtMost(64).toString()+" de 64 actividades realizadas",color=Muted,fontSize=11.sp)
        Row(Modifier.horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(6.dp)) {
            lesson.intensiveSessions.forEachIndexed { i,block ->
                FilterChip(selected=i==sessionIndex,onClick={sessionIndex=i;taskIndex=0},
                    label={Text(block.title,fontSize=11.sp)})
            }
        }
        Card(colors=CardDefaults.cardColors(containerColor=Pale),shape=RoundedCornerShape(17.dp)) {
            Column(Modifier.padding(14.dp),verticalArrangement=Arrangement.spacedBy(4.dp)) {
                Text(session.title,color=Navy,fontSize=18.sp)
                Text(session.subtitle,color=Muted,fontSize=12.sp)
                Text("Actividad "+(taskIndex+1)+" de 8 · "+task.skill,color=Blue,fontSize=11.sp)
            }
        }
        Card(colors=CardDefaults.cardColors(containerColor=Color.White),shape=RoundedCornerShape(18.dp)) {
            Column(Modifier.padding(15.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
                Text(task.prompt,color=Navy,fontSize=16.sp)
                if(task.stimulus.isNotBlank())Text(task.stimulus,color=Muted,fontSize=14.sp,lineHeight=21.sp)
                if(task.kind=="listen") {
                    Button(onClick={onSpeak(task.audioText,1f)},modifier=Modifier.fillMaxWidth(),
                        colors=ButtonDefaults.buttonColors(containerColor=Blue)) { Text("▶ Escuchar") }
                    OutlinedButton(onClick={onSpeak(task.audioText,0.75f)},modifier=Modifier.fillMaxWidth()) {
                        Text("▶ Repetir lentamente")
                    }
                }
                if(task.kind=="speak" && task.answer.isNotBlank()) {
                    OutlinedButton(onClick={onSpeak(task.answer,0.85f)}) {
                        Text("▶ Escuchar modelo")
                    }
                }
                if(task.hint.isNotBlank()) {
                    TextButton(onClick={showHint=!showHint}) {
                        Text(if(showHint)"Ocultar pista" else "Mostrar pista")
                    }
                    if(showHint)Text(task.hint,color=Muted,fontSize=12.sp)
                }
                if(closed) {
                    if(task.kind=="arrange") {
                        Text(if(chosen.isEmpty())"Toca las palabras para ordenarlas."
                            else chosen.joinToString(" ") { words[it] },color=Navy,fontSize=14.sp)
                        mixed.chunked(3).forEach { group ->
                            Row(horizontalArrangement=Arrangement.spacedBy(5.dp)) {
                                group.forEach { (index,word) ->
                                    if(index !in chosen) OutlinedButton(
                                        onClick={chosen=chosen+index},enabled=!checked
                                    ) { Text(word,fontSize=11.sp) }
                                }
                            }
                        }
                        OutlinedButton(onClick={chosen=emptyList()},enabled=!checked) { Text("Reiniciar orden") }
                    } else {
                        task.options.forEach { option ->
                            OutlinedButton(onClick={selected=option},modifier=Modifier.fillMaxWidth(),enabled=!checked) {
                                Text((if(selected==option)"● " else "○ ")+option,fontSize=12.sp,
                                    textAlign=TextAlign.Start)
                            }
                        }
                    }
                    if(!checked)Button(
                        enabled=if(task.kind=="arrange")chosen.size==words.size else selected.isNotBlank(),
                        onClick={checked=true;stats.record(lesson.id,task.skill,correct)},
                        modifier=Modifier.fillMaxWidth(),
                        colors=ButtonDefaults.buttonColors(containerColor=Blue)
                    ) { Text("Comprobar") }
                    if(checked) {
                        Card(colors=CardDefaults.cardColors(containerColor=if(correct)Mint else Amber)) {
                            Column(Modifier.padding(13.dp),verticalArrangement=Arrangement.spacedBy(7.dp)) {
                                Text(if(correct)"✓ ¡Correcto!" else "Compara con el modelo",color=Navy,fontSize=14.sp)
                                Text(task.answer,color=Navy,fontSize=13.sp)
                                if(task.explanation.isNotBlank())Text(task.explanation,color=Muted,fontSize=12.sp)
                            }
                        }
                    }
                } else {
                    if(task.kind in setOf("write","observe","reflect")) {
                        OutlinedTextField(value=writing,onValueChange={writing=it.take(1600)},
                            modifier=Modifier.fillMaxWidth().height(128.dp),
                            label={Text("Tu respuesta, sin enviarla a internet")})
                    }
                    if(task.kind=="speak")
                        Text("Practica oralmente: no se graba ni se transmite tu voz.",color=Muted,fontSize=12.sp)
                    OutlinedButton(onClick={showModel=!showModel},modifier=Modifier.fillMaxWidth()) {
                        Text(if(showModel)"Ocultar referencia" else "Comparar con un modelo")
                    }
                    if(showModel)Card(colors=CardDefaults.cardColors(containerColor=Mint)) {
                        Column(Modifier.padding(13.dp),verticalArrangement=Arrangement.spacedBy(7.dp)) {
                            Text("Referencia orientativa",color=Navy,fontSize=14.sp)
                            Text(task.answer,color=Navy,fontSize=13.sp)
                            Text("Pueden existir otras respuestas correctas.",color=Muted,fontSize=11.sp)
                        }
                    }
                }
            }
        }
        Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick={
                    if(taskIndex>0) taskIndex--
                    else if(sessionIndex>0) { sessionIndex--;taskIndex=7 }
                },
                enabled=taskIndex>0 || sessionIndex>0,
                modifier=Modifier.weight(1f)
            ) { Text("Anterior") }
            Button(
                onClick={
                    if(task.id !in completed) {
                        completed.add(task.id)
                        prefs.edit().putStringSet("done:"+lesson.id,completed.toSet()).apply()
                    }
                    if(taskIndex<7)taskIndex++
                    else if(sessionIndex<lesson.intensiveSessions.lastIndex) { sessionIndex++;taskIndex=0 }
                    else {sessionIndex=0;taskIndex=0}
                },
                modifier=Modifier.weight(1f),colors=ButtonDefaults.buttonColors(containerColor=Blue)
            ) { Text(if(sessionIndex==7&&taskIndex==7)"Finalizar" else "Siguiente") }
        }
        Text("Este recorrido usa ejemplos revisables, combinaciones y actividades abiertas; no equivale a 64 frases nuevas ni a una certificación lingüística.",
            color=Muted,fontSize=11.sp)
    }
}
