package ru.watertracker.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.watertracker.data.WaterRepository
import ru.watertracker.data.buildHistory
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val dateFormat = DateTimeFormatter.ofPattern("d MMMM, EEEE", RU)

@Composable
fun HistoryScreen(repository: WaterRepository) {
    val entries by repository.allEntries.collectAsState(initial = null)
    val settings by repository.settings.collectAsState(initial = null)
    val goal = settings?.dailyGoalMl ?: return
    val list = entries ?: return
    val today = LocalDate.now()
    val days = remember(list) { buildHistory(list, today) }

    if (days.isEmpty()) {
        Text(
            "История пока пуста. Добавьте первую порцию на экране «Сегодня».",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(24.dp),
        )
        return
    }

    LazyColumn {
        items(days, key = { it.date.toEpochDay() }) { day ->
            val reached = day.totalMl >= goal
            ListItem(
                headlineContent = { Text(if (day.date == today) "Сегодня" else day.date.format(dateFormat)) },
                supportingContent = { Text(if (reached) "Норма выполнена" else "Норма не выполнена") },
                trailingContent = { Text(ml(day.totalMl), style = MaterialTheme.typography.titleMedium) },
                leadingContent = {
                    Icon(
                        Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = if (reached) Success else MaterialTheme.colorScheme.surfaceVariant,
                    )
                },
            )
        }
    }
}
