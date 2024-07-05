package com.example.smartly.data.apiInterface


import android.util.Log
import com.example.smartly.domain.model.TriviaResponse
import javax.inject.Inject

class TriviaApiImpl @Inject constructor(private val triviaApi: TriviaApi) {

    suspend fun getTriviaQuestions(
        amount: Int,
        category: Int,
        difficulty: String,
        type: String
    ): TriviaResponse {
        val response = triviaApi.getTriviaQuestions(amount, category, difficulty, type)
        Log.d("TriviaApiImpl", "API Response: $response")
        return response
    }
}

