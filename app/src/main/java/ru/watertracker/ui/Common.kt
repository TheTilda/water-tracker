package ru.watertracker.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import java.text.NumberFormat
import java.util.Locale

val Success = Color(0xFF2E8B57)
val RU: Locale = Locale.forLanguageTag("ru")

@Composable
fun WaterTheme(content: @Composable () -> Unit) {
    // Задаём и нейтральные цвета, иначе Material 3 подставляет свои сиреневые.
    val colors = if (isSystemInDarkTheme()) {
        darkColorScheme(
            primary = Color(0xFF8DC3EA), primaryContainer = Color(0xFF1A4C6D), secondaryContainer = Color(0xFF1A4C6D),
            background = Color(0xFF111417), surface = Color(0xFF111417), surfaceVariant = Color(0xFF3F474F),
            surfaceContainer = Color(0xFF1D2125), surfaceContainerHigh = Color(0xFF272B30),
        )
    } else {
        lightColorScheme(
            primary = Color(0xFF1F6A9A), primaryContainer = Color(0xFFD3E5F2), secondaryContainer = Color(0xFFD3E5F2),
            background = Color(0xFFF7F9FB), surface = Color(0xFFF7F9FB), surfaceVariant = Color(0xFFDDE3EA),
            surfaceContainer = Color(0xFFECF0F4), surfaceContainerHigh = Color(0xFFE6EBF0),
        )
    }
    MaterialTheme(colorScheme = colors, content = content)
}

fun ml(value: Int): String = NumberFormat.getIntegerInstance(RU).format(value) + " мл"

/** Текст ошибки для введённого объёма или null, если значение корректно. */
fun validateMl(text: String, max: Int): String? {
    val value = text.toIntOrNull()
    return when {
        value == null || value <= 0 -> "Введите число больше нуля"
        value > max -> "Не больше ${ml(max)}"
        else -> null
    }
}

@Composable
fun MlField(value: String, onValueChange: (String) -> Unit, label: String, error: String?) {
    OutlinedTextField(
        value = value,
        onValueChange = { onValueChange(it.filter(Char::isDigit).take(5)) },
        label = { Text(label) },
        suffix = { Text("мл") },
        isError = error != null,
        supportingText = error?.let { { Text(it) } },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
    )
}
