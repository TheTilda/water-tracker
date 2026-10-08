package ru.watertracker.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import ru.watertracker.WaterApp

enum class Tab(val title: String, val icon: ImageVector) {
    Today("Сегодня", Icons.Filled.Home),
    History("История", Icons.AutoMirrored.Filled.List),
    Settings("Настройки", Icons.Filled.Settings),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaterTrackerApp() {
    val repository = (LocalContext.current.applicationContext as WaterApp).repository
    val snackbar = remember { SnackbarHostState() }
    var tab by rememberSaveable { mutableStateOf(Tab.Today) }

    BackHandler(enabled = tab != Tab.Today) { tab = Tab.Today }

    WaterTheme {
        Scaffold(
            topBar = { TopAppBar(title = { Text(tab.title) }) },
            snackbarHost = { SnackbarHost(snackbar) },
            bottomBar = {
                NavigationBar {
                    Tab.entries.forEach {
                        NavigationBarItem(
                            selected = tab == it,
                            onClick = { tab = it },
                            icon = { Icon(it.icon, contentDescription = null) },
                            label = { Text(it.title) },
                        )
                    }
                }
            },
        ) { padding ->
            // Экран настроек при уходе удаляется из композиции, поэтому несохранённые изменения сбрасываются.
            Box(Modifier.padding(padding)) {
                when (tab) {
                    Tab.Today -> TodayScreen(repository, snackbar)
                    Tab.History -> HistoryScreen(repository)
                    Tab.Settings -> SettingsScreen(repository, snackbar)
                }
            }
        }
    }
}
