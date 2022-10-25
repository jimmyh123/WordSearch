package com.jimmyh123.wordsearch.data


import kotlinx.coroutines.flow.Flow

interface QuizRepository {

    suspend fun insertQuizData(quiz: Quiz)

    suspend fun deleteQuizData(quiz: Quiz)

    suspend fun getQuizDataById(id: Int): Quiz?

    fun getQuizData(): Flow<List<Quiz>>
}
