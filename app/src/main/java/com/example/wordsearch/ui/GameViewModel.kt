package com.example.wordsearch.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

import com.example.wordsearch.data.allCluesAndAnswers

import com.example.wordsearch.util.Constants.MAX_NO_OF_TURNS
import com.example.wordsearch.util.Constants.difficulty
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GameViewModel : ViewModel() {

    private lateinit var currentWord: String
    private var usedWords: MutableSet<Int> = mutableSetOf()

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    var userGuess by mutableStateOf("")
        private set

    init {
//        resetGame()
        usedWords.clear()
        updateUi()

    }

    private fun updateUi() {
        val wordSelectionIndex = ((allCluesAndAnswers.indices).random())
        val currentClue = allCluesAndAnswers[wordSelectionIndex].first
        val currentAnswer = allCluesAndAnswers[wordSelectionIndex].second
        val generatedString = createRandomString(difficulty)
        val wordInsertionIndex = ((0 until difficulty).random())
        val jumbleWithInsertedString = insertAnswerIntoRandomString(generatedString,
            currentAnswer,
            wordInsertionIndex)

//        val currentJumbledWord = selectNewRandomClueAndAnswer() //TODO decide whether to use

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

//    private fun selectNewRandomClueAndAnswer(): Answer {
//        val clueAndAnswerSet = clueAndAnswer.random()
//        //TODO check if this answer has appeared before
//
//        if(usedWords.contains(clueAndAnswerSet.answer)){
//            return selectNewRandomClueAndAnswer() //TODO check this has more unique answers than questions asked
//        } else {
//            usedWords.add(clueAndAnswerSet.answer)
//            return clueAndAnswerSet
//        }
//    }

    private fun createRandomString(currentDifficulty: Int): String {
        val ALPHABET: List<Char> = ('a'..'z') + ('A'..'Z')
        val randomString: String = List(currentDifficulty) { ALPHABET.random() }.joinToString("")
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
        if (usedWords.size == MAX_NO_OF_TURNS){
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
        if(userGuess.equals(currentWord, ignoreCase = true)){
            // correct answer received, update score
            _uiState.update{ currentState ->
                currentState.copy(wordsCompleted = currentState.wordsCompleted.inc())
            }
            updateGameState()
        } else {
            // incorrect answer received
            _uiState.update{ currentState ->
                currentState.copy(isGuessedWordWrong = true)
            }
        }
    }


}