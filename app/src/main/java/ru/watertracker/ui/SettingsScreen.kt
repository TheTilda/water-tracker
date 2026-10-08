package ru.watertracker.ui

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.watertracker.data.Settings
import ru.watertracker.data.WaterRepository
import ru.watertracker.reminders.Notifications
import ru.watertracker.reminders.ReminderScheduler

private val goalPresets = listOf(1500, 2000, 2500)
private val intervals = listOf(30 to "30 мин", 60 to "1 ч", 90 to "1,5 ч", 120 to "2 ч", 180 to "3 ч")

@Composable
fun SettingsScreen(repository: WaterRepository, snackbar: SnackbarHostState) {
    val saved by repository.settings.collectAsState(initial = null)
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    SettingsForm(saved ?: return) { settings ->
        scope.launch {
            repository.saveSettings(settings)
            ReminderScheduler.apply(context, settings)
            snackbar.showSnackbar("Настройки сохранены")
        }
    }
}

@Composable
private fun SettingsForm(saved: Settings, onSave: (Settings) -> Unit) {
    val context = LocalContext.current
    var goal by rememberSaveable { mutableStateOf(saved.dailyGoalMl.toString()) }
    var portion by rememberSaveable { mutableStateOf(saved.portionMl.toString()) }
    var reminders by rememberSaveable { mutableStateOf(saved.remindersEnabled) }
    var interval by rememberSaveable { mutableStateOf(saved.reminderIntervalMin) }
    val permission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { reminders = it }

    val goalError = validateMl(goal, max = 10_000)
    val portionError = validateMl(portion, max = 3_000)
    val draft = if (goalError == null && portionError == null) {
        Settings(dailyGoalMl = goal.toInt(), portionMl = portion.toInt(), remindersEnabled = reminders, reminderIntervalMin = interval)
    } else null

    Column(
        Modifier.verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Дневная норма", style = MaterialTheme.typography.titleMedium)
        MlField(goal, { goal = it }, "Норма", goalError)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            goalPresets.forEach { preset ->
                FilterChip(selected = goal == preset.toString(), onClick = { goal = preset.toString() }, label = { Text(ml(preset)) })
            }
        }

        Text("Порция для кнопки «Стакан»", style = MaterialTheme.typography.titleMedium)
        MlField(portion, { portion = it }, "Порция", portionError)

        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Напоминания", style = MaterialTheme.typography.titleMedium)
                Text(
                    "С 8:00 до 22:00, пока норма не выполнена",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Switch(checked = reminders, onCheckedChange = { on ->
                if (on && Build.VERSION.SDK_INT >= 33 && !Notifications.canPost(context)) {
                    permission.launch(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    reminders = on
                }
            })
        }
        if (reminders) {
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                intervals.forEach { (minutes, label) ->
                    FilterChip(selected = interval == minutes, onClick = { interval = minutes }, label = { Text(label) })
                }
            }
        }

        Button(
            onClick = { draft?.let(onSave) },
            enabled = draft != null && draft != saved,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        ) { Text("Сохранить") }
    }
}
