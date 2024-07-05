package com.example.smartly

import android.app.Application
import com.example.smartly.data.dao.NotesDatabase
import com.example.smartly.domain.Repository.NotesRepository
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class NotesApplication :Application() {
val database by lazy { NotesDatabase.getDatabase(this)}
val repository by lazy { NotesRepository(database.notesDao()) }
}
