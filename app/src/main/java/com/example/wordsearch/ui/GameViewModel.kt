package com.example.wordsearch.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wordsearch.data.Quiz
import com.example.wordsearch.data.QuizRepository
import com.example.wordsearch.util.Constants
import com.example.wordsearch.util.Constants.MAX_NO_OF_TURNS
//import com.example.wordsearch.util.Constants.difficulty
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val repository: QuizRepository
): ViewModel() {

    private var currentQuizList by mutableStateOf<List<Quiz?>?>(null)

    private var usedWordIndices: MutableSet<Int> = mutableSetOf()
    var userGuess by mutableStateOf("")
        private set

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var difficulty = Constants.EASY

    init {
        usedWordIndices.clear()

        collectUpdatedWordFlow()
        triggerUiUpdate()
    }


    fun collectUpdatedWordFlow(){
        viewModelScope.launch {
            // Trigger the flow and consume its elements using collect
            repository.getQuizData()
                .catch { exception -> println(exception) }
                .collect { myQuizList ->
                    this@GameViewModel.currentQuizList = myQuizList
                }
        }
    }

    fun triggerUiUpdate() {
        val wordSelectionIndex = findUniqueAnswerIndex()

        if (wordSelectionIndex!=-1) {
            val currentClue = currentQuizList?.get(wordSelectionIndex)?.clue
            val currentAnswer = currentQuizList?.get(wordSelectionIndex)?.answer

            val generatedString = createRandomString(difficulty)
            val wordInsertionIndex = ((0 until difficulty).random())
            val jumbleWithInsertedString = insertAnswerIntoRandomString(
                generatedString,
                currentAnswer,
                wordInsertionIndex
            )

            _uiState.update { currentState ->
                currentState.copy(
                    wordInsertionIndex = wordInsertionIndex,
                    wordSelectionIndex = wordSelectionIndex,
                    currentClue = currentClue,
                    currentAnswer = currentAnswer,
                    jumbleWithInsertedString = jumbleWithInsertedString,
                    skipsPerformed = _uiState.value.skipsPerformed
                )
            }
        }
    }

    fun updateUserGuess(guessedWord: String){
        userGuess = guessedWord
    }

    private fun findUniqueAnswerIndex(): Int{
        val index = (currentQuizList?.indices)?.random() ?: return -1

        return if(usedWordIndices.contains(index)){
            findUniqueAnswerIndex() //TODO check this has more unique answers than questions asked
        } else {
            usedWordIndices.add(index)
            index
        }
    }

    private fun createRandomString(currentDifficulty: Int): String {
        val alphabet: CharRange = ('a'..'z')
        return List(currentDifficulty) { alphabet.random() }.joinToString("")
    }

    private fun insertAnswerIntoRandomString(
        generatedString: String,
        targetString: String?,
        insertionPosition: Int
    ): String {
        val myString = StringBuilder(generatedString)
        return myString.insert(insertionPosition,targetString).toString()
    }

    private fun updateGameState() {
        if (usedWordIndices.size == MAX_NO_OF_TURNS){
            // last round in game
            _uiState.update{ currentState ->
                currentState.copy(
                   /*TODO*/
                )
            }
        } else {
            // start new round
            triggerUiUpdate()
        }
    }

    fun skipWord() {
        // update number of skips
        _uiState.update{ currentState ->
            currentState.copy(skipsPerformed = currentState.skipsPerformed.inc())
        }
        updateGameState()
        updateUserGuess("")
    }

    fun checkSubmittedGuess(){
        if(userGuess.trim().equals(_uiState.value.currentAnswer, ignoreCase = true)){
            _uiState.update{ currentState ->
                currentState.copy(
                    isGuessedWordWrong = false,
                    wordsCompleted = currentState.wordsCompleted.inc()
                )
            }
            updateGameState()
        } else {
            _uiState.update{ currentState ->
                currentState.copy(isGuessedWordWrong = true)
            }
        }
        updateUserGuess("")
    }

    fun toggleGameDifficulty(newDifficulty: String){
        when (newDifficulty) {
            "Easy" -> difficulty = Constants.EASY
            "Medium" -> difficulty = Constants.MEDIUM
            "Hard" -> difficulty = Constants.HARD
        }
    }
}