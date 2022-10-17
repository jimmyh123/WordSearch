package com.example.wordsearch.add_edit_quiz

sealed class AddEditQuizEvent {
    data class OnClueChange(val clue: String): AddEditQuizEvent()
    data class OnAnswerChange(val answer: String): AddEditQuizEvent()
    object OnSaveQuizClick: AddEditQuizEvent()
}
