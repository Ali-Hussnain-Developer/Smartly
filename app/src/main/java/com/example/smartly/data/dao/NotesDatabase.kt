package com.example.smartly.data.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.smartly.domain.model.NotesModelClass
import com.example.smartly.domain.model.UserAnswer

@Database(entities = [NotesModelClass::class, UserAnswer::class], version = 1)
abstract class NotesDatabase : RoomDatabase() {
    abstract fun notesDao(): NotesDao
}