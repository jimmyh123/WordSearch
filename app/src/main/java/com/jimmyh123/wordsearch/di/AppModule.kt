package com.jimmyh123.wordsearch.di

import android.app.Application
import androidx.room.Room
import com.jimmyh123.wordsearch.data.QuizDatabase
import com.jimmyh123.wordsearch.data.QuizRepository
import com.jimmyh123.wordsearch.data.QuizRepositoryImplementation
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideQuizDatabase(app: Application): QuizDatabase {
        return Room.databaseBuilder(
            app,
            QuizDatabase::class.java,
            "Quiz"
        )
            .createFromAsset("database/Quiz.db")
            .build()
    }

    @Provides
    @Singleton
    fun provideQuizRepository(db: QuizDatabase): QuizRepository {
        return QuizRepositoryImplementation(db.dao)
    }
}