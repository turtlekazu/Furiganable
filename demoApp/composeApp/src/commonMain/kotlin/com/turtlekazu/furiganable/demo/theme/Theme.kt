package com.turtlekazu.furiganable.demo.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors =
    androidx.compose.material3.lightColorScheme(
        surface = Color.White,
        onSurface = Color(0xFF6D0000),
    )

private val DarkColors =
    androidx.compose.material3.darkColorScheme(
        surface = Color.Black,
        onSurface = Color(0xFFFFDADA),
    )

private val LightColorPalette =
    lightColors(
        surface = Color.White,
        background = Color.White,
        onSurface = Color(0xFF20008A),
        onBackground = Color(0xFF20008A),
    )

private val DarkColorPalette =
    darkColors(
        surface = Color.Black,
        background = Color.Black,
        onSurface = Color(0xFFD6C9FF),
        onBackground = Color(0xFFD6C9FF),
    )

@Composable
fun AppThemeM3(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) DarkColors else LightColors

    androidx.compose.material3.MaterialTheme(
        colorScheme = colors,
        content = content,
    )
}

@Composable
fun AppThemeM2(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) DarkColorPalette else LightColorPalette

    MaterialTheme(
        colors = colors,
        content = content,
    )
}
