package com.example.smartly.presentation.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.smartly.Util.ApiState
import com.example.smartly.domain.model.NotesModelClass
import com.example.smartly.domain.model.UserAnswer
import com.example.smartly.domain.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ViewModels @Inject constructor(private val repository: Repository) : ViewModel() {


    private val triviaStateFlow: MutableStateFlow<ApiState> = MutableStateFlow(ApiState.Empty)
    val _triviaStateFlow: StateFlow<ApiState> = triviaStateFlow

    fun getTriviaQuestions(amount: Int, category: Int, difficulty: String, type: String) = viewModelScope.launch {
        triviaStateFlow.value = ApiState.Loading

        repository.getTriviaQuestions(amount, category, difficulty, type)

            .catch { e ->
                triviaStateFlow.value = ApiState.Failure(e)
                Log.e("MainViewModel", "Error fetching trivia questions", e)
            }
            .collect { data ->
                triviaStateFlow.value = ApiState.Success(data.results)

            }
    }

    // Room DB operations
    val allNotes: LiveData<List<NotesModelClass>> = repository.allNotes.asLiveData()
    val allAnswers: LiveData<List<UserAnswer>> = repository.allAnswers.asLiveData()

    fun insertNotes(notes: NotesModelClass) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(notes)
    }

    fun insertAnswer(userAnswer: UserAnswer) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertAnswer(userAnswer)
    }

    suspend fun getCorrectAnswersCount(): Int {
        return withContext(Dispatchers.IO) {
            repository.getCorrectAnswersCount()
        }
    }

    suspend fun getIncorrectAnswersCount(): Int {
        return withContext(Dispatchers.IO) {
            repository.getIncorrectAnswersCount()
        }
    }

    fun deleteAllUserAnswers() = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteAllUserAnswers()
    }
}
