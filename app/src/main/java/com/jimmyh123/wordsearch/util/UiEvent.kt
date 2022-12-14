package com.jimmyh123.wordsearch.util

// send events from viewModel to the Ui
sealed class UiEvent {
    object PopBackStack: UiEvent()
    data class Navigate(val route: String): UiEvent()
    data class ShowSnackbar(
        val message: String,
        val action: String? = null
    ): UiEvent()
    data class ShowToast(
        val message: String,
        val action: String? = null
    ): UiEvent()
}