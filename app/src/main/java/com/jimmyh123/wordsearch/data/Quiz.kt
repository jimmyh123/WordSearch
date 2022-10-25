package com.jimmyh123.wordsearch.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Quiz(
    val clue: String,
    val answer: String,
    @PrimaryKey val id: Int? = null
)
