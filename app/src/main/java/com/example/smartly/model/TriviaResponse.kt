package com.example.smartly.model

data class TriviaResponse(
    val response_code: Int,
    val results: List<Question>
)
