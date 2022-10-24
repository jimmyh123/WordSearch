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
import com.example.wordsearch.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val repository: QuizRepository
): ViewModel() {

    private var currentQuizList by mutableStateOf<List<Quiz?>?>(null)

    private var usedAnswerList: MutableSet<String> = mutableSetOf()
    var userGuess by mutableStateOf("")
        private set

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var difficulty = Constants.EASY

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    init {
        resetGame()
        viewModelScope.launch {
            _isLoading.value = false
        }
    }

    fun resetGame(){
        _uiState.update { currentState -> currentState.copy(
            isGameOver = false,
            wordsCompleted = 0,
            skipsPerformed = 0
        )}
        usedAnswerList.clear()
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
        var randomIndex: Int = -1
        var currentWord: String? = null
        if (!currentQuizList.isNullOrEmpty()){
            randomIndex = currentQuizList!!.indices.random()
            currentWord = currentQuizList!![randomIndex]?.answer
        }

        return if (usedAnswerList.contains(currentWord) && currentWord!="") {
            findUniqueAnswerIndex()
        } else {
            if (currentWord != null) {
                usedAnswerList.add(currentWord)
            }
            randomIndex
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
        if (usedAnswerList.size >= MAX_NO_OF_TURNS-1 ||
            usedAnswerList.size >= currentQuizList!!.size-1){
            _uiState.update{ currentState ->
                currentState.copy(
                   isGameOver = true
                )
            }
        } else {
            // game not over so start a new round
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
            sendUiEvent(UiEvent.ShowToast("Correct!"))
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

        // recompose jumbled string and update UI without changing clue/answer
        val generatedString = createRandomString(difficulty)
        val wordInsertionIndex = ((0 until difficulty).random())
        val jumbleWithInsertedString = insertAnswerIntoRandomString(
            generatedString,
            uiState.value.currentAnswer,
            wordInsertionIndex
        )
        _uiState.update { currentState ->
            currentState.copy(
                wordInsertionIndex = wordInsertionIndex,
                jumbleWithInsertedString = jumbleWithInsertedString
            )
        }
    }

    private fun sendUiEvent(event: UiEvent){
        viewModelScope.launch{
            _uiEvent.send(event)
        }
    }
}
