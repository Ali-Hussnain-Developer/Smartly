package com.example.smartly.domain.repository

import com.example.smartly.data.apiInterface.TriviaApiImpl
import com.example.smartly.data.dao.NotesDao
import com.example.smartly.domain.model.NotesModelClass
import com.example.smartly.domain.model.TriviaResponse
import com.example.smartly.domain.model.UserAnswer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Repository @Inject constructor(
    private val triviaApiImpl: TriviaApiImpl,
    private val notesDao: NotesDao
) {

    // API operations
    fun getTriviaQuestions(
        amount: Int,
        category: Int,
        difficulty: String,
        type: String
    ): Flow<TriviaResponse> = flow {
        emit(triviaApiImpl.getTriviaQuestions(amount, category, difficulty, type))
    }.flowOn(Dispatchers.IO)

    // Room DB operations
    val allNotes: Flow<List<NotesModelClass>> = notesDao.getAllNote()
    val allAnswers: Flow<List<UserAnswer>> = notesDao.getAllResult()

    suspend fun insert(note: NotesModelClass) {
        notesDao.insertNote(note)
    }

    suspend fun deleteAllUserAnswers() {
        notesDao.deleteAllUserAnswers()
    }

    suspend fun insertAnswer(userAnswer: UserAnswer) {
        notesDao.insertAnswer(userAnswer)
    }

    suspend fun getCorrectAnswersCount(): Int {
        return notesDao.getCorrectAnswersCount()
    }

    suspend fun getIncorrectAnswersCount(): Int {
        return notesDao.getIncorrectAnswersCount()
    }
}
