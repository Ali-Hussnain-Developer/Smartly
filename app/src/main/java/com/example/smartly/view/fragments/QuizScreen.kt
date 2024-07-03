package com.example.smartly.view.fragments

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.smartly.R
import java.util.Locale
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.smartly.Util.ApiState
import com.example.smartly.dao.NotesDatabase
import com.example.smartly.model.Question
import com.example.smartly.model.UserAnswer
import com.example.smartly.databinding.FragmentQuizScreenBinding
import com.example.smartly.view.activities.MainActivity
import com.example.smartly.viewModel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class QuizScreen : Fragment() {
    private var _binding: FragmentQuizScreenBinding? = null
    private val binding get() = _binding!!
    var categoryId: Int? = 0
    var selectedDifficulty: String? = null
    var selectedQuestionType: String? = null
    private val db by lazy { NotesDatabase.getDatabase(requireContext()) }
    private var currentQuestionIndex = 0
    private var questions: List<Question> = listOf()
    private val mainViewModel: MainViewModel by viewModels()

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
        clickListener()
    }

    private fun initialization() {
        loadQuestions()
        createNotificationChannel(requireContext())
    }

    private fun clickListener() {
        binding.submitAnswerButton.setOnClickListener {
            checkAnswer()
        }

    }

    private fun loadQuestions() {
        val amount = 10
        if (arguments != null) {
            categoryId = requireArguments().getInt("categoryId")
            selectedDifficulty = requireArguments().getString("selectedDifficulty")
            selectedQuestionType = requireArguments().getString("selectedQuestionType")
        }
        val type = if (selectedQuestionType!!.contains("True/False")) {
            "boolean"
        } else {
            "multiple"
        }

        categoryId.let {
            selectedDifficulty.let { difficulty ->
                it?.let { it1 ->
                    difficulty?.let { it2 ->
                        mainViewModel.getTriviaQuestions(amount, it1, it2.toLowerCase(Locale.ROOT), type)
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            mainViewModel._triviaStateFlow.collect { state ->
                when (state) {
                    is ApiState.Loading -> {
                        binding.progressBar.visibility=View.VISIBLE
                        Log.d("Loading", "yes")
                    }
                    is ApiState.Failure -> {
                        Log.d("Main", "onCreate: ${state.msg}")
                        binding.progressBar.visibility=View.INVISIBLE
                    }
                    is ApiState.Success -> {
                        binding.progressBar.visibility=View.INVISIBLE
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
        if (currentQuestionIndex < questions.size) {
            val question = questions[currentQuestionIndex]
            val options = question.incorrect_answers.toMutableList().apply {
                add(question.correct_answer)
                shuffle()
            }

            binding.questionTextView.text = Html.fromHtml(question.question, Html.FROM_HTML_MODE_LEGACY).toString()
            binding.option1RadioButton.text = options[0]
            binding.option2RadioButton.text = options[1]
            binding.option3RadioButton.text = options[2]
            binding.option4RadioButton.text = options[3]

            binding.optionsRadioGroup.clearCheck()
        } else {
            // Quiz finished
            showResults()
        }
    }

    private fun checkAnswer() {
        val selectedOptionId = binding.optionsRadioGroup.checkedRadioButtonId
        val selectedOption = view?.findViewById<RadioButton>(selectedOptionId)?.text.toString()

        val currentQuestion = questions[currentQuestionIndex]
        val correctAnswer = Html.fromHtml(currentQuestion.correct_answer, Html.FROM_HTML_MODE_LEGACY).toString()

        val isCorrect = selectedOption == correctAnswer
        val userAnswer = UserAnswer(
            question = Html.fromHtml(currentQuestion.question, Html.FROM_HTML_MODE_LEGACY).toString(),
            selectedAnswer = selectedOption,
            correctAnswer = correctAnswer,
            isCorrect = isCorrect
        )

        lifecycleScope.launch {
            db.notesDao().insert(userAnswer)
        }

        if (isCorrect) {
            view?.findViewById<RadioButton>(selectedOptionId)?.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.holo_green_light))
        } else {
            view?.findViewById<RadioButton>(selectedOptionId)?.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_light))
        }

        // Move to next question after a delay
        binding.optionsRadioGroup.postDelayed({
            view?.findViewById<RadioButton>(selectedOptionId)?.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.transparent))
            currentQuestionIndex++
            displayQuestion()
        }, 1000)
    }

    private fun showResults() {
        lifecycleScope.launch {
            val correctCount = db.notesDao().getCorrectAnswersCount()
            val incorrectCount = db.notesDao().getIncorrectAnswersCount()
            val message = "Correct: $correctCount, Incorrect: $incorrectCount Quiz Mode $selectedDifficulty"
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
            showNotification(requireContext(), message)
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
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "My Channel"
            val descriptionText = "Channel for My Notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("MY_CHANNEL_ID", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


    fun showNotification(context: Context,message:String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("fragment_to_open", "ResultFragment")
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, "MY_CHANNEL_ID")
            .setSmallIcon(R.drawable.quiz_app_logo)
            .setContentTitle("Your Quiz Result")
            .setContentText("$message")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "MY_CHANNEL_ID",
                "My Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Channel for My Notifications"
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        with(NotificationManagerCompat.from(context)) {
            notify(1, builder.build())
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
