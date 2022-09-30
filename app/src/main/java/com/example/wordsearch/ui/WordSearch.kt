package com.example.wordsearch.ui

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wordsearch.R
import com.example.wordsearch.ui.GameViewModel
import com.example.wordsearch.ui.theme.WordSearchTheme


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
fun WordSearch(
    modifier: Modifier = Modifier,
    gameViewModel: GameViewModel = viewModel(),
) {
    val gameUiState by gameViewModel.uiState.collectAsState()

    Scaffold(
        topBar = { WordSearchTopAppBar() }
    ) {

        Column(
            modifier = modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Find the hidden word!")
            }

            Text("Clue: ${gameUiState.currentClue}")

            Card(
                modifier = Modifier.padding(
                    top = 8.dp,
                    bottom = 8.dp),
                elevation = 10.dp,
                shape = RectangleShape
            ) {
                Text(gameUiState.jumbleWithInsertedString)
            }

            Row {
                Text("Words completed: ${gameUiState.wordsCompleted}")
                Text(
                    text = "Skips: ${gameUiState.skipsPerformed}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.End),
                )
            }
//            JumbledText()

            Spacer(modifier = modifier.padding(8.dp))

            GameStatus()

            Spacer(modifier = modifier.padding(8.dp))

            OutlinedTextField(
                value = gameViewModel.userGuess,
                singleLine = true,
                onValueChange = { gameViewModel.updateUserGuess(it) },
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
                    onDone = { gameViewModel.checkSubmittedGuess() }
                )
            )

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
                        "Answer: ${gameUiState.currentAnswer}",
                        Toast.LENGTH_SHORT
                    ).show()
                    gameViewModel.skipWord()
                }) {
                    Text("Skip")
                }
                OutlinedButton(onClick = { gameViewModel.checkSubmittedGuess() }) {
                    Text("Submit")
                }
            }

        }
    }

}




@Composable
fun GameClue(
    modifier: Modifier = Modifier,
    clue: String
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
//        Text("Clue: Popular pizza topping")
//        Text("Clue: $clue")
    }
}

@Composable
fun JumbledText(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ){

    }
}

@Composable
fun GameStatus() {
//    Text("Words completed: 0")
//    Text("Skips: 0")
}

@Preview
@Composable
fun FullPreview() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        WordSearch()
    }
}

@Preview
@Composable
fun DarkThemePreview() {
    WordSearchTheme(darkTheme = true) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            WordSearch()
        }
    }
}
