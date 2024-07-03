package com.example.smartly.apiInterface


import com.example.smartly.model.TriviaResponse
import javax.inject.Inject


class TriviaApiImpl @Inject constructor(private val triviaApi: TriviaApi) {

    suspend fun getTriviaQuestions(
        amount: Int,
        category: Int,
        difficulty: String,
        type: String
    ): TriviaResponse = triviaApi.getTriviaQuestions(amount, category, difficulty, type)
}
