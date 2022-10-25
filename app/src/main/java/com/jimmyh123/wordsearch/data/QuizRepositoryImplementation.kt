package com.jimmyh123.wordsearch.data

import kotlinx.coroutines.flow.Flow

class QuizRepositoryImplementation(
    private val dao: QuizDao
) : QuizRepository {

    override suspend fun insertQuizData(quiz: Quiz) {
        dao.insertQuizData(quiz)
    }

    override suspend fun deleteQuizData(quiz: Quiz) {
        dao.deleteQuizData(quiz)
    }

    override suspend fun getQuizDataById(id: Int): Quiz? {
        return dao.getQuizDataById(id)
    }

    override fun getQuizData(): Flow<List<Quiz>> {
        return dao.getQuizData()
    }
}