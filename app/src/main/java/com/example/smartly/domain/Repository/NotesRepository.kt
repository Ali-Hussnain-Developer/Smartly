package com.example.smartly.domain.Repository

import com.example.smartly.data.dao.NotesDao
import com.example.smartly.domain.model.NotesModelClass
import com.example.smartly.domain.model.UserAnswer
import kotlinx.coroutines.flow.Flow


class NotesRepository(val notesDao: NotesDao) {

    // Read operations
    val allNotes: Flow<List<NotesModelClass>> = notesDao.getAllNote()
    val allAnswers: Flow<List<UserAnswer>> = notesDao.getAllResult()

    // Insert operation
    suspend fun insert(note: NotesModelClass) {
        notesDao.insertNote(note)
    }


    suspend fun insertAnswer( userAnswer: UserAnswer) {
        notesDao.insertAnswer(userAnswer)
    }

}
