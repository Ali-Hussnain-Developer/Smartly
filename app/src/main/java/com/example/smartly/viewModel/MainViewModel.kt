package com.example.smartly.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartly.Repository.MainRepository
import com.example.smartly.Util.ApiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val mainRepository: MainRepository) : ViewModel() {

    private val triviaStateFlow: MutableStateFlow<ApiState> = MutableStateFlow(ApiState.Empty)
    val _triviaStateFlow: StateFlow<ApiState> = triviaStateFlow

    fun getTriviaQuestions(amount: Int, category: Int, difficulty: String, type: String) = viewModelScope.launch {
        triviaStateFlow.value = ApiState.Loading
        mainRepository.getTriviaQuestions(amount, category, difficulty, type)
            .catch { e ->
                triviaStateFlow.value = ApiState.Failure(e)
                Log.e("MainViewModel", "Error fetching trivia questions", e)
            }
            .collect { data ->
                triviaStateFlow.value = ApiState.Success(data.results)
            }
    }
}
