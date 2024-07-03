package com.example.smartly.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.smartly.model.UserAnswer

@Dao
interface UserAnswerDao {
    @Insert
    suspend fun insert(userAnswer: UserAnswer)

    @Query("SELECT COUNT(*) FROM user_answers WHERE isCorrect = 1")
    suspend fun getCorrectAnswersCount(): Int

    @Query("SELECT COUNT(*) FROM user_answers WHERE isCorrect = 0")
    suspend fun getIncorrectAnswersCount(): Int
}