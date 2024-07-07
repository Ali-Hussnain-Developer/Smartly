package com.example.smartly.presentation.view.fragments

import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.smartly.Util.ApiState
import com.example.smartly.Util.FragmentTransactionClass
import com.example.smartly.Util.InternetConnectivity.isInternetAvailable
import com.example.smartly.Util.QuizFeedBackDialog
import com.example.smartly.Util.SharedPreferencesHelper
import com.example.smartly.Util.ShowEmptyListDialog
import com.example.smartly.Util.ShowNotificationClass
import com.example.smartly.Util.ShowScoreDialog
import com.example.smartly.Util.ToastHandler
import com.example.smartly.databinding.FragmentQuizScreenBinding
import com.example.smartly.domain.model.Question
import com.example.smartly.domain.model.UserAnswer
import com.example.smartly.presentation.viewModel.ViewModels
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject


@AndroidEntryPoint
class QuizScreen : Fragment(), ShowEmptyListDialog.OnCategorySelectedListener {
    private var _binding: FragmentQuizScreenBinding? = null
    private val binding get() = _binding!!
    @Inject
    lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private val viewModel: ViewModels by viewModels()
    private var questions: List<Question> = listOf()
    var categoryId: Int? = 0
    var selectedDifficulty: String? = null
    var selectedQuestionType: String? = null
    private var currentQuestionIndex = 0
    private var isQuizStarted = false
    var userTotalScore: Int? = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentQuizScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialization()

    }

    private fun initialization() {
        checkInternetConnectivity()
        clickListener()
    }

    private fun checkInternetConnectivity() {
        if (!isInternetAvailable(requireContext())) {
            ToastHandler.showToast(requireContext(),"No internet connection. Please check your network settings")
            FragmentTransactionClass.fragmentTransaction(parentFragmentManager,QuizSetupScreen())

        } else {
            loadQuestions()
        }
    }

    private fun clickListener() {
        binding.optionsRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            binding.submitAnswerButton.isEnabled =
                checkedId != -1 // Enable button if any radio button is selected
        }
        binding.submitAnswerButton.setOnClickListener {
            checkAnswer()
        }
        binding.submitAnswerButton.isEnabled = false

    }

    private fun loadQuestions() {
        val amount = 10
        if (arguments != null) {
            categoryId = requireArguments().getInt("categoryId")
            selectedDifficulty = requireArguments().getString("selectedDifficulty")
            selectedQuestionType = requireArguments().getString("selectedQuestionType")
        }
        val type: String?
        if (selectedQuestionType!!.contains("True/False")) {
            val categoryIdNew = (categoryId)!! + 8
            type = "boolean"
            viewModel.getTriviaQuestions(
                amount,
                categoryIdNew, selectedDifficulty!!.toLowerCase(Locale.ROOT), type
            )
        } else {
            type = "multiple"
            val categoryIdNew = (categoryId)!! + 9
            categoryIdNew.let {
                selectedDifficulty.let { difficulty ->
                    it?.let { it1 ->
                        difficulty?.let { it2 ->
                            viewModel.getTriviaQuestions(
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
                viewModel.deleteAllUserAnswers()
            }
            isQuizStarted = true // Mark the quiz as started
        }



        lifecycleScope.launchWhenStarted {
            viewModel._triviaStateFlow.collect { state ->
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
        if (questions.isEmpty()) {
            ShowEmptyListDialog.showEmptyListDialog(requireContext(), this)

        } else {
            binding.totalQuestionTextView.text = "Total Questions: ${questions.size}"
            binding.quizMode.text = "Quiz Mode: ${selectedDifficulty}"
            if (currentQuestionIndex < questions.size) {
                val question = questions[currentQuestionIndex]
                binding.questionNumberTextView.text = "Question # ${currentQuestionIndex + 1}"
                binding.optionsRadioGroup.visibility = View.VISIBLE
                binding.submitAnswerButton.visibility = View.VISIBLE
                binding.layoutAnswersCount.visibility = View.VISIBLE
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
        }
    }


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
            viewModel.insertAnswer(userAnswer)
        }
        binding.optionsRadioGroup.clearCheck()
        QuizFeedBackDialog.showFeedbackDialog(isCorrect, requireContext())
        lifecycleScope.launch {

            val incorrectCount = viewModel.getIncorrectAnswersCount()
            val correctCount = viewModel.getCorrectAnswersCount()
            binding.correctAnswerTextView.text = correctCount.toString()
            binding.InCorrectAnswerTextView.text = incorrectCount.toString()
        }


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
            val incorrectCount=  viewModel.getIncorrectAnswersCount()
            val correctCount = viewModel.getCorrectAnswersCount()

            userTotalScore = ShowScoreDialog.showScoreDialog(
                selectedDifficulty.toString(),
                correctCount,
                requireContext(),
                sharedPreferencesHelper
            )
            val message =
                "Correct: $correctCount, Incorrect: $incorrectCount Quiz Mode $selectedDifficulty Total Score $userTotalScore "
            ToastHandler.showToast(requireContext(),message)
            ShowNotificationClass.showNotification(requireContext(), message)
            val bundle = Bundle().apply {
                putInt("correctCount", correctCount)
                putInt("inCorrectCount", incorrectCount)
                putString("selectedDifficultyLevel", selectedDifficulty)
            }
            val resultFragment: Fragment = ResultScreen().apply {
                arguments = bundle
            }
            FragmentTransactionClass.fragmentTransaction(parentFragmentManager,resultFragment)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCategorySelected() {
        FragmentTransactionClass.fragmentTransaction(parentFragmentManager,QuizSetupScreen())

    }
}
