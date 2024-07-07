package com.example.smartly.domain.Repository

import com.example.smartly.data.dao.NotesDao
import com.example.smartly.domain.model.NotesModelClass
import com.example.smartly.domain.model.UserAnswer
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotesRepository @Inject constructor(private val notesDao: NotesDao) {

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

