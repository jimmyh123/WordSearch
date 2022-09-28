package com.example.wordsearch.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun WordSearch(
    modifier: Modifier = Modifier
) {
    //TODO <game ui state goes here>
    var text by remember { mutableStateOf("") }

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

        GameClue()
        JumbledText()

        Spacer(modifier = modifier.padding(8.dp))

        Text("Words completed: 0")
        Text("Skips: 0")

        Spacer(modifier = modifier.padding(8.dp))

        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Enter your answer") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ){
            OutlinedButton(onClick = { /*TODO*/ }) {
                Text("Skip")
            }
            OutlinedButton(onClick = { /*TODO*/ }) {
                Text("Submit")
            }
        }

    }

}



@Composable
fun GameClue(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text("Clue: Popular pizza topping")
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
        Text("qwekfjkdnfjsowkcopdpskdlakfjkdnfjsowkcopdpskdlapepperonikfjkdnfjsowkcopdpskdlakfjkdnfjsowkcopdpskdlaqwr")
    }
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
