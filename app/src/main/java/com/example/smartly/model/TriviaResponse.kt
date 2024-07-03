package com.example.smartly.model

data class Question(
    val category: String,
    val type: String,
    val difficulty: String,
    val question: String,
    val correct_answer: String,
    val incorrect_answers: List<String>
)

data class TriviaResponse(
    val response_code: Int,
    val results: List<Question>
)