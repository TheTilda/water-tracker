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
    val colors = if (isSystemInDarkTheme()) {
        darkColorScheme(primary = Color(0xFF8DC3EA), primaryContainer = Color(0xFF1A4C6D))
    } else {
        lightColorScheme(primary = Color(0xFF1F6A9A), primaryContainer = Color(0xFFD3E5F2))
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
