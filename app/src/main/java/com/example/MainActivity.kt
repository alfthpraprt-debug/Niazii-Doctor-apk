package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.ui.screens.AiDiagnosisScreen
import com.example.ui.screens.AiRepairAssistantScreen
import com.example.ui.screens.AppStorageScreen
import com.example.ui.screens.AudioTestScreen
import com.example.ui.screens.BatteryScreen
import com.example.ui.screens.CacheCleanerScreen
import com.example.ui.screens.ChargingScreen
import com.example.ui.screens.DisplayTouchScreen
import com.example.ui.screens.HelpScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MonitoringSettingsScreen
import com.example.ui.screens.NetworkScreen
import com.example.ui.screens.PermissionsScreen
import com.example.ui.screens.ReportScreen
import com.example.ui.screens.ScanScreen
import com.example.ui.screens.SensorsScreen
import com.example.ui.screens.SetupWizardScreen
import com.example.ui.screens.SoftwareDiagnosticsScreen
import com.example.ui.screens.VoiceSettingsScreen
import com.example.ui.theme.NiaziDoctorTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val isDarkTheme by viewModel.preferencesManager.darkTheme.collectAsState()
            val firstRunCompleted by viewModel.preferencesManager.firstRunCompleted.collectAsState()

            NiaziDoctorTheme(darkTheme = isDarkTheme) {
                NiaziDoctorAppContent(
                    viewModel = viewModel,
                    firstRunCompleted = firstRunCompleted
                )
            }
        }
    }
}

@Composable
fun NiaziDoctorAppContent(
    viewModel: MainViewModel,
    firstRunCompleted: Boolean
) {
    val navStack = remember {
        mutableStateListOf(if (firstRunCompleted) "home" else "wizard")
    }

    val currentScreen = navStack.lastOrNull() ?: "home"

    // BackHandler: pop screen from stack or exit if at root
    BackHandler(enabled = navStack.size > 1) {
        if (navStack.size > 1) {
            navStack.removeAt(navStack.size - 1)
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        val screenModifier = Modifier.padding(innerPadding)

        when (currentScreen) {
            "wizard" -> {
                SetupWizardScreen(
                    onFinishSetup = {
                        viewModel.setFirstRunCompleted()
                        navStack.clear()
                        navStack.add("home")
                        viewModel.startFullCheckup()
                    },
                    modifier = screenModifier
                )
            }

            "home" -> {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigate = { route -> navStack.add(route) },
                    modifier = screenModifier
                )
            }

            "scan" -> {
                ScanScreen(
                    viewModel = viewModel,
                    onBack = { if (navStack.size > 1) navStack.removeAt(navStack.size - 1) },
                    onViewDiagnosis = {
                        navStack.add("diagnosis")
                    },
                    modifier = screenModifier
                )
            }

            "battery" -> {
                BatteryScreen(
                    viewModel = viewModel,
                    onBack = { if (navStack.size > 1) navStack.removeAt(navStack.size - 1) },
                    modifier = screenModifier
                )
            }

            "charging" -> {
                ChargingScreen(
                    viewModel = viewModel,
                    onBack = { if (navStack.size > 1) navStack.removeAt(navStack.size - 1) },
                    modifier = screenModifier
                )
            }

            "cache_cleaner" -> {
                CacheCleanerScreen(
                    viewModel = viewModel,
                    onBack = { if (navStack.size > 1) navStack.removeAt(navStack.size - 1) },
                    onNavigateToApps = { navStack.add("app_storage") },
                    modifier = screenModifier
                )
            }

            "app_storage" -> {
                AppStorageScreen(
                    viewModel = viewModel,
                    onBack = { if (navStack.size > 1) navStack.removeAt(navStack.size - 1) },
                    modifier = screenModifier
                )
            }

            "software" -> {
                SoftwareDiagnosticsScreen(
                    viewModel = viewModel,
                    onBack = { if (navStack.size > 1) navStack.removeAt(navStack.size - 1) },
                    modifier = screenModifier
                )
            }

            "repair_assistant" -> {
                AiRepairAssistantScreen(
                    viewModel = viewModel,
                    onBack = { if (navStack.size > 1) navStack.removeAt(navStack.size - 1) },
                    onNavigate = { route -> navStack.add(route) },
                    modifier = screenModifier
                )
            }

            "display_touch" -> {
                DisplayTouchScreen(
                    viewModel = viewModel,
                    onBack = { if (navStack.size > 1) navStack.removeAt(navStack.size - 1) },
                    modifier = screenModifier
                )
            }

            "audio_test" -> {
                AudioTestScreen(
                    viewModel = viewModel,
                    onBack = { if (navStack.size > 1) navStack.removeAt(navStack.size - 1) },
                    modifier = screenModifier
                )
            }

            "network" -> {
                NetworkScreen(
                    viewModel = viewModel,
                    onBack = { if (navStack.size > 1) navStack.removeAt(navStack.size - 1) },
                    modifier = screenModifier
                )
            }

            "sensors" -> {
                SensorsScreen(
                    viewModel = viewModel,
                    onBack = { if (navStack.size > 1) navStack.removeAt(navStack.size - 1) },
                    modifier = screenModifier
                )
            }

            "diagnosis" -> {
                AiDiagnosisScreen(
                    viewModel = viewModel,
                    onBack = { if (navStack.size > 1) navStack.removeAt(navStack.size - 1) },
                    modifier = screenModifier
                )
            }

            "voice_settings" -> {
                VoiceSettingsScreen(
                    viewModel = viewModel,
                    onBack = { if (navStack.size > 1) navStack.removeAt(navStack.size - 1) },
                    modifier = screenModifier
                )
            }

            "monitoring_settings" -> {
                MonitoringSettingsScreen(
                    viewModel = viewModel,
                    onBack = { if (navStack.size > 1) navStack.removeAt(navStack.size - 1) },
                    modifier = screenModifier
                )
            }

            "history" -> {
                HistoryScreen(
                    viewModel = viewModel,
                    onBack = { if (navStack.size > 1) navStack.removeAt(navStack.size - 1) },
                    modifier = screenModifier
                )
            }

            "report" -> {
                ReportScreen(
                    viewModel = viewModel,
                    onBack = { if (navStack.size > 1) navStack.removeAt(navStack.size - 1) },
                    modifier = screenModifier
                )
            }

            "permissions" -> {
                PermissionsScreen(
                    viewModel = viewModel,
                    onBack = { if (navStack.size > 1) navStack.removeAt(navStack.size - 1) },
                    modifier = screenModifier
                )
            }

            "help" -> {
                HelpScreen(
                    onBack = { if (navStack.size > 1) navStack.removeAt(navStack.size - 1) },
                    modifier = screenModifier
                )
            }

            else -> {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigate = { route -> navStack.add(route) },
                    modifier = screenModifier
                )
            }
        }
    }
}
