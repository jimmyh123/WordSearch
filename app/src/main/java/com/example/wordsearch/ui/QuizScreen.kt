package com.example.wordsearch.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.wordsearch.util.UiEvent

@Composable
fun QuizScreen(
    onNavigate: (UiEvent.Navigate) -> Unit,
    onClickOpenWordSearch: () -> Unit = {},
    viewModel: QuizViewModel = hiltViewModel()
) {
    val quizData = viewModel.quizData.collectAsState(initial = emptyList())
    val scaffoldState = rememberScaffoldState()

    LaunchedEffect(key1 = true){
        viewModel.uiEvent.collect { event ->
            when(event){
                is UiEvent.ShowSnackbar ->{
                    val result = scaffoldState.snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = event.action
                    )
                    if(result==SnackbarResult.ActionPerformed){
                        viewModel.onEvent(QuizEvent.OnUndoDeleteClick)
                    }
                }
                is UiEvent.Navigate -> onNavigate(event)
                else -> Unit

            }
        }
    }
    Scaffold(
        scaffoldState = scaffoldState,
        floatingActionButton = {
            FloatingActionButton(onClick = {
                viewModel.onEvent(QuizEvent.OnAddQuizClick)
            }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add"
                )
            }
        }
    ){
        Column(Modifier.fillMaxSize()) {
            Text(
                text = "Click the '+' to add new clues/answers." +
                    "\nClick directly on the clue/answer pair to edit them." +
                    "\nClick the trashcan to delete a clue/answer pair",
                modifier = Modifier.padding(16.dp)
            )
            LazyColumn(
                Modifier.weight(1f)
            ) {
                items(quizData.value) { quiz ->
                    QuizItem(
                        quiz = quiz,
                        onEvent = viewModel::onEvent,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.onEvent(QuizEvent.OnQuizClick(quiz))
                            }
                            .padding(16.dp)
                    )
                }
            }
            TextButton(
                onClick = onClickOpenWordSearch,
                modifier = Modifier
                    .height(44.dp)
                    .fillMaxWidth()
            ) {
                Text("BACK TO WORD SEARCH")
            }
        }
    }
}