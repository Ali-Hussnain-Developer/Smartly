package com.example.smartly.Repository

import com.example.smartly.dao.NotesDao
import com.example.smartly.model.NotesModelClass
import com.example.smartly.model.UserAnswer
import kotlinx.coroutines.flow.Flow


class NotesRepository(val notesDao: NotesDao) {

    // Read operations
    val allNotes: Flow<List<NotesModelClass>> = notesDao.getAllNote()
    val allAnswers: Flow<List<UserAnswer>> = notesDao.getAllResult()

    // Insert operation
    suspend fun insert(note: NotesModelClass) {
        notesDao.insertNote(note)
    }


}
