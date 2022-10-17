package com.example.wordsearch.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wordsearch.data.Quiz
import com.example.wordsearch.data.QuizRepository
import com.example.wordsearch.ui.navigation.AddEditQuiz
import com.example.wordsearch.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val repository: QuizRepository
): ViewModel() {

    val quizData = repository.getQuizData()

    // create mutable and immutable version so UI can see but not directly change immutable version
    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    // to hold the deleted quiz temporarily and allow re-adding via Snackbar
    private var deletedQuiz: Quiz? = null

    fun onEvent(event: QuizEvent){
        when(event){
            is QuizEvent.OnQuizClick ->{
                sendUiEvent(UiEvent.Navigate(AddEditQuiz.route + "?quizId=${event.quiz.id}"))
            }
            is QuizEvent.OnAddQuizClick ->{
                sendUiEvent(UiEvent.Navigate(AddEditQuiz.route))
            }
            is QuizEvent.OnUndoDeleteClick ->{
                deletedQuiz?.let{ quiz ->
                    viewModelScope.launch {
                        repository.insertQuizData(quiz)
                    }
                }
            }
            is QuizEvent.OnDeleteQuizClick ->{
                viewModelScope.launch{
                    deletedQuiz = event.quiz
                    repository.deleteQuizData(event.quiz)
                    sendUiEvent(
                        UiEvent.ShowSnackbar(
                            message = "Quiz deleted",
                            action = "Undo"
                        )
                    )
                }
            }
        }
    }

    private fun sendUiEvent(event: UiEvent){
        viewModelScope.launch{
            _uiEvent.send(event)
        }
    }
}