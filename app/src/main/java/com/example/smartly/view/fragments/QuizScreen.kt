package com.example.smartly.view.fragments

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.smartly.R
import com.example.smartly.apiInterface.RetrofitInstance
import com.example.smartly.apiInterface.TriviaApi
import com.example.smartly.model.TriviaResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import com.example.smartly.AppDatabase
import com.example.smartly.model.Question
import com.example.smartly.model.UserAnswer
import com.example.smartly.databinding.FragmentQuizScreenBinding
import com.example.smartly.view.activities.MainActivity
import kotlinx.coroutines.launch


class QuizScreen : Fragment() {
    private var _binding: FragmentQuizScreenBinding? = null
    private val binding get() = _binding!!
var categoryId:Int?=0
var selectedDifficulty:String?=null
var selectedQuestionType:String?=null
    private val db by lazy { AppDatabase.getDatabase(requireContext()) }
    private var currentQuestionIndex = 0
    private var questions: List<Question> = listOf()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentQuizScreenBinding.inflate(inflater, container, false)
        loadQuestions()
        binding.submitAnswerButton.setOnClickListener {
            checkAnswer()
        }
        createNotificationChannel(requireContext())
        return binding.root
    }

    private fun loadQuestions() {
        val amount = 10
        if (arguments != null) {
            categoryId = requireArguments().getInt("categoryId")
            selectedDifficulty = requireArguments().getString("selectedDifficulty")
            selectedQuestionType = requireArguments().getString("selectedQuestionType")
        }
        var type:String
        if(selectedQuestionType!!.contains("True/False")){
            type = "boolean"
        }
        else{
            type="multiple"
        }

        val triviaApi = RetrofitInstance.retrofit.create(TriviaApi::class.java)
        val call = categoryId?.let {
            selectedDifficulty?.let { it1 ->
                triviaApi.getTriviaQuestions(amount, it, it1.lowercase(
                    Locale.ROOT
                ), type)
            }
        }

        call?.enqueue(object : Callback<TriviaResponse> {
            override fun onResponse(call: Call<TriviaResponse>, response: Response<TriviaResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        questions = it.results
                        displayQuestion()
                    }
                }
            }

            override fun onFailure(call: Call<TriviaResponse>, t: Throwable) {
                // Handle failure
            }
        })
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
            db.userAnswerDao().insert(userAnswer)
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
            val correctCount = db.userAnswerDao().getCorrectAnswersCount()
            val incorrectCount = db.userAnswerDao().getIncorrectAnswersCount()
            val message = "Correct: $correctCount, Incorrect: $incorrectCount Quiz Mode $selectedDifficulty"
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
            showNotification(requireContext(),message)
            val bundle = Bundle()
            bundle.putInt("correctCount", correctCount)
            bundle.putInt("inCorrectCount", incorrectCount)
            bundle.putString("selectedDifficultyLevel", selectedDifficulty)
            val resultFragment: Fragment = ResultScreen()
            resultFragment.arguments = bundle
            resultFragment.arguments = bundle
            val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, resultFragment)
            transaction.addToBackStack(null)
            transaction.commit()

        }
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




    /*private fun callQuizApi(
        selectedCategoryId: Int,
        selectedDifficulty: String,
        selectedQuestionType: String
    ) {
        val triviaApi = RetrofitInstance.retrofit.create(TriviaApi::class.java)

        // Parameters
        val amount = 10
        var type:String
        if(selectedQuestionType.contains("True/False")){
            type = "boolean"
        }
        else{
            type="multiple"
        }
        val call = triviaApi.getTriviaQuestions(amount, selectedCategoryId, selectedDifficulty.lowercase(
            Locale.ROOT
        ), type)
        Log.d("selectedCategoryId","$selectedCategoryId")
        Log.d("selectedDifficulty",selectedDifficulty.lowercase(
            Locale.ROOT
        ))
        Log.d("selectedQuestionType",type)

        call.enqueue(object : Callback<TriviaResponse> {
            override fun onResponse(call: Call<TriviaResponse>, response: Response<TriviaResponse>) {
                if (response.isSuccessful) {
                    val triviaResponse = response.body()
                    triviaResponse?.let {
                        // Handle the response
                        it.results.forEach { question ->
                            val decodedQuestion = Html.fromHtml(question.question, Html.FROM_HTML_MODE_LEGACY).toString()
                            val decodedCorrectAnswer = Html.fromHtml(question.correct_answer, Html.FROM_HTML_MODE_LEGACY).toString()
                            val decodedIncorrectAnswers = question.incorrect_answers.map { answer ->
                                Html.fromHtml(answer, Html.FROM_HTML_MODE_LEGACY).toString()
                            }

                            Log.d("Trivia", "Question: $decodedQuestion")
                            Log.d("Trivia", "Correct Answer: $decodedCorrectAnswer")
                            Log.d("Trivia", "Incorrect Answers: ${decodedIncorrectAnswers.joinToString(", ")}")
                            Log.d("QuestionSize", "QuestionSize: ${triviaResponse.results.size}")
                        }
                    }
                } else {
                    Log.e("Trivia", "Failed to get response")
                }
            }

            override fun onFailure(call: Call<TriviaResponse>, t: Throwable) {
                Log.e("Trivia", "API call failed", t)
            }
        })
    }*/
