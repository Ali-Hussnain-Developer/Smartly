package com.example.smartly.domain.model

data class TriviaResponse(
    val response_code: Int,
    val results: List<Question>
)
