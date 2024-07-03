package com.example.smartly.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.smartly.model.NotesModelClass
import kotlinx.coroutines.flow.Flow


@Dao
interface NotesDao {
    // Create
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NotesModelClass)

    @Query("SELECT * FROM Notes_Table ORDER BY id ASC")
    fun getAllNote(): Flow<List<NotesModelClass>>

    // Update
    @Update
    suspend fun updateNote(note: NotesModelClass)

    // Delete
    @Delete
    suspend fun deleteNote(note: NotesModelClass)

    @Query("DELETE FROM Notes_Table")
    suspend fun deleteAllNotes()
}
