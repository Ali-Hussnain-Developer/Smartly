package com.example.smartly

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserAnswerDao {
    @Insert
    suspend fun insert(userAnswer: UserAnswer)

    @Query("SELECT COUNT(*) FROM user_answers WHERE isCorrect = 1")
    suspend fun getCorrectAnswersCount(): Int

    @Query("SELECT COUNT(*) FROM user_answers WHERE isCorrect = 0")
    suspend fun getIncorrectAnswersCount(): Int
}