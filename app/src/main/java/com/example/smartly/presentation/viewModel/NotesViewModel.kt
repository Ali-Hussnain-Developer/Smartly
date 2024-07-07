package com.example.smartly.presentation.viewModel

import androidx.lifecycle.*
import com.example.smartly.domain.Repository.NotesRepository
import com.example.smartly.domain.model.NotesModelClass
import com.example.smartly.domain.model.UserAnswer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(private val notesRepository: NotesRepository) : ViewModel() {

    val allNotes: LiveData<List<NotesModelClass>> = notesRepository.allNotes.asLiveData()
    val allAnswers: LiveData<List<UserAnswer>> = notesRepository.allAnswers.asLiveData()

    fun insertNotes(notes: NotesModelClass) = viewModelScope.launch(Dispatchers.IO) {
        notesRepository.insert(notes)
    }

    fun insertAnswer(userAnswer: UserAnswer) = viewModelScope.launch(Dispatchers.IO) {
        notesRepository.insertAnswer(userAnswer)
    }
    fun deleteAllUserAnswers() = viewModelScope.launch(Dispatchers.IO) {
        notesRepository.deleteAllUserAnswers()
    }


    suspend fun getCorrectAnswersCount(): Int {
        return withContext(Dispatchers.IO) {
            notesRepository.getCorrectAnswersCount()
        }
    }

    suspend fun getIncorrectAnswersCount(): Int {
        return withContext(Dispatchers.IO) {
            notesRepository.getIncorrectAnswersCount()
        }
    }
}

