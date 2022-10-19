package com.example.wordsearch.ui

import androidx.activity.compose.setContent
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.wordsearch.MainActivity
import com.example.wordsearch.R
import com.example.wordsearch.data.Quiz
import com.example.wordsearch.data.QuizDao
import com.example.wordsearch.data.QuizRepository
import com.example.wordsearch.di.AppModule
import com.example.wordsearch.di.TestAppModule
import com.example.wordsearch.ui.navigation.WordSearchObj
import com.example.wordsearch.ui.theme.WordSearchTheme
import com.example.wordsearch.util.TestTags
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
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
                ){
                   composable(route = WordSearchObj.route){
                       WordSearch()
                   }
                }
            }
        }
    }

    suspend fun addDummyDataToDatabase(){
            repository.insertQuizData(
                Quiz(
                    clue = "clue",
                    answer = "answer",
                    id = 1
                )
            )
    }

    @Test
    fun clickSkipButton_skipsIncreaseByOne(){

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
    fun clickSubmitButtonWithDataInDatabase_totalWordsCompletedDoesNotIncrease(){
        runTest {
            addDummyDataToDatabase()

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
    fun clickSubmitButtonWithEmptyDatabase_totalWordsCompletedIncreases(){

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
}