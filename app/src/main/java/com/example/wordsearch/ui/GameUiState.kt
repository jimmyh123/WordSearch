package com.example.wordsearch.ui

data class GameUiState(
    val currentClue: String? = "",
    val currentAnswer: String? = "",
    val currentJumble: String = "",
    val wordInsertionIndex: Int = 0,
    val wordSelectionIndex: Int = 0,
    val generatedString: String = "",
    val jumbleWithInsertedString: String = "",

    val isGuessedWordWrong: Boolean = false,
    val wordsCompleted: Int = 0,
    val skipsPerformed: Int = 0
    )
