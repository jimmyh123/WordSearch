package com.jimmyh123.wordsearch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.jimmyh123.wordsearch.ui.GameViewModel
import com.jimmyh123.wordsearch.ui.navigation.NavigationComposable
import com.jimmyh123.wordsearch.ui.theme.WordSearchTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    private val gameViewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen().apply{
            setKeepOnScreenCondition{
                gameViewModel.isLoading.value
            }
        }

        setContent {
            WordSearchTheme {
                NavigationComposable()
            }
        }
    }
}


