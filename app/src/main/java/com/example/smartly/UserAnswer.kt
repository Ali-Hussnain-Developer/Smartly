package com.example.smartly

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "user_answers")
data class UserAnswer(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val question: String,
    val selectedAnswer: String,
    val correctAnswer: String,
    val isCorrect: Boolean
)