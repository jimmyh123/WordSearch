package com.example.wordsearch.di

import android.app.Application
import androidx.room.Room
import com.example.wordsearch.data.QuizDatabase
import com.example.wordsearch.data.QuizRepository
import com.example.wordsearch.data.QuizRepositoryImplementation
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
    fun provideQuizDatabase(app: Application): QuizDatabase{
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
    fun provideQuizRepository(db: QuizDatabase): QuizRepository{
        return QuizRepositoryImplementation(db.dao)
    }
}