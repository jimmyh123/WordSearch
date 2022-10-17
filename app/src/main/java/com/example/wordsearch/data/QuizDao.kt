package com.example.wordsearch.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface QuizDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizData(quiz: Quiz)

    @Delete
    suspend fun deleteQuizData(quiz: Quiz)

    @Query("SELECT * FROM quiz WHERE id = :id")
    suspend fun getQuizDataById(id: Int): Quiz?

    @Query("SELECT * FROM quiz")
    fun getQuizData(): Flow<List<Quiz>>
}