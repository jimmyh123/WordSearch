@file:OptIn(ExperimentalComposeUiApi::class)

package com.example.wordsearch.ui

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.wordsearch.R
import com.example.wordsearch.ui.theme.WordSearchTheme
import com.example.wordsearch.util.TestTags
import com.example.wordsearch.util.UiEvent


@Composable
fun WordSearch(
    modifier: Modifier = Modifier,
    gameViewModel: GameViewModel = hiltViewModel(),
) {
    gameViewModel.collectUpdatedWordFlow()
    gameViewModel.triggerUiUpdate()

    val context = LocalContext.current

    val gameUiState by gameViewModel.uiState.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current

    val selectedDifficultyButton = remember { mutableStateOf("Easy") }

    val scaffoldState = rememberScaffoldState()
    LaunchedEffect(key1 = true){
        gameViewModel.uiEvent.collect{ event ->
            when(event){
                is UiEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                else -> Unit
            }
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
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
                gameUiState = gameUiState,
                checkSubmittedGuess = { gameViewModel.checkSubmittedGuess() },
                updateUserGuess = { gameViewModel.updateUserGuess(it) }
            )
            SkipAndSubmitButtons(
                modifier = modifier,
                currentAnswer = gameUiState.currentAnswer,
                skipWord = { gameViewModel.skipWord() },
                checkSubmittedGuess = { gameViewModel.checkSubmittedGuess() },
                keyboardController = keyboardController,
                gameUiState = gameUiState
            )
            GameDifficultyButtons(
                toggleGameDifficulty = { gameViewModel.toggleGameDifficulty(it) },
                selectedDifficultyButton = selectedDifficultyButton
            )
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
        modifier = Modifier
            .padding(
            top = 8.dp,
            bottom = 8.dp)
            .background(color = MaterialTheme.colors.surface),
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
        Text(
            text = "Words completed: $wordsCompleted",
            modifier = Modifier.testTag(TestTags.WORDS_COMPLETED_COUNTER)
        )
        Text(
            text = "Skips: $skipsPerformed",
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.End)
                .testTag(TestTags.NUMBER_OF_SKIPS_COUNTER)
        )
    }
}

@Composable
fun AnswerBar(
    userGuess: String,
    updateUserGuess: (String) -> Unit,
    checkSubmittedGuess: () -> Unit,
    gameUiState: GameUiState,
) {
    OutlinedTextField(
        value = userGuess,
        singleLine = true,
        onValueChange = updateUserGuess,
        label = {
            if (gameUiState.isGuessedWordWrong) {
                Text("Wrong answer")
            } else {
                Text("Enter your answer")
            }
        },
        modifier = Modifier.fillMaxWidth(),
        isError = gameUiState.isGuessedWordWrong,
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = { checkSubmittedGuess() }
        )
    )

}

@Composable
fun SkipAndSubmitButtons(
    modifier: Modifier,
    currentAnswer: String?,
    keyboardController: SoftwareKeyboardController?,
    skipWord: () -> Unit,
    checkSubmittedGuess: () -> Unit,
    gameUiState: GameUiState
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        val context = LocalContext.current
        OutlinedButton(
            onClick = {
            Toast.makeText(
                context,
                "Answer: $currentAnswer",
                Toast.LENGTH_SHORT
            ).show()
            skipWord()
                      },
            modifier = Modifier.semantics {
                this.contentDescription = R.string.skip_button_text.toString()
            }
        ) {
            Text(stringResource(R.string.skip_button_text))
        }
        OutlinedButton(
            onClick = {
            checkSubmittedGuess()
        },
            modifier = Modifier.semantics {
                this.contentDescription = R.string.submit_button_text.toString()
            }
        ) {
            Text(stringResource(R.string.submit_button_text))
        }
    }
}


@Composable
fun GameDifficultyButtons(
    toggleGameDifficulty: (String) -> Unit,
    selectedDifficultyButton: MutableState<String>,
) {

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Button(
            colors = ButtonDefaults.buttonColors(
                backgroundColor = if(selectedDifficultyButton.value=="Easy")
                    MaterialTheme.colors.primary else MaterialTheme.colors.surface
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
                backgroundColor = if(selectedDifficultyButton.value=="Medium")
                    MaterialTheme.colors.primary else MaterialTheme.colors.surface

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
                backgroundColor = if(selectedDifficultyButton.value=="Hard")
                    MaterialTheme.colors.primary else MaterialTheme.colors.surface
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