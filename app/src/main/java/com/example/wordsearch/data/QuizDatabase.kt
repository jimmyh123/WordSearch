package com.example.wordsearch.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Quiz::class],
    version = 1,
    exportSchema = true
)
abstract class QuizDatabase : RoomDatabase() {

    abstract val dao: QuizDao
}