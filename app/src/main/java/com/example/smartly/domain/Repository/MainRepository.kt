package com.example.smartly.domain.Repository

import com.example.smartly.data.apiInterface.TriviaApiImpl
import com.example.smartly.domain.model.Question
import com.example.smartly.domain.model.TriviaResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject


class MainRepository @Inject constructor(private val triviaApiImpl: TriviaApiImpl) {

    fun getTriviaQuestions(
        amount: Int,
        category: Int,
        difficulty: String,
        type: String
    ): Flow<TriviaResponse> = flow {
        emit(triviaApiImpl.getTriviaQuestions(amount, category, difficulty, type))
    }.flowOn(Dispatchers.IO)

}








