package com.example.smartly.view.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import com.example.smartly.R
import java.util.Locale
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.smartly.Util.ApiState
import com.example.smartly.Util.CreateNotificationClass
import com.example.smartly.Util.InternetConnectivity.Companion.isInternetAvailable
import com.example.smartly.Util.QuizFeedBackDialog
import com.example.smartly.Util.SharedPreferencesHelper
import com.example.smartly.Util.ShowEmptyListDialog
import com.example.smartly.Util.ShowNotificationClass
import com.example.smartly.Util.ShowScoreDialog
import com.example.smartly.dao.NotesDatabase
import com.example.smartly.model.Question
import com.example.smartly.model.UserAnswer
import com.example.smartly.databinding.FragmentQuizScreenBinding
import com.example.smartly.view.activities.MainActivity
import com.example.smartly.viewModel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

import kotlinx.coroutines.launch


@AndroidEntryPoint
class QuizScreen : Fragment(), ShowEmptyListDialog.OnCategorySelectedListener {
    private var _binding: FragmentQuizScreenBinding? = null
    private val binding get() = _binding!!
    var categoryId: Int? = 0
    var selectedDifficulty: String? = null
    var selectedQuestionType: String? = null
    private val db by lazy { NotesDatabase.getDatabase(requireContext()) }
    private var currentQuestionIndex = 0
    private var questions: List<Question> = listOf()
    private val mainViewModel: MainViewModel by viewModels()
    lateinit var progressBar: ProgressBar
    lateinit var submitButton: Button
    private var isQuizStarted = false
     var userTotalScore :Int?=0

    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentQuizScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialization(view)
        clickListener()
    }

    private fun initialization(view: View) {
        sharedPreferencesHelper = SharedPreferencesHelper(requireContext())
        progressBar = view.findViewById(R.id.progress_bar)
        submitButton = view.findViewById(R.id.submitAnswerButton)
        if (!isInternetAvailable(requireContext())) {
            Toast.makeText(requireContext(), "No internet connection. Please check your network settings.", Toast.LENGTH_LONG).show()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, QuizSetupScreen())
                .addToBackStack(null)
                .commit()
        }
        else{
            loadQuestions()
        }
       CreateNotificationClass.createNotificationChannel(requireContext())
    }

    private fun clickListener() {
        binding.optionsRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            submitButton.isEnabled =
                checkedId != -1 // Enable button if any radio button is selected
        }
        binding.submitAnswerButton.setOnClickListener {
            checkAnswer()
        }
        submitButton.isEnabled = false

    }

    private fun loadQuestions() {
        val amount = 10
        if (arguments != null) {
            categoryId = requireArguments().getInt("categoryId")
            selectedDifficulty = requireArguments().getString("selectedDifficulty")
            selectedQuestionType = requireArguments().getString("selectedQuestionType")
        }
        val type:String?
        if (selectedQuestionType!!.contains("True/False")) {
            val categoryIdNew= (categoryId)!! +8
           type= "boolean"
            mainViewModel.getTriviaQuestions(amount,
                categoryIdNew,selectedDifficulty!!.toLowerCase(Locale.ROOT),type)
        } else {
           type= "multiple"
            val categoryIdNew= (categoryId)!! +9
            categoryIdNew.let {
          selectedDifficulty.let { difficulty ->
              it?.let { it1 ->
                  difficulty?.let { it2 ->
                      mainViewModel.getTriviaQuestions(
                          amount,
                          it1,
                          it2.toLowerCase(Locale.ROOT),
                          type
                      )
                  }
              }
          }
      }
        }

        if (!isQuizStarted) {
            lifecycleScope.launch {
                db.notesDao().deleteAllUserAnswers()
            }
            isQuizStarted = true // Mark the quiz as started
        }



        lifecycleScope.launchWhenStarted {
            mainViewModel._triviaStateFlow.collect { state ->
                when (state) {
                    is ApiState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        Log.d("Loading", "yes")
                    }

                    is ApiState.Failure -> {
                        Log.d("Main", "onCreate: ${state.msg}")
                        binding.progressBar.visibility = View.INVISIBLE
                    }

                    is ApiState.Success -> {
                        binding.progressBar.visibility = View.INVISIBLE
                        // Display questions
                        questions = state.data
                        displayQuestion()

                    }

                    is ApiState.Empty -> {
                        // Initial state
                    }
                }
            }
        }
    }

    private fun displayQuestion() {
        if(questions.isEmpty()){
            ShowEmptyListDialog.showEmptyListDialog(requireContext(), this)

        }else{
        binding.totalQuestionTextView.text = "Total Questions: ${questions.size}"
        binding.quizMode.text = "Quiz Mode: ${selectedDifficulty}"
        if (currentQuestionIndex < questions.size) {
            val question = questions[currentQuestionIndex]
            binding.questionNumberTextView.text = "Question # ${currentQuestionIndex + 1}"
            binding.optionsRadioGroup.visibility = View.VISIBLE
            binding.submitAnswerButton.visibility = View.VISIBLE
            val options = question.incorrect_answers.toMutableList().apply {
                add(question.correct_answer)
                shuffle()
            }

            binding.questionTextView.text =
                Html.fromHtml(question.question, Html.FROM_HTML_MODE_LEGACY).toString()

            if (question.type == "boolean") {
                binding.option1RadioButton.visibility = View.VISIBLE
                binding.option2RadioButton.visibility = View.VISIBLE
                binding.option3RadioButton.visibility = View.GONE
                binding.option4RadioButton.visibility = View.GONE
                binding.option1RadioButton.text = options[0]
                binding.option2RadioButton.text = options[1]
            } else {
                binding.option1RadioButton.visibility = View.VISIBLE
                binding.option2RadioButton.visibility = View.VISIBLE
                binding.option3RadioButton.visibility = View.VISIBLE
                binding.option4RadioButton.visibility = View.VISIBLE
                binding.option1RadioButton.text = options[0]
                binding.option2RadioButton.text = options[1]
                binding.option3RadioButton.text = options[2]
                binding.option4RadioButton.text = options[3]
            }


            binding.optionsRadioGroup.clearCheck()
        } else {
            // Quiz finished
            showResults()
        }
    }}


    private fun checkAnswer() {
        val selectedOptionId = binding.optionsRadioGroup.checkedRadioButtonId
        val selectedOption = view?.findViewById<RadioButton>(selectedOptionId)?.text.toString()

        val currentQuestion = questions[currentQuestionIndex]
        val correctAnswer =
            Html.fromHtml(currentQuestion.correct_answer, Html.FROM_HTML_MODE_LEGACY).toString()

        val isCorrect = selectedOption == correctAnswer
        val userAnswer = UserAnswer(
            question = Html.fromHtml(currentQuestion.question, Html.FROM_HTML_MODE_LEGACY)
                .toString(),
            selectedAnswer = selectedOption,
            correctAnswer = correctAnswer,
            isCorrect = isCorrect
        )

        lifecycleScope.launch {
            db.notesDao().insert(userAnswer)
        }
        binding.optionsRadioGroup.clearCheck()
        QuizFeedBackDialog.showFeedbackDialog(isCorrect,requireContext())

        // Move to next question after a delay
        binding.optionsRadioGroup.postDelayed({
            view?.findViewById<RadioButton>(selectedOptionId)?.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    android.R.color.transparent
                )
            )
            currentQuestionIndex++
            displayQuestion()
        }, 1000)
    }

    private fun showResults() {
        lifecycleScope.launch {
            val correctCount = db.notesDao().getCorrectAnswersCount()
            val incorrectCount = db.notesDao().getIncorrectAnswersCount()

            userTotalScore= ShowScoreDialog.showScoreDialog(selectedDifficulty.toString(), correctCount,requireContext(),sharedPreferencesHelper)
            val message =
                "Correct: $correctCount, Incorrect: $incorrectCount Quiz Mode $selectedDifficulty Total Score $userTotalScore "
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
            ShowNotificationClass.showNotification(requireContext(), message)
            val bundle = Bundle().apply {
                putInt("correctCount", correctCount)
                putInt("inCorrectCount", incorrectCount)
                putString("selectedDifficultyLevel", selectedDifficulty)
            }
            val resultFragment: Fragment = ResultScreen().apply {
                arguments = bundle
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, resultFragment)
                .addToBackStack(null)
                .commit()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCategorySelected() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, QuizSetupScreen())
            .addToBackStack(null)
            .commit()
    }
}
