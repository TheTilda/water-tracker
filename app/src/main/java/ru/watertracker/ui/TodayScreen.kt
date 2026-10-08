package ru.watertracker.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.watertracker.data.WaterRepository
import ru.watertracker.data.localDateTime
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private const val BOTTLE_ML = 500
private val timeFormat = DateTimeFormatter.ofPattern("HH:mm")

@Composable
fun TodayScreen(repository: WaterRepository, snackbar: SnackbarHostState) {
    val today = LocalDate.now()
    val settings by repository.settings.collectAsState(initial = null)
    val entries by remember(today) { repository.entriesFor(today) }.collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    var showDialog by rememberSaveable { mutableStateOf(false) }

    val goal = settings?.dailyGoalMl ?: return
    val portion = settings?.portionMl ?: return
    val total = entries.sumOf { it.amountMl }
    val reached = total >= goal

    fun add(amount: Int) = scope.launch {
        repository.add(amount)
        if (total < goal && total + amount >= goal) snackbar.showSnackbar("Дневная норма выполнена!")
    }

    LazyColumn(Modifier.fillMaxWidth(), contentPadding = PaddingValues(16.dp)) {
        item {
            val progress by animateFloatAsState((total.toFloat() / goal).coerceAtMost(1f), label = "progress")
            Box(Modifier.fillMaxWidth().padding(vertical = 16.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.size(220.dp),
                    color = if (reached) Success else MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    strokeWidth = 14.dp,
                    gapSize = 0.dp,
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(ml(total), style = MaterialTheme.typography.headlineMedium)
                    Text("из ${ml(goal)}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    if (reached) Text("Норма выполнена", color = Success)
                }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { add(portion) }, Modifier.weight(1f)) {
                    Text("+ Стакан\n${ml(portion)}", textAlign = TextAlign.Center)
                }
                Button(onClick = { add(BOTTLE_ML) }, Modifier.weight(1f)) {
                    Text("+ Бутылка\n${ml(BOTTLE_ML)}", textAlign = TextAlign.Center)
                }
                OutlinedButton(onClick = { showDialog = true }, Modifier.weight(1f)) {
                    Text("Другой\nобъём", textAlign = TextAlign.Center)
                }
            }
        }
        item {
            Row(Modifier.fillMaxWidth().padding(top = 24.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("Порции за сегодня", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                TextButton(
                    onClick = { scope.launch { repository.undoLast(today) } },
                    enabled = entries.isNotEmpty(),
                ) { Text("Отменить последнюю") }
            }
        }
        if (entries.isEmpty()) {
            item {
                Text(
                    "Сегодня ещё ничего не добавлено",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 16.dp),
                )
            }
        }
        items(entries, key = { it.id }) { entry ->
            ListItem(
                headlineContent = { Text(ml(entry.amountMl)) },
                trailingContent = { Text(entry.localDateTime().format(timeFormat)) },
            )
        }
    }

    if (showDialog) {
        CustomAmountDialog(onDismiss = { showDialog = false }, onAdd = { showDialog = false; add(it) })
    }
}

@Composable
private fun CustomAmountDialog(onDismiss: () -> Unit, onAdd: (Int) -> Unit) {
    var text by rememberSaveable { mutableStateOf("") }
    val error = if (text.isEmpty()) null else validateMl(text, max = 3000)
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Другой объём") },
        text = { MlField(text, { text = it }, "Объём", error) },
        confirmButton = {
            TextButton(onClick = { onAdd(text.toInt()) }, enabled = text.isNotEmpty() && error == null) { Text("Добавить") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } },
    )
}
