package com.jimmyh123.wordsearch.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jimmyh123.wordsearch.add_edit_quiz.AddEditQuizScreen
import com.jimmyh123.wordsearch.ui.QuizScreen
import com.jimmyh123.wordsearch.ui.WordSearch
import com.jimmyh123.wordsearch.util.QuizTabRow

@Composable
fun NavigationComposable() {

        val navController = rememberNavController()
        val currentBackStack by navController.currentBackStackEntryAsState()
        val currentDestination = currentBackStack?.destination

        // search to find current screen by id, use WordSearchObj as a backup screen if one is not found
        val currentScreen = quizTabRowScreens.find { it.route == currentDestination?.route } ?: WordSearchObj
        Scaffold(
            topBar = {
                QuizTabRow(
                    allScreens = quizTabRowScreens,
                    onTabSelected = { newScreen ->
                        navController.navigateSingleTopTo(newScreen.route)
                                    },
                    currentScreen = currentScreen
                )
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = WordSearchObj.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(route = WordSearchObj.route) {
                    WordSearch()
                }

                composable(route = AddWords.route){
                    QuizScreen(
                        onNavigate = {
                            navController.navigate(it.route)
                        },
                        onClickOpenWordSearch = {
                            navController.navigateSingleTopTo(WordSearchObj.route)
                        }
                    )
                }

                composable(
                    route = AddEditQuiz.route + "?quizId={quizId}",
                    arguments = listOf(
                        navArgument(name = "quizId"){
                            type = NavType.IntType
                            defaultValue = -1
                        }
                    )
                ){
                    AddEditQuizScreen(
                        onPopBackStack = {
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
}

fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) {
        popUpTo(
            this@navigateSingleTopTo.graph.findStartDestination().id
        ) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }