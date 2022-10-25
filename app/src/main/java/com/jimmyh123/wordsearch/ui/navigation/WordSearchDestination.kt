package com.jimmyh123.wordsearch.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person

import androidx.compose.ui.graphics.vector.ImageVector

interface WordSearchDestination {
    val icon: ImageVector
    val route: String
}

object WordSearchObj : WordSearchDestination {
    override val icon = Icons.Filled.Person
    override val route = "Word Search"
}


object AddWords : WordSearchDestination {
    override val icon = Icons.Filled.List
    override val route = "Add Words"
}

object AddEditQuiz : WordSearchDestination {
    override val icon = Icons.Filled.List       // unused icon
    override val route = "AddEditQuiz"
}

// Screens to be displayed in the top row
val quizTabRowScreens = listOf(WordSearchObj, AddWords)