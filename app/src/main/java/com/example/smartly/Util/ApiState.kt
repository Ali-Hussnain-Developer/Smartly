package com.example.smartly.Util

import com.example.smartly.model.Question
import com.example.smartly.model.TriviaResponse
sealed class ApiState {
    object Loading : ApiState()
    class Failure(val msg: Throwable) : ApiState()
    class Success(val data: List<Question>) : ApiState()
    object Empty : ApiState()
}