package com.example.wordsearch.ui

import com.example.wordsearch.data.Quiz

// send events from the UI to the viewModel
sealed class QuizEvent {
    data class OnDeleteQuizClick(val quiz: Quiz): QuizEvent()
//    data class OnDoneChange(
//        val quiz: Quiz,
//        val isDone: Boolean
//        ): QuizEvent()
    object OnUndoDeleteClick: QuizEvent()
    data class OnQuizClick(val quiz: Quiz): QuizEvent()
    object OnAddQuizClick: QuizEvent()
}