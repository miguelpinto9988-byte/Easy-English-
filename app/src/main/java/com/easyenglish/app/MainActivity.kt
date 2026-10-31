package com.easyenglish.app

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.easyenglish.app.data.ContentStore
import com.easyenglish.app.data.DownloadPack
import com.easyenglish.app.data.LevelCatalog
import com.easyenglish.app.data.ProfileStore
import com.easyenglish.app.data.PracticeProgressStore
import com.easyenglish.app.data.UserProfile
import com.easyenglish.app.ui.splash.EasyEnglishSplash
import com.easyenglish.app.ui.study.StudyExperience
import com.easyenglish.app.ui.theme.EasyEnglishTheme
import kotlinx.coroutines.launch
import java.util.Locale
import android.speech.tts.TextToSpeech

private val Navy = Color(0xFF143878)
private val Blue = Color(0xFF266DDD)
private val Pale = Color(0xFFF0F6FF)
private val Aqua = Color(0xFF34C5DE)
private val Gray = Color(0xFF617797)

private enum class Page { PROFILE, PLACEMENT, REPORT, HOME, LEVELS, UNITS, LESSON, PROGRESS, SETTINGS, DOWNLOADS }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(android.graphics.Color.WHITE, android.graphics.Color.WHITE),
            navigationBarStyle = SystemBarStyle.light(android.graphics.Color.WHITE, android.graphics.Color.WHITE)
        )
        setContent {
            EasyEnglishTheme {
                var splashDone by rememberSaveable { mutableStateOf(false) }
                if (!splashDone) {
                    EasyEnglishSplash { splashDone = true }
                } else {
                    EasyEnglishApplication()
                }
            }
        }
    }
}

@Composable
private fun EasyEnglishApplication() {
    val context = LocalContext.current
    val prefs = remember { ProfileStore(context) }
    val contentStore = remember { ContentStore(context) }
    var profile by remember { mutableStateOf(prefs.load()) }
    var page by remember { mutableStateOf(if (profile.saved) Page.HOME else Page.PROFILE) }
    var level by remember { mutableStateOf("A0") }
    var unitNumber by remember { mutableStateOf(0) }
    var message by remember { mutableStateOf("") }
    var reduceMotion by remember { mutableStateOf(prefs.reduceMotion()) }
    var enableAudio by remember { mutableStateOf(prefs.enableAudio()) }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navigate: (Page) -> Unit = { destination ->
        page = destination
        scope.launch { drawerState.close() }
    }
    val pickPhoto = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            val result = prefs.copyPhotoAsPng(uri)
            if (result != null) {
                profile = profile.copy(photoPath = result)
                message = ""
            } else message = "No se pudo procesar la foto. Selecciona otra imagen."
        }
    }
    val saveProfile: (Boolean) -> Unit = { beginA0 ->
        if (profile.name.isBlank()) {
            message = "Escribe un nombre o un apodo para guardar el perfil."
        } else {
            profile = profile.copy(saved = true, placementSkipped = beginA0 || profile.placementSkipped)
            prefs.save(profile)
            message = ""
            navigate(Page.HOME)
        }
    }
    val menu: @Composable () -> Unit = {
        ModalDrawerSheet(
            modifier = Modifier
                .width(322.dp)
                .statusBarsPadding()
                .navigationBarsPadding(),
            drawerContainerColor = Color(0xFFF9FBFF)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 18.dp)
            ) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                            .background(Brush.horizontalGradient(listOf(Color(0xFF103B99), Color(0xFF1870D3), Color(0xFF49C8E9))))
                            .padding(start = 20.dp, end = 18.dp, top = 15.dp, bottom = 21.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(13.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Avatar(profile.photoPath, 66)
                                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(profile.name.ifBlank { "Mi perfil" }, color = Color.White, fontSize = 19.sp)
                                    Text("Ruta " + profile.level + " · desde cero", color = Color(0xFFE2F1FF), fontSize = 11.sp)
                                }
                                TextButton(onClick = { scope.launch { drawerState.close() } }) {
                                    Text("×", color = Color.White, fontSize = 27.sp)
                                }
                            }
                            Text("🦊 Tu espacio en Easy English", color = Color.White, fontSize = 12.sp)
                        }
                    }
                }
                item { DrawerSection("APRENDIZAJE") }
                item { DrawerOption(R.drawable.nav_home, "Inicio", "Panel principal", page == Page.HOME) { navigate(Page.HOME) } }
                item { DrawerOption(R.drawable.nav_levels, "Niveles y unidades", "A0 a C2 · 156 unidades", page == Page.LEVELS || page == Page.UNITS) { navigate(Page.LEVELS) } }
                item { DrawerOption(R.drawable.nav_book, "Mi clase", "Volver a la lección", page == Page.LESSON) { navigate(Page.LESSON) } }
                item { DrawerOption(R.drawable.nav_progress, "Mi progreso", "Resumen de tu ruta", page == Page.PROGRESS) { navigate(Page.PROGRESS) } }
                item { HorizontalDivider(Modifier.padding(horizontal = 18.dp, vertical = 8.dp), color = Color(0xFFE2EAFA)) }
                item { DrawerSection("PERFIL Y EVALUACIÓN") }
                item { DrawerOption(R.drawable.nav_profile, "Editar perfil", "Foto, nombre y preferencias", page == Page.PROFILE) { navigate(Page.PROFILE) } }
                item { DrawerOption(R.drawable.nav_exam, "Prueba de clasificación", "Evaluar habilidades · demostración", page == Page.PLACEMENT) { navigate(Page.PLACEMENT) } }
                item { DrawerOption(R.drawable.nav_settings, "Preferencias", "Accesibilidad y sonido", page == Page.SETTINGS) { navigate(Page.SETTINGS) } }
                item { DrawerOption(R.drawable.nav_download, "Descargar contenidos", "Más temas y audios", page == Page.DOWNLOADS) { navigate(Page.DOWNLOADS) } }
                item {
                    Text(
                        "🔒 Tu perfil se almacena en este dispositivo. Las pruebas actuales son demostrativas.",
                        modifier = Modifier.padding(horizontal = 22.dp, vertical = 17.dp),
                        color = Gray, fontSize = 11.sp
                    )
                }
            }
        }
    }
    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = profile.saved,
        drawerContent = menu
    ) {
        Scaffold(
            containerColor = Color(0xFFF7FAFF),
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .statusBarsPadding()
                        .padding(horizontal = 17.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier.size(40.dp).clip(RoundedCornerShape(13.dp))
                                .background(Color(0xFFE4F2FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(painterResource(R.drawable.fox_mascot), null, Modifier.size(40.dp))
                        }
                        Text("Easy English", color = Color(0xFF1554B5), fontSize = 20.sp)
                    }
                    if (profile.saved) {
                        Box(Modifier.clickable { scope.launch { drawerState.open() } }) {
                            Avatar(profile.photoPath, 46)
                        }
                    }
                }
            }
        ) { padding ->
            Box(Modifier.fillMaxSize().padding(padding)) {
                when (page) {
                    Page.PROFILE -> ProfilePage(
                        profile = profile, error = message,
                        onEdit = { profile = it; message = "" },
                        onPickPhoto = { pickPhoto.launch("image/*") },
                        onStartTest = { navigate(Page.PLACEMENT) },
                        onSkip = { saveProfile(true) },
                        onSave = { saveProfile(false) }
                    )
                    Page.PLACEMENT -> PlacementPage(
                        profile = profile,
                        onBack = { navigate(Page.PROFILE) },
                        onDone = { navigate(Page.REPORT) }
                    )
                    Page.REPORT -> ReportPage(
                        onSave = { saveProfile(false) },
                        onSkip = { saveProfile(true) }
                    )
                    Page.HOME -> HomePage(profile, onLevels = { navigate(Page.LEVELS) }, onClass = {
                        level = "A1"; unitNumber = 2; navigate(Page.LESSON)
                    }, onDownloads = { navigate(Page.DOWNLOADS) })
                    Page.LEVELS -> LevelsPage(onLevel = { level = it; navigate(Page.UNITS) })
                    Page.UNITS -> UnitsPage(level = level, onBack = { navigate(Page.LEVELS) }, onUnit = {
                        unitNumber = it; navigate(Page.LESSON)
                    })
                    Page.LESSON -> StudyExperience(level = level, unit = unitNumber, onBack = { navigate(Page.UNITS) })
                    Page.PROGRESS -> ProgressPage(profile, onLevels = { navigate(Page.LEVELS) })
                    Page.SETTINGS -> SettingsPage(
                        reduceMotion = reduceMotion, enableAudio = enableAudio,
                        onReduce = { reduceMotion = it; prefs.setReduceMotion(it) },
                        onAudio = { enableAudio = it; prefs.setEnableAudio(it) },
                        onDownloads = { navigate(Page.DOWNLOADS) }
                    )
                    Page.DOWNLOADS -> DownloadsPage(contentStore, enableAudio)
                }
            }
        }
    }
}

@Composable
private fun DrawerSection(name: String) {
    Text(
        name,
        modifier = Modifier.padding(start = 23.dp, top = 19.dp, bottom = 9.dp),
        color = Color(0xFF7A8CAD),
        fontSize = 11.sp,
        letterSpacing = 1.sp
    )
}

@Composable
private fun DrawerOption(icon: Int, label: String, detail: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 3.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) Color(0xFFE8F1FF) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 11.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Image(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.size(28.dp),
            contentScale = ContentScale.Fit
        )
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(label, fontSize = 13.sp, color = if (selected) Color(0xFF1258C0) else Color(0xFF203D72))
            Text(detail, fontSize = 10.sp, color = Color(0xFF7084A5), maxLines = 1)
        }
        Text(if (selected) "●" else "›", fontSize = if (selected) 13.sp else 21.sp, color = Color(0xFF7594C5))
    }
}

@Composable
private fun Avatar(photoPath: String, size: Int) {
    val bitmap = remember(photoPath) {
        if (photoPath.isBlank()) null else BitmapFactory.decodeFile(photoPath)?.asImageBitmap()
    }
    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(Color(0xFFDCECFF))
            .border(2.dp, Color(0xFF95C5FF), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (bitmap != null) {
            Image(bitmap, "Foto del perfil", Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        } else {
            Image(painterResource(R.drawable.nav_profile), "Foto de perfil predeterminada", Modifier.size((size * .68f).dp))
        }
    }
}

@Composable
private fun ScreenTitle(heading: String, sub: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(heading, color = Navy, fontSize = 25.sp, lineHeight = 30.sp)
        Text(sub, color = Gray, fontSize = 12.sp)
    }
}

@Composable
private fun FeatureCard(title: String, detail: String, icon: Int? = null, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(19.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (icon != null) Image(painterResource(icon), null, Modifier.size(46.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, color = Navy, fontSize = 16.sp)
                Text(detail, color = Gray, fontSize = 12.sp)
            }
            Text("›", color = Blue, fontSize = 24.sp)
        }
    }
}

@Composable
private fun PrimaryAction(title: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(13.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Blue)
    ) { Text(title, color = Color.White) }
}

@Composable
private fun ProfilePage(
    profile: UserProfile, error: String, onEdit: (UserProfile) -> Unit,
    onPickPhoto: () -> Unit, onStartTest: () -> Unit, onSkip: () -> Unit, onSave: () -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item { ScreenTitle(if (profile.saved) "Editar mi perfil" else "¡Bienvenido a Easy English!", "Tu información se guarda solo en este dispositivo.") }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(20.dp)) {
                Column(Modifier.fillMaxWidth().padding(17.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Avatar(profile.photoPath, 86)
                    Spacer(Modifier.height(8.dp))
                    TextButton(onClick = onPickPhoto) { Text("Cambiar foto PNG") }
                    OutlinedTextField(
                        value = profile.name,
                        onValueChange = { onEdit(profile.copy(name = it.take(50))) },
                        label = { Text("Nombre o apodo") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(Modifier.height(10.dp))
                    Text("Rango de edad (opcional)", color = Navy, modifier = Modifier.fillMaxWidth(), fontSize = 13.sp)
                    ChoiceRows(
                        listOf("Prefiero no indicar", "13–15", "16–17", "18 o más"),
                        profile.ageRange,
                        onPick = { onEdit(profile.copy(ageRange = it)) }
                    )
                    Spacer(Modifier.height(9.dp))
                    Text("¿Has estudiado inglés anteriormente?", color = Navy, modifier = Modifier.fillMaxWidth(), fontSize = 13.sp)
                    ChoiceRows(listOf("Sí", "No", "Un poco"), profile.priorStudy) {
                        onEdit(profile.copy(priorStudy = it))
                    }
                    Spacer(Modifier.height(9.dp))
                    Text("¿Para qué quieres aprender inglés?", color = Navy, modifier = Modifier.fillMaxWidth(), fontSize = 13.sp)
                    ChoiceRows(listOf("Estudios", "Trabajo", "Viajes", "Interés personal"), profile.goal) {
                        onEdit(profile.copy(goal = it))
                    }
                }
            }
        }
        item {
            FeatureCard("Prueba de clasificación", "Evalúa tus conocimientos. Versión inicial de demostración.", R.drawable.nav_exam, onStartTest)
        }
        item {
            OutlinedButton(onClick = onSkip, modifier = Modifier.fillMaxWidth()) {
                Text("Saltar prueba y comenzar desde A0")
            }
        }
        if (error.isNotBlank()) item { Text(error, color = Color(0xFFB33636), fontSize = 12.sp) }
        item { PrimaryAction("Guardar perfil y continuar", onSave) }
        item {
            Text(
                "La experiencia de estudio no determina tu nivel. La prueba inicial todavía no constituye una certificación.",
                color = Gray, fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun ChoiceRows(options: List<String>, selected: String, onPick: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        options.chunked(2).forEach { group ->
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                group.forEach { option ->
                    FilterChip(
                        selected = selected == option,
                        onClick = { onPick(option) },
                        label = { Text(option, fontSize = 11.sp, maxLines = 1) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (group.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

private data class QuizItem(val category: String, val prompt: String, val options: List<String>, val correct: Int, val explanation: String)
private val quizItems = listOf(
    QuizItem("Gramática · A1", "She ___ English every day.", listOf("study", "studies", "studys"), 1, "Con she, study cambia a studies."),
    QuizItem("Gramática · A2", "Yesterday I ___ to school.", listOf("go", "went", "going"), 1, "Yesterday requiere el pasado: went."),
    QuizItem("Lectura · A1", "Tom has two sisters. ¿Cuántas hermanas tiene?", listOf("One", "Two", "Three"), 1, "Two significa dos."),
    QuizItem("Gramática · B1", "I have lived here ___ 2022.", listOf("for", "since", "during"), 1, "Since indica el punto inicial."),
    QuizItem("Lectura · B1", "Although it was raining, Mia went outside. ¿Qué sucedió?", listOf("Se quedó dentro", "Salió a pesar de la lluvia", "Dejó de llover"), 1, "Although introduce un contraste."),
    QuizItem("Uso de inglés · B2", "If I had known, I ___ you.", listOf("would tell", "would have told", "will tell"), 1, "El tercer condicional usa would have + participio.")
)

@Composable
private fun PlacementPage(profile: UserProfile, onBack: () -> Unit, onDone: () -> Unit) {
    var questionIndex by rememberSaveable { mutableStateOf(0) }
    val answers = remember { mutableStateListOf<Int>() }
    var writing by rememberSaveable { mutableStateOf("") }
    var speakingDone by rememberSaveable { mutableStateOf(false) }
    val question = quizItems[questionIndex]
    LazyColumn(
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item { ScreenTitle("Prueba de clasificación", "Demostración inicial, no es un examen MCER validado.") }
        item { LinearProgressIndicator(progress = { (questionIndex + 1f) / quizItems.size }, modifier = Modifier.fillMaxWidth()) }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(13.dp)) {
                    Text("Pregunta " + (questionIndex + 1) + " de " + quizItems.size, color = Blue, fontSize = 12.sp)
                    Text(question.category, color = Gray, fontSize = 13.sp)
                    Text(question.prompt, fontSize = 20.sp, color = Navy)
                    question.options.forEachIndexed { i, choice ->
                        OutlinedButton(
                            onClick = {
                                if (answers.size > questionIndex) answers[questionIndex] = i else answers.add(i)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text((if (answers.getOrNull(questionIndex) == i) "●  " else "○  ") + choice)
                        }
                    }
                    if (answers.getOrNull(questionIndex) != null) {
                        Text(
                            if (answers[questionIndex] == question.correct) "Correcto. " + question.explanation else "Revisa la regla: " + question.explanation,
                            color = if (answers[questionIndex] == question.correct) Color(0xFF14846A) else Gray,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
        if (questionIndex == quizItems.lastIndex) item {
            Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Writing · Muestra sin corrección automática", color = Navy)
                    Text("Write 2–3 sentences about your day.", color = Gray, fontSize = 12.sp)
                    OutlinedTextField(value = writing, onValueChange = { writing = it.take(600) }, modifier = Modifier.fillMaxWidth(), label = { Text("Tu respuesta") })
                    Text("Speaking · Lee en voz alta: I enjoy learning English.", color = Navy, fontSize = 12.sp)
                    OutlinedButton(onClick = { speakingDone = !speakingDone }) {
                        Text(if (speakingDone) "✓ He practicado en voz alta" else "Marcar práctica oral realizada")
                    }
                    Text("Esta demostración no escucha ni puntúa tu voz y no califica Writing. Las tareas completas se validarán antes de asignar niveles.", fontSize = 11.sp, color = Gray)
                }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                OutlinedButton(
                    onClick = { if (questionIndex > 0) questionIndex-- else onBack() },
                    modifier = Modifier.weight(1f)
                ) { Text("Volver") }
                Button(
                    onClick = { if (questionIndex == quizItems.lastIndex) onDone() else questionIndex++ },
                    enabled = answers.getOrNull(questionIndex) != null,
                    modifier = Modifier.weight(1f)
                ) { Text(if (questionIndex == quizItems.lastIndex) "Resultados" else "Siguiente") }
            }
        }
    }
}

@Composable
private fun ReportPage(onSave: () -> Unit, onSkip: () -> Unit) {
    LazyColumn(contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(13.dp)) {
        item { ScreenTitle("Informe orientativo", "La muestra es insuficiente para asignar un nivel oficial.") }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Ruta recomendada por ahora: A0", color = Navy, fontSize = 22.sp)
                    Text("La versión de demostración no puede medir Listening, Speaking y Writing de manera válida. Esas destrezas constarán como no evaluadas.", color = Gray, fontSize = 13.sp)
                    listOf("Listening · no evaluado", "Speaking · no evaluado", "Reading · muestra parcial", "Writing · no evaluado").forEach {
                        Text("• " + it, color = Navy, fontSize = 13.sp)
                    }
                }
            }
        }
        item { PrimaryAction("Guardar perfil y entrar", onSave) }
        item { OutlinedButton(onClick = onSkip, modifier = Modifier.fillMaxWidth()) { Text("Empezar desde cero") } }
    }
}

@Composable
private fun HomePage(profile: UserProfile, onLevels: () -> Unit, onClass: () -> Unit, onDownloads: () -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(13.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item { Text("APRENDE A TU RITMO", fontSize = 11.sp, color = Blue, letterSpacing = 1.sp) }
        item {
            Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Navy)) {
                Box(Modifier.fillMaxWidth().background(Brush.linearGradient(listOf(Color(0xFF103B99), Color(0xFF176ACA), Color(0xFF48ADE2))))) {
                    Image(
                        painterResource(R.drawable.fox_mascot),
                        "Zorrito de Easy English",
                        Modifier.align(Alignment.CenterEnd).size(168.dp)
                    )
                    Column(Modifier.padding(17.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("TU AVENTURA COMIENZA", color = Color(0xFFD0F4FF), fontSize = 11.sp)
                        Text("¡Cada palabra\ncuenta!", color = Color.White, fontSize = 27.sp, lineHeight = 30.sp)
                        Text("Avanza de A0 a C2 con\nexplicaciones y práctica.", color = Color(0xFFE1F0FF), fontSize = 12.sp)
                        Spacer(Modifier.height(13.dp))
                        Button(onClick = onClass, colors = ButtonDefaults.buttonColors(containerColor = Color.White)) {
                            Text("Comenzar a aprender →", color = Blue, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                MetricCard(profile.level, "Nivel seleccionado", Modifier.weight(1f))
                MetricCard("0/156", "Unidades terminadas", Modifier.weight(1f))
                MetricCard("7", "Etapas totales", Modifier.weight(1f))
            }
        }
        item { Text("Tu siguiente lección", fontSize = 19.sp, color = Navy) }
        item { FeatureCard("Verbo to be · A1", "Lección con teoría y juegos", R.drawable.nav_book, onClick = onClass) }
        item { Text("Explora tu aprendizaje", fontSize = 19.sp, color = Navy) }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                QuickTile("Ver niveles", R.drawable.nav_levels, Color(0xFFE8F1FF), Modifier.weight(1f), onLevels)
                QuickTile("Teoría interactiva", R.drawable.nav_book, Color(0xFFF3E8FF), Modifier.weight(1f), onClass)
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                QuickTile("Practicar", R.drawable.nav_exam, Color(0xFFFFF1DD), Modifier.weight(1f), onClass)
                QuickTile("Más contenidos", R.drawable.nav_download, Color(0xFFE0F9F4), Modifier.weight(1f), onDownloads)
            }
        }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFEEF6FF))) {
                Row(Modifier.padding(14.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Image(painterResource(R.drawable.fox_mascot), null, Modifier.size(53.dp))
                    Column {
                        Text("¡Aprende conmigo!", color = Navy, fontSize = 14.sp)
                        Text("Reglas claras y ejercicios paso a paso.", color = Gray, fontSize = 11.sp)
                    }
                }
            }
        }
        item { Text("156 unidades · 8 sesiones intensivas por tema. Material original pendiente de revisión docente.", color = Gray, fontSize = 11.sp) }
    }
}

@Composable
private fun MetricCard(value: String, label: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(value, color = Blue, fontSize = 19.sp)
            Text(label, color = Gray, fontSize = 10.sp, lineHeight = 13.sp)
        }
    }
}

@Composable
private fun QuickTile(label: String, icon: Int, tint: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = modifier, colors = CardDefaults.cardColors(containerColor = tint), shape = RoundedCornerShape(16.dp)) {
        Row(Modifier.padding(11.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(painterResource(icon), null, Modifier.size(30.dp))
            Text(label, color = Navy, fontSize = 11.sp, lineHeight = 15.sp)
        }
    }
}

private fun levelColor(code: String): Color = when (code) {
    "A0" -> Color(0xFF3AA2C9)
    "A1" -> Color(0xFF276BE3)
    "A2" -> Color(0xFF19A0A3)
    "B1" -> Color(0xFF289967)
    "B2" -> Color(0xFFC39234)
    "C1" -> Color(0xFF8B62CB)
    else -> Color(0xFF6653AC)
}
private fun levelTint(code: String): Color = when (code) {
    "A0" -> Color(0xFFE3F9FF)
    "A1" -> Color(0xFFE5EEFF)
    "A2" -> Color(0xFFDFF9F4)
    "B1" -> Color(0xFFE0FAEC)
    "B2" -> Color(0xFFFFF3DD)
    "C1" -> Color(0xFFF3EBFF)
    else -> Color(0xFFECE9FF)
}
private fun levelIcon(code: String): Int = when (code) {
    "A0" -> R.drawable.nav_a0
    "A1" -> R.drawable.nav_a1
    "A2" -> R.drawable.nav_a2
    "B1" -> R.drawable.nav_b1
    "B2" -> R.drawable.nav_b2
    "C1" -> R.drawable.nav_c1
    else -> R.drawable.nav_c2
}

@Composable
private fun LevelsPage(onLevel: (String) -> Unit) {
    LazyColumn(contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(11.dp)) {
        item { ScreenTitle("Niveles y unidades", "A0 a C2 · 156 unidades de inglés americano") }
        item { Text("ELIGE TU ETAPA", color = Blue, fontSize = 11.sp, letterSpacing = 1.sp) }
        items(LevelCatalog.levels) { lvl ->
            Card(onClick = { onLevel(lvl.code) }, colors = CardDefaults.cardColors(containerColor = levelTint(lvl.code)), shape = RoundedCornerShape(19.dp)) {
                Row(Modifier.padding(horizontal = 15.dp, vertical = 15.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Image(painterResource(levelIcon(lvl.code)), null, Modifier.size(48.dp))
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(lvl.code + " · " + lvl.name, color = levelColor(lvl.code), fontSize = 17.sp)
                        Text(lvl.units.size.toString() + " unidades · " + lvl.description, color = Gray, fontSize = 11.sp)
                    }
                    Text("›", fontSize = 24.sp, color = levelColor(lvl.code))
                }
            }
        }
        item { Text("La clasificación por niveles es orientativa. El banco de pruebas completo aún está en desarrollo.", color = Gray, fontSize = 11.sp) }
    }
}

@Composable
private fun UnitsPage(level: String, onBack: () -> Unit, onUnit: (Int) -> Unit) {
    val lvl=LevelCatalog.levels.firstOrNull { it.code==level } ?: LevelCatalog.levels.first()
    var search by rememberSaveable(level) { mutableStateOf("") }
    val filtered=remember(search,level) {
        lvl.units.mapIndexed { index,title -> index to title }
            .filter { (_,title) ->
                search.isBlank() || title.contains(search.trim(),ignoreCase=true)
            }
    }
    LazyColumn(contentPadding=PaddingValues(18.dp),verticalArrangement=Arrangement.spacedBy(10.dp)) {
        item { ScreenTitle(level+" · "+lvl.name,"Biblioteca: "+lvl.units.size+" unidades de inglés americano") }
        item { TextButton(onClick=onBack) { Text("‹ Volver a niveles") } }
        item {
            OutlinedTextField(
                value=search,onValueChange={search=it.take(60)},
                label={Text("Buscar un tema")},
                placeholder={Text("Ejemplo: presente, familia, historia...")},
                singleLine=true,
                modifier=Modifier.fillMaxWidth()
            )
        }
        item {
            Text(filtered.size.toString()+" temas encontrados · material inicial sin conexión",
                color=Gray,fontSize=11.sp)
        }
        if(filtered.isEmpty())item {
            Text("No se encontraron temas con ese texto.",color=Gray,fontSize=13.sp)
        }
        items(filtered) { (index,title) ->
            FeatureCard(
                (index+1).toString().padStart(2,'0')+"  "+title,
                "8 sesiones · 128 ejercicios, teoría y juegos",
                R.drawable.nav_book
            ) { onUnit(index) }
        }
    }
}

@Composable
private fun ProgressPage(profile: UserProfile, onLevels: () -> Unit) {
    val context=LocalContext.current
    val stats=remember { PracticeProgressStore(context).summary() }
    val accuracy=if(stats.attempts==0)0f else stats.correct.toFloat()/stats.attempts
    LazyColumn(contentPadding=PaddingValues(18.dp),verticalArrangement=Arrangement.spacedBy(15.dp)) {
        item { ScreenTitle("Mi progreso","Tu actividad de práctica en este dispositivo") }
        item {
            Card(colors=CardDefaults.cardColors(containerColor=Color.White)) {
                Column(Modifier.padding(18.dp),verticalArrangement=Arrangement.spacedBy(11.dp)) {
                    Text("Respuestas correctas: "+stats.correct+" / "+stats.attempts,
                        color=Navy,fontSize=18.sp)
                    LinearProgressIndicator(progress={accuracy},modifier=Modifier.fillMaxWidth())
                    Text("Aciertos en ejercicios autocorregibles; no es una nota MCER.",
                        fontSize=12.sp,color=Gray)
                }
            }
        }
        item {
            FeatureCard(
                "Repasos sugeridos: "+stats.dueUnits,
                "Recuperación espaciada basada en tus intentos",
                R.drawable.nav_progress,
                onClick=onLevels
            )
        }
        item {
            Card(colors=CardDefaults.cardColors(containerColor=Color(0xFFEAF4FF))) {
                Column(Modifier.padding(15.dp),verticalArrangement=Arrangement.spacedBy(7.dp)) {
                    Text("Áreas que conviene practicar",color=Navy,fontSize=16.sp)
                    Text(
                        if(stats.weakSkills.isEmpty())"Aún no hay suficientes respuestas para sugerir refuerzos."
                        else stats.weakSkills.joinToString(" · "),
                        color=Gray,fontSize=12.sp
                    )
                    Text("La clasificación inicial y las tareas abiertas todavía requieren evaluadores y rúbricas validadas.",
                        color=Gray,fontSize=11.sp)
                }
            }
        }
        item { PrimaryAction("Explorar unidades",onLevels) }
    }
}

@Composable
private fun SettingsPage(
    reduceMotion: Boolean, enableAudio: Boolean, onReduce: (Boolean) -> Unit,
    onAudio: (Boolean) -> Unit, onDownloads: () -> Unit
) {
    LazyColumn(contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(13.dp)) {
        item { ScreenTitle("Preferencias", "Personaliza tu experiencia de aprendizaje") }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(Modifier.padding(17.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Visual y accesibilidad", fontSize = 17.sp, color = Navy)
                    androidx.compose.material3.Switch(checked = reduceMotion, onCheckedChange = onReduce)
                    Text("Reducir animaciones: " + (if (reduceMotion) "Activado" else "Desactivado"), fontSize = 13.sp)
                    HorizontalDivider()
                    androidx.compose.material3.Switch(checked = enableAudio, onCheckedChange = onAudio)
                    Text("Permitir audio: " + (if (enableAudio) "Activado" else "Desactivado"), fontSize = 13.sp)
                    Text("Todas las letras se muestran en negrita.", color = Gray, fontSize = 12.sp)
                }
            }
        }
        item { FeatureCard("Buscar temas y audios", "Consulta y descarga paquetes oficiales de Easy English. Se requiere internet solo para descargar.", R.drawable.nav_download, onDownloads) }
        item { Text("Los contenidos ya instalados permanecen disponibles sin conexión. No se envían nombres, fotografías ni respuestas al descargar.", fontSize = 11.sp, color = Gray) }
    }
}

@Composable
private fun DownloadsPage(store: ContentStore, enableAudio: Boolean) {
    val scope = rememberCoroutineScope()
    var catalog by remember { mutableStateOf<List<DownloadPack>>(emptyList()) }
    var loading by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf("El catálogo no se consulta automáticamente.") }
    var played by remember { mutableStateOf("") }
    val context = LocalContext.current
    LazyColumn(contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { ScreenTitle("Descargar contenidos", "Más temas, ejercicios y audios para usar después sin internet") }
        item { FeatureCard("Contenido instalado", "La estructura A0–C2 y la lección de muestra están integradas en la APK.", R.drawable.nav_book) {} }
        item {
            PrimaryAction(if (loading) "Consultando..." else "Buscar paquetes disponibles") {
                if (!loading) scope.launch {
                    loading = true
                    try {
                        catalog = store.fetchCatalog()
                        status = if (catalog.isEmpty()) "Aún no hay paquetes publicados." else "Se encontraron " + catalog.size + " paquetes."
                    } catch (e: Exception) {
                        status = "No fue posible consultar el catálogo. Comprueba tu conexión."
                    } finally {
                        loading = false
                    }
                }
            }
        }
        item { Text(status, color = Gray, fontSize = 12.sp) }
        items(catalog) { pack ->
            Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(18.dp)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                    Text(pack.title, color = Navy, fontSize = 17.sp)
                    Text(pack.description, color = Gray, fontSize = 12.sp)
                    Text(pack.type.uppercase(Locale.US) + " · " + if (store.hasPack(pack)) "Descargado" else "Disponible", color = Blue, fontSize = 11.sp)
                    if (store.hasPack(pack) && pack.type == "audio") {
                        OutlinedButton(enabled = enableAudio, onClick = {
                            try { store.playAudio(pack) ; played = "Reproduciendo audio descargado." }
                            catch (_: Exception) { played = "No se pudo reproducir este audio." }
                        }) { Text("Reproducir audio") }
                    } else if (store.hasPack(pack) && pack.type == "lesson") {
                        Text(store.describeLesson(pack), color = Navy, fontSize = 12.sp)
                    } else {
                        OutlinedButton(onClick = {
                            scope.launch {
                                loading = true
                                try { store.download(pack); status = "Descarga completada: " + pack.title }
                                catch (_: Exception) { status = "No se pudo verificar o guardar el paquete." }
                                finally { loading = false }
                            }
                        }, enabled = !loading) { Text("Descargar") }
                    }
                }
            }
        }
        if (played.isNotBlank()) item { Text(played, color = Gray, fontSize = 12.sp) }
        item { Text("Descargas opcionales verificadas con SHA-256. La aplicación funciona sin red para el contenido instalado. Este catálogo se controla desde GitHub.", color = Gray, fontSize = 11.sp) }
    }
}
