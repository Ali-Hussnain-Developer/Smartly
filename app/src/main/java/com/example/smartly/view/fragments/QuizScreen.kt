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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import com.example.smartly.Util.ApiState
import com.example.smartly.Util.SharedPreferencesHelper
import com.example.smartly.dao.NotesDatabase
import com.example.smartly.model.Question
import com.example.smartly.model.UserAnswer
import com.example.smartly.databinding.FragmentQuizScreenBinding
import com.example.smartly.view.activities.MainActivity
import com.example.smartly.viewModel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
    lateinit var progressBar: ProgressBar
    lateinit var submitButton: Button
    private var isQuizStarted = false
    private var userTotalScore:Int?=0
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
        loadQuestions()
        createNotificationChannel(requireContext())
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
        val type = if (selectedQuestionType!!.contains("True/False")) {
            "boolean"
        } else {
            "multiple"
        }
        if (!isQuizStarted) {
            lifecycleScope.launch {
                db.notesDao().deleteAllUserAnswers()
            }
            isQuizStarted = true // Mark the quiz as started
        }
        categoryId.let {
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
        showFeedbackDialog(isCorrect)
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
            val message =
                "Correct: $correctCount, Incorrect: $incorrectCount Quiz Mode $selectedDifficulty Total Score $userTotalScore "
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()

            showScoreDialog(selectedDifficulty.toString(), correctCount)
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


    fun showNotification(context: Context, message: String) {
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

    private fun showFeedbackDialog(isCorrect: Boolean) {
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_feedback, null)
        val feedbackImageView = dialogView.findViewById<ImageView>(R.id.feedbackImageView)
        val feedbackTextView = dialogView.findViewById<TextView>(R.id.feedbackTextView)

        if (isCorrect) {
            feedbackImageView.setImageResource(R.drawable.ic_correct)
            feedbackTextView.text = "Correct Answer"
            feedbackTextView.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    android.R.color.holo_green_dark
                )
            )
        } else {
            feedbackImageView.setImageResource(R.drawable.ic_wrong)
            feedbackTextView.text = "Wrong Answer"
            feedbackTextView.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    android.R.color.holo_red_dark
                )
            )
        }

        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        // Set the dialog window background to transparent
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        alertDialog.show()

        val fadeOut = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_out)
        dialogView.postDelayed({
            dialogView.startAnimation(fadeOut)
            dialogView.postDelayed({
                alertDialog.dismiss()
            }, fadeOut.duration)
        }, 500)
    }

    @SuppressLint("MissingInflatedId")
    private fun showScoreDialog(userQuizMode: String, userCorrectAnswer: Int) {
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_total_score, null)

        val userScoreTextView = dialogView.findViewById<TextView>(R.id.totalScoreTextView)
        val userQuizModeTextView = dialogView.findViewById<TextView>(R.id.userQuizModeTextView)
        val userCorrectedAnswerTextView = dialogView.findViewById<TextView>(R.id.userCorrectedAnswerTextView)
        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        if (userQuizMode.contains("Easy")) {
            userTotalScore=userCorrectAnswer*1

        } else if (userQuizMode.contains("Medium")) {
            userTotalScore=userCorrectAnswer*2

        } else if (userQuizMode.contains("Hard")) {
            userTotalScore=userCorrectAnswer*3

        }
        userScoreTextView.text=userTotalScore.toString()
        userQuizModeTextView.text=userQuizMode
        userCorrectedAnswerTextView.text=userCorrectAnswer.toString()
        sharedPreferencesHelper.setUserTotalScore(userTotalScore.toString())
        alertDialog.show()

    }

}
