package com.easyenglish.app.ui.theme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

private val original = Typography()
private val bold = Typography(
    displayLarge=original.displayLarge.copy(fontWeight=FontWeight.Bold),
    displayMedium=original.displayMedium.copy(fontWeight=FontWeight.Bold),
    displaySmall=original.displaySmall.copy(fontWeight=FontWeight.Bold),
    headlineLarge=original.headlineLarge.copy(fontWeight=FontWeight.Bold),
    headlineMedium=original.headlineMedium.copy(fontWeight=FontWeight.Bold),
    headlineSmall=original.headlineSmall.copy(fontWeight=FontWeight.Bold),
    titleLarge=original.titleLarge.copy(fontWeight=FontWeight.Bold),
    titleMedium=original.titleMedium.copy(fontWeight=FontWeight.Bold),
    titleSmall=original.titleSmall.copy(fontWeight=FontWeight.Bold),
    bodyLarge=original.bodyLarge.copy(fontWeight=FontWeight.Bold),
    bodyMedium=original.bodyMedium.copy(fontWeight=FontWeight.Bold),
    bodySmall=original.bodySmall.copy(fontWeight=FontWeight.Bold),
    labelLarge=original.labelLarge.copy(fontWeight=FontWeight.Bold),
    labelMedium=original.labelMedium.copy(fontWeight=FontWeight.Bold),
    labelSmall=original.labelSmall.copy(fontWeight=FontWeight.Bold)
)
@Composable
fun EasyEnglishTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme=lightColorScheme(
            primary=Color(0xFF266DDD),
            secondary=Color(0xFF4370AA),
            background=Color(0xFFF7FAFF),
            surface=Color.White,
            onSurface=Color(0xFF143878)
        ),
        typography=bold,
        content=content
    )
}