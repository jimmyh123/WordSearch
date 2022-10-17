package com.example.wordsearch.add_edit_quiz

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wordsearch.data.Quiz
import com.example.wordsearch.data.QuizRepository
import com.example.wordsearch.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditQuizViewModel @Inject constructor(
    private val repository: QuizRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var quiz by mutableStateOf<Quiz?>(null)
        private set

    var clue by mutableStateOf("")
        private set

    var answer by mutableStateOf("")
        private set

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init{
        val quizId = savedStateHandle.get<Int>("quizId") ?: -1
        if(quizId != -1){
            viewModelScope.launch {
                repository.getQuizDataById(quizId)?.let{ quiz ->
                    clue = quiz.clue
                    answer = quiz.answer
                    this@AddEditQuizViewModel.quiz = quiz
                }
            }
        }
    }

    fun onEvent(event: AddEditQuizEvent){
        when(event){
            is AddEditQuizEvent.OnClueChange -> {
                clue = event.clue
            }
            is AddEditQuizEvent.OnAnswerChange -> {
                answer = event.answer
            }
            is AddEditQuizEvent.OnSaveQuizClick -> {
                viewModelScope.launch{
                    if(clue.isBlank()){
                        sendUiEvent(UiEvent.ShowSnackbar(
                            message = "The clue can't be empty"
                        ))
                        return@launch
                    }
                    if(answer.isBlank()){
                        sendUiEvent(UiEvent.ShowSnackbar(
                            message = "The answer can't be empty"
                        ))
                        return@launch
                    }
                    repository.insertQuizData(
                        Quiz(
                            clue = clue,
                            answer = answer,
                            id = quiz?.id
                        )
                    )
                    sendUiEvent((UiEvent.PopBackStack))
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