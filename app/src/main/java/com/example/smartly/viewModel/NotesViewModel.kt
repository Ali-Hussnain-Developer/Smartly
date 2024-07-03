package com.example.smartly.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.smartly.Repository.NotesRepository
import com.example.smartly.model.NotesModelClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotesViewModel(private val notesRepository: NotesRepository) : ViewModel() {
    val allNotes: LiveData<List<NotesModelClass>> = notesRepository.allNotes.asLiveData()



    // Create
    fun insertNotes(notes: NotesModelClass) = viewModelScope.launch(Dispatchers.IO){
            notesRepository.insert(notes)

    }


    // Update
    fun updateNotes(notes: NotesModelClass)  = viewModelScope.launch(Dispatchers.IO){
            notesRepository.update(notes)

    }

    // Delete
    fun deleteNotes(notes: NotesModelClass)  = viewModelScope.launch(Dispatchers.IO) {

        notesRepository.delete(notes)
    }

    fun deleteAllNotes()  = viewModelScope.launch(Dispatchers.IO){
            notesRepository.deleteAll()


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
