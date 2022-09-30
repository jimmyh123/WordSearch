package com.example.wordsearch.ui

import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.wordsearch.MainActivity
import com.example.wordsearch.data.allCluesAndAnswers
import com.example.wordsearch.util.Constants.MAX_NO_OF_TURNS
import com.example.wordsearch.util.Constants.difficulty
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GameViewModel : ViewModel() {

    private var usedWordIndices: MutableSet<Int> = mutableSetOf()
    var userGuess by mutableStateOf("")
        private set
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    init {
        usedWordIndices.clear()
        updateUi()
    }

    private fun updateUi() {
        val wordSelectionIndex = findUniqueAnswerIndex()
        val currentClue = allCluesAndAnswers[wordSelectionIndex].first
        val currentAnswer = allCluesAndAnswers[wordSelectionIndex].second
        val generatedString = createRandomString(difficulty)
        val wordInsertionIndex = ((0 until difficulty).random())
        val jumbleWithInsertedString = insertAnswerIntoRandomString(generatedString,
            currentAnswer,
            wordInsertionIndex)

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

    fun updateUserGuess(guessedWord: String){
        userGuess = guessedWord
    }

    private fun findUniqueAnswerIndex(): Int{
        val index = (allCluesAndAnswers.indices).random()
        if(usedWordIndices.contains(index)){
            return findUniqueAnswerIndex() //TODO check this has more unique answers than questions asked
        } else {
            usedWordIndices.add(index)
            return index
        }
    }

    private fun createRandomString(currentDifficulty: Int): String {
        val alphabet: CharRange = ('a'..'z')
        val randomString: String = List(currentDifficulty) { alphabet.random() }.joinToString("")
        return randomString
    }

    private fun insertAnswerIntoRandomString(
        generatedString: String,
        targetString: String,
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
            updateUi()
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
        if(userGuess.equals(_uiState.value.currentAnswer, ignoreCase = true)){
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
}