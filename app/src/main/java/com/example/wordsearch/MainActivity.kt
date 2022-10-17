package com.example.wordsearch

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.wordsearch.ui.theme.WordSearchTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WordSearchTheme {
                NavigationComposable()
            }
        }
    }
}

fun correctAnswerToast(context: Context){
    Toast.makeText(context, "Correct!", Toast.LENGTH_SHORT).show()
}

