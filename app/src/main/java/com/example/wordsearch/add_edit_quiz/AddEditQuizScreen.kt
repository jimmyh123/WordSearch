package com.example.wordsearch.add_edit_quiz

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.wordsearch.util.UiEvent


@Composable
fun AddEditQuizScreen(
    onPopBackStack: () -> Unit,
    viewModel: AddEditQuizViewModel = hiltViewModel()
) {
    val scaffoldState = rememberScaffoldState()
    LaunchedEffect(key1 = true){
        viewModel.uiEvent.collect{ event ->
            when(event){
                is UiEvent.PopBackStack -> onPopBackStack()
                is UiEvent.ShowSnackbar -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = event.action
                    )
                }
                else -> Unit
            }
        }
    }
    Scaffold(
        scaffoldState = scaffoldState,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        floatingActionButton = {
            FloatingActionButton(onClick = {
                viewModel.onEvent(AddEditQuizEvent.OnSaveQuizClick)
            }) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Save"
                )

            }
        }
    ){
        Column(
            modifier = Modifier.fillMaxSize()
        ){
            Text("Add your own clues and answers!")
            Spacer(modifier = Modifier.height(20.dp))

            TextField(
                value = viewModel.clue,
                onValueChange = {
                    viewModel.onEvent(AddEditQuizEvent.OnClueChange(it))
                },
                placeholder = {
                    Text("Clue")
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = viewModel.answer,
                onValueChange = {
                    viewModel.onEvent(AddEditQuizEvent.OnAnswerChange(it))
                },
                placeholder = {
                    Text("Answer")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                maxLines = 5
            )
        }
    }
}