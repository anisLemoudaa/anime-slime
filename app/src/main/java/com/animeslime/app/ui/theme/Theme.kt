package com.animeslime.app.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Ink = Color(0xFF07090F)
val Surface = Color(0xFF111726)
val Border = Color(0xFF1F2742)
val Mint = Color(0xFF5EEAD4)
val Sky = Color(0xFF38BDF8)
val Muted = Color(0xFF9AA4C2)

private val DarkColors = darkColorScheme(
    primary = Mint, secondary = Sky, background = Ink, surface = Surface,
    onPrimary = Color(0xFF04121A), onBackground = Color.White, onSurface = Color.White,
    outline = Border
)

@Composable
fun AnimeSlimeTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = DarkColors, typography = Typography(), content = content)
}
