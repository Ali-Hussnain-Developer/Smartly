package com.example.smartly.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.smartly.Repository.NotesRepository
import com.example.smartly.model.NotesModelClass
import com.example.smartly.model.UserAnswer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotesViewModel(private val notesRepository: NotesRepository) : ViewModel() {
    val allNotes: LiveData<List<NotesModelClass>> = notesRepository.allNotes.asLiveData()
    val allAnswers: LiveData<List<UserAnswer>> = notesRepository.allAnswers.asLiveData()

    // Create
    fun insertNotes(notes: NotesModelClass) = viewModelScope.launch(Dispatchers.IO){
            notesRepository.insert(notes)
    }
}
class NotesViewModelFactory(private val repository: NotesRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotesViewModel::class.java)){
            return NotesViewModel(repository) as T
        }
        else{
            throw IllegalArgumentException("unknown View Model")
        }
    }
}
