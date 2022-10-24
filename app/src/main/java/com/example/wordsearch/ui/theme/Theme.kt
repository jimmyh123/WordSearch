package com.example.wordsearch.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
        primary = OrangeHarleyDavidson,
        primaryVariant = BeigeHintPensive,
        secondary = GreyChainGang
)

private val LightColorPalette = lightColors(
    primary = BlueProtossPylon,
    primaryVariant = PurplePeriwinkle,
    secondary = YellowRiseNShine,
    background = BlueSeabrook,
    surface = GreyChainGang,
)

@Composable
fun WordSearchTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
