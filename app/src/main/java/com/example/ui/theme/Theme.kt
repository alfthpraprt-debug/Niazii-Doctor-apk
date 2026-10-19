package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val NiaziDarkColorScheme = darkColorScheme(
    primary = CyanPrimary,
    onPrimary = NavyDeep,
    primaryContainer = NavySurfaceVariant,
    onPrimaryContainer = CyanPrimary,
    secondary = TurquoiseAccent,
    onSecondary = NavyDeep,
    secondaryContainer = NavySurface,
    onSecondaryContainer = TurquoiseAccent,
    tertiary = MedicalGreen,
    onTertiary = NavyDeep,
    background = NavyBackground,
    onBackground = TextPrimaryDark,
    surface = NavySurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = NavySurfaceVariant,
    onSurfaceVariant = TextSecondaryDark,
    outline = NavyCardBorder,
    error = AlertCritical,
    onError = Color.White
)

val NiaziLightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F2FE),
    onPrimaryContainer = Color(0xFF0369A1),
    secondary = LightSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCCFBF1),
    onSecondaryContainer = Color(0xFF115E59),
    tertiary = Color(0xFF16A34A),
    onTertiary = Color.White,
    background = LightBackground,
    onBackground = TextPrimaryLight,
    surface = LightSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = TextSecondaryLight,
    outline = Color(0xFFCBD5E1),
    error = AlertCritical,
    onError = Color.White
)

@Composable
fun NiaziDoctorTheme(
    darkTheme: Boolean = true, // Default to ultra-premium dark midnight aesthetic
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) NiaziDarkColorScheme else NiaziLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    NiaziDoctorTheme(darkTheme = darkTheme, content = content)
}
