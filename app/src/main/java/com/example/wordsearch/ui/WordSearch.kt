@file:OptIn(ExperimentalComposeUiApi::class)

package com.example.wordsearch.ui

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.wordsearch.R
import com.example.wordsearch.correctAnswerToast
import com.example.wordsearch.ui.theme.WordSearchTheme


@Composable
fun WordSearch(
    modifier: Modifier = Modifier,
    gameViewModel: GameViewModel = hiltViewModel(),
) {
    gameViewModel.collectUpdatedWordFlow()
    gameViewModel.triggerUiUpdate()

    val gameUiState by gameViewModel.uiState.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        topBar = { WordSearchTopAppBar() }
    ) {

        Column(
            modifier = modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            TitleBarSubtitle(stringResource(R.string.title_bar_subtitle))
            ClueText(gameUiState.currentClue)
            WordJumble(gameUiState.jumbleWithInsertedString)
            WordsCompletedSkipsPerformed(
                wordsCompleted = gameUiState.wordsCompleted,
                skipsPerformed = gameUiState.skipsPerformed
            )

            Spacer(modifier = modifier.padding(8.dp))

            AnswerBar(
                userGuess = gameViewModel.userGuess,
                isGuessedWordWrong = gameUiState.isGuessedWordWrong,
                checkSubmittedGuess = { gameViewModel.checkSubmittedGuess() },
                updateUserGuess = { gameViewModel.updateUserGuess(it) }
            )
            SkipAndSubmitButtons(
                modifier = modifier,
                currentAnswer = gameUiState.currentAnswer,
                skipWord = { gameViewModel.skipWord() },
                isGuessedWordWrong = gameUiState.isGuessedWordWrong,
                checkSubmittedGuess = { gameViewModel.checkSubmittedGuess() },
                keyboardController = keyboardController
            )
            GameDifficultyButtons(toggleGameDifficulty = { gameViewModel.toggleGameDifficulty("") })
        }
    }
}

@Composable
fun TitleBarSubtitle(subtitleText: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(subtitleText)
    }
}

@Composable
fun WordSearchTopAppBar() {
    Row(
        modifier = Modifier
            .background(color = MaterialTheme.colors.primary)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ){
        Text(
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.h1,
            modifier = Modifier
        )
    }
}

@Composable
fun ClueText(currentClue: String?) {
    Text("Clue: $currentClue")
}

@Composable
fun WordJumble(jumbleWithInsertedString: String?) {
    Card(
        modifier = Modifier.padding(
            top = 8.dp,
            bottom = 8.dp),
        elevation = 10.dp,
        shape = RectangleShape
    ) {
        Text(jumbleWithInsertedString.toString())
    }
}

@Composable
fun WordsCompletedSkipsPerformed(
    wordsCompleted: Int,
    skipsPerformed: Int
) {
    Row {
        Text("Words completed: $wordsCompleted")
        Text(
            text = "Skips: $skipsPerformed",
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.End),
        )
    }
}

@Composable
fun AnswerBar(
    userGuess: String,
    updateUserGuess: (String) -> Unit,
    isGuessedWordWrong: Boolean,
    checkSubmittedGuess: (KeyboardActionScope.() -> Unit)?,
) {
    OutlinedTextField(
        value = userGuess,
        singleLine = true,
        onValueChange = updateUserGuess,
        label = {
            if (isGuessedWordWrong) {
                Text("Wrong answer")
            } else {
                Text("Enter your answer")
            }
        },
        modifier = Modifier.fillMaxWidth(),
        isError = isGuessedWordWrong,
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = checkSubmittedGuess
        )
    )

}

@Composable
fun SkipAndSubmitButtons(
    modifier: Modifier,
    currentAnswer: String?,
    isGuessedWordWrong: Boolean,
    keyboardController: SoftwareKeyboardController?,
    skipWord: () -> Unit,
    checkSubmittedGuess: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        val context = LocalContext.current
        OutlinedButton(onClick = {
            Toast.makeText(
                context,
                "Answer: $currentAnswer",
                Toast.LENGTH_SHORT
            ).show()
            skipWord
        }) {
            Text("Skip")
        }
        OutlinedButton(onClick = {
            checkSubmittedGuess
            if (!isGuessedWordWrong) {
                correctAnswerToast(context)
                keyboardController?.hide()
            }
        }) {
            Text("Submit")
        }
    }
}


@Composable
fun GameDifficultyButtons(
    toggleGameDifficulty: (String) -> Unit
) {
    val selectedDifficultyButton = remember { mutableStateOf("Easy") }
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Button(
            colors = ButtonDefaults.buttonColors(
                backgroundColor = if(selectedDifficultyButton.value=="Easy") Color.Blue else Color.Gray
            ),
            onClick = {
                selectedDifficultyButton.value = "Easy"
                toggleGameDifficulty(selectedDifficultyButton.value)
            }
        ){
            Text(
                text = "Easy"
            )
        }
        Button(
            colors = ButtonDefaults.buttonColors(
                backgroundColor = if(selectedDifficultyButton.value=="Medium") Color.Blue else Color.Gray
            ),
            onClick = {
                selectedDifficultyButton.value = "Medium"
                toggleGameDifficulty(selectedDifficultyButton.value)
            }
        ){
            Text(
                text = "Medium"
            )
        }
        Button(
            colors = ButtonDefaults.buttonColors(
                backgroundColor = if(selectedDifficultyButton.value=="Hard") Color.Blue else Color.Gray
            ),
            onClick = {
                selectedDifficultyButton.value = "Hard"
                toggleGameDifficulty(selectedDifficultyButton.value)
            }){
            Text(
                text = "Hard"
            )
        }
    }
}

@DayAndNightMode
@Composable
fun WordSearchTopAppBarPreview() {
    WordSearchTheme {
        Surface(color = MaterialTheme.colors.background) {
            WordSearchTopAppBar()
        }
    }
}
@DayAndNightMode
@Composable
fun TitleBarSubtitlePreview() {
    WordSearchTheme {
        Surface(color = MaterialTheme.colors.background) {
            TitleBarSubtitle(stringResource(R.string.title_bar_subtitle))
        }
    }
}

@DayAndNightMode
@Composable
fun ClueTextPreview() {
    WordSearchTheme {
        Surface(color = MaterialTheme.colors.background) {
            ClueText("Test Clue")
        }
    }
}

@DayAndNightMode
@Composable
fun WordJumblePreview() {
    WordSearchTheme {
        Surface(color = MaterialTheme.colors.background) {
            WordJumble("skdjfbksjbflksjbdfsbdkjsbincomprehensibleasldabskjdbksjbaksbdka")
        }
    }

}

@DayAndNightMode
@Composable
fun WordsCompletedSkipsPerformedPreview() {
    WordSearchTheme {
        Surface (color = MaterialTheme.colors.background) {
            WordsCompletedSkipsPerformed(wordsCompleted = 3, skipsPerformed = 15)
        }
    }
}


@Preview(
    name = "dark theme",
    group = "themes",
    uiMode = UI_MODE_NIGHT_YES
)
@Preview(
    name = "light theme",
    group = "themes",
    uiMode = UI_MODE_NIGHT_NO
)
annotation class DayAndNightMode