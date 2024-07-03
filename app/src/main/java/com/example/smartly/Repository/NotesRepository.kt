package com.example.smartly.Repository

import com.example.smartly.dao.NotesDao
import com.example.smartly.model.NotesModelClass
import kotlinx.coroutines.flow.Flow


class NotesRepository(val notesDao: NotesDao) {

    // Read operations
    val allNotes: Flow<List<NotesModelClass>> = notesDao.getAllNote()

    // Insert operation
    suspend fun insert(note: NotesModelClass) {
        notesDao.insertNote(note)
    }


    // Update operation
    suspend fun update(note: NotesModelClass) {
        notesDao.updateNote(note)
    }

    // Delete operation
    suspend fun delete(note: NotesModelClass) {
        notesDao.deleteNote(note)
    }

    // Delete all users
    suspend fun deleteAll() {
        notesDao.deleteAllNotes()
    }


}
