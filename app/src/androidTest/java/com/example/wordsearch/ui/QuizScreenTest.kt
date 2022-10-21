package com.example.wordsearch.ui

import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.wordsearch.MainActivity
import com.example.wordsearch.R
import com.example.wordsearch.data.Quiz
import com.example.wordsearch.data.QuizRepository
import com.example.wordsearch.di.AppModule
import com.example.wordsearch.ui.navigation.WordSearchObj
import com.example.wordsearch.ui.theme.WordSearchTheme
import com.example.wordsearch.util.Constants
import com.example.wordsearch.util.TestTags
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
@UninstallModules(AppModule::class)
class QuizScreenTest {


    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var repository: QuizRepository

    @Before
    fun setUp() {
        hiltRule.inject()
        composeRule.activity.setContent {
            WordSearchTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = WordSearchObj.route
                ) {
                    composable(route = WordSearchObj.route) {
                        WordSearch()
                    }
                }
            }
        }
    }

    private suspend fun addDummyDataToDatabase(answerString: String, numberOfAnswers: Int) {
        for (i in 1..numberOfAnswers)
            repository.insertQuizData(
                Quiz(
                    clue = "clue$i",
                    answer = "$answerString$i",
                    id = i
                )
            )
    }

    @Test
    fun clickSkipButton_skipsIncreaseByOne() {

        composeRule
            .onNodeWithTag(TestTags.NUMBER_OF_SKIPS_COUNTER)
            .assertTextEquals("Skips: 0")

        composeRule
            .onNodeWithContentDescription(R.string.skip_button_text.toString())
            .performClick()

        composeRule
            .onNodeWithTag(TestTags.NUMBER_OF_SKIPS_COUNTER)
            .assertTextEquals("Skips: 1")
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun clickSubmitButtonWithDataInDatabase_totalWordsCompletedDoesNotIncrease() {
        runTest {
            addDummyDataToDatabase("answer", 1)
            composeRule
                .onNodeWithTag(TestTags.WORDS_COMPLETED_COUNTER)
                .assertTextEquals("Words completed: 0")

            composeRule
                .onNodeWithContentDescription(R.string.submit_button_text.toString())
                .performClick()

            composeRule
                .onNodeWithTag(TestTags.WORDS_COMPLETED_COUNTER)
                .assertTextEquals("Words completed: 0")
        }
    }

    @Test
    fun clickSubmitButtonWithEmptyDatabase_totalWordsCompletedIncreases() {

        composeRule
            .onNodeWithTag(TestTags.WORDS_COMPLETED_COUNTER)
            .assertTextEquals("Words completed: 0")

        composeRule
            .onNodeWithContentDescription(R.string.submit_button_text.toString())
            .performClick()

        composeRule
            .onNodeWithTag(TestTags.WORDS_COMPLETED_COUNTER)
            .assertTextEquals("Words completed: 1")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun addCluesAndAnswersToDataBase_AnswerVisibleInJumbledText() {
        runTest {
            addDummyDataToDatabase("answer", 5)
            composeRule
                .onNodeWithContentDescription(R.string.jumbled_text.toString())
                .assertTextContains("answer", substring = true)
        }
    }
}