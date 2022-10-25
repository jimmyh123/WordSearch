package com.jimmyh123.wordsearch.ui

import com.jimmyh123.wordsearch.data.Quiz

// send events from the UI to the viewModel
sealed class QuizEvent {
    data class OnDeleteQuizClick(val quiz: Quiz): QuizEvent()
    object OnUndoDeleteClick: QuizEvent()
    data class OnQuizClick(val quiz: Quiz): QuizEvent()
    object OnAddQuizClick: QuizEvent()
}