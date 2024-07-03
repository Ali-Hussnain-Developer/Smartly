package com.example.smartly.applicationClass

import android.app.Application
import com.example.smartly.dao.NotesDatabase
import com.example.smartly.Repository.NotesRepository
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class NotesApplication :Application() {
val database by lazy { NotesDatabase.getDatabase(this)}
val repository by lazy { NotesRepository(database.notesDao()) }
}
