package com.example.wordsearch.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wordsearch.ui.GameViewModel

@Composable
fun WordSearch(
    modifier: Modifier = Modifier,
    gameViewModel: GameViewModel = viewModel(),
) {
    val gameUiState by gameViewModel.uiState.collectAsState()
//    var text by remember { mutableStateOf("") }

    Column (
        modifier = modifier
        .padding(16.dp)
    ) {

        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text("Find the hidden word!")
        }

        Text("Clue: ${gameUiState.currentClue}")
        Text("Answer: ${gameUiState.currentAnswer}")
        Text(gameUiState.jumbleWithInsertedString)
        Text("Words completed: ${gameUiState.wordsCompleted}" )
        Text("Skips: ${gameUiState.skipsPerformed}")

        JumbledText()

        Spacer(modifier = modifier.padding(8.dp))

        GameStatus()

        Spacer(modifier = modifier.padding(8.dp))

        OutlinedTextField(
            value = gameViewModel.userGuess,
            onValueChange = { gameViewModel.updateUserGuess(it) },
            label = { Text("Enter your answer") },
            modifier = Modifier.fillMaxWidth(),
            isError = gameUiState.isGuessedWordWrong,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { gameViewModel.checkSubmittedGuess()  }
            )
        )

        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ){
            OutlinedButton(onClick = { gameViewModel.skipWord() }) {
                Text("Skip")
            }
            OutlinedButton(onClick = { gameViewModel.checkSubmittedGuess() }) {
                Text("Submit")
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
//        Text("qwekfjkdnfjsowkcopdpskdlakfjkdnfjsowkcopdpskdlapepperonikfjkdnfjsowkcopdpskdlakfjkdnfjsowkcopdpskdlaqwr")
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
