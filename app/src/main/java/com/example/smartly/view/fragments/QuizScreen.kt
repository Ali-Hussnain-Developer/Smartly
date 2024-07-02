package com.example.smartly.view.fragments

import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.smartly.R
import com.example.smartly.RetrofitInstance
import com.example.smartly.TriviaApi
import com.example.smartly.TriviaResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import com.example.smartly.AppDatabase
import com.example.smartly.Question
import com.example.smartly.UserAnswer
import kotlinx.coroutines.launch


class QuizScreen : Fragment() {
  /*  private lateinit var questionTextView: TextView
    private lateinit var optionsRadioGroup: RadioGroup
    private lateinit var option1RadioButton: RadioButton
    private lateinit var option2RadioButton: RadioButton
    private lateinit var option3RadioButton: RadioButton
    private lateinit var option4RadioButton: RadioButton
    private lateinit var submitAnswerButton: Button
    private val db by lazy { AppDatabase.getDatabase(requireContext()) }
    private var currentQuestionIndex = 0
    private var questions: List<Question> = listOf()*/
/*var categoryId:Int?=0
var selectedDifficulty:String?=null
var selectedQuestionType:String?=null*/

    private lateinit var questionTextView: TextView
    private lateinit var optionsRadioGroup: RadioGroup
    private lateinit var option1RadioButton: RadioButton
    private lateinit var option2RadioButton: RadioButton
    private lateinit var option3RadioButton: RadioButton
    private lateinit var option4RadioButton: RadioButton
    private lateinit var submitAnswerButton: Button
    private val db by lazy { AppDatabase.getDatabase(requireContext()) }
    private var currentQuestionIndex = 0
    private var questions: List<Question> = listOf()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_quiz_screen, container, false)

        questionTextView = view.findViewById(R.id.questionTextView)
        optionsRadioGroup = view.findViewById(R.id.optionsRadioGroup)
        option1RadioButton = view.findViewById(R.id.option1RadioButton)
        option2RadioButton = view.findViewById(R.id.option2RadioButton)
        option3RadioButton = view.findViewById(R.id.option3RadioButton)
        option4RadioButton = view.findViewById(R.id.option4RadioButton)
        submitAnswerButton = view.findViewById(R.id.submitAnswerButton)

        loadQuestions()

        submitAnswerButton.setOnClickListener {
            checkAnswer()
        }

        return view
    }

    private fun loadQuestions() {
        val amount = 10
        val category = 21 // Example category ID
        val difficulty = "medium"
        val type = "multiple"
        val triviaApi = RetrofitInstance.retrofit.create(TriviaApi::class.java)
        val call = triviaApi.getTriviaQuestions(amount, category, difficulty, type)

        call.enqueue(object : Callback<TriviaResponse> {
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

            questionTextView.text = Html.fromHtml(question.question, Html.FROM_HTML_MODE_LEGACY).toString()
            option1RadioButton.text = options[0]
            option2RadioButton.text = options[1]
            option3RadioButton.text = options[2]
            option4RadioButton.text = options[3]

            optionsRadioGroup.clearCheck()
        } else {
            // Quiz finished
            showResults()
        }
    }

    private fun checkAnswer() {
        val selectedOptionId = optionsRadioGroup.checkedRadioButtonId
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
        optionsRadioGroup.postDelayed({
            view?.findViewById<RadioButton>(selectedOptionId)?.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.transparent))
            currentQuestionIndex++
            displayQuestion()
        }, 1000)
    }

    private fun showResults() {
        lifecycleScope.launch {
            val correctCount = db.userAnswerDao().getCorrectAnswersCount()
            val incorrectCount = db.userAnswerDao().getIncorrectAnswersCount()
            val message = "Correct: $correctCount, Incorrect: $incorrectCount"
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
            val bundle = Bundle()
            bundle.putInt("correctCount", correctCount)
            bundle.putInt("inCorrectCount", incorrectCount)
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

/*    fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)





        *//* if (arguments != null) {
                   categoryId = requireArguments().getInt("categoryId")
                   selectedDifficulty = requireArguments().getString("selectedDifficulty")
                   selectedQuestionType = requireArguments().getString("selectedQuestionType")
               }
               Log.d("categoryIdddd","$categoryId")
               Log.d("selectedDifficultyyyyyyy","$selectedDifficulty")
               Log.d("selectedQuestionTypee","$selectedQuestionType")
               callQuizApi(categoryId!!, selectedDifficulty!!, selectedQuestionType!!)*//*








    *//*    questionTextView = view.findViewById(R.id.questionTextView)
        optionsRadioGroup = view.findViewById(R.id.optionsRadioGroup)
        option1RadioButton = view.findViewById(R.id.option1RadioButton)
        option2RadioButton = view.findViewById(R.id.option2RadioButton)
        option3RadioButton = view.findViewById(R.id.option3RadioButton)
        option4RadioButton = view.findViewById(R.id.option4RadioButton)
        submitAnswerButton = view.findViewById(R.id.submitAnswerButton)

        loadQuestions()

        submitAnswerButton.setOnClickListener {
            checkAnswer()
        }*//*
    }*/
   /* private fun loadQuestions() {
        val amount = 10
        val category = 21 // Example category ID
        val difficulty = "medium"
        val type = "multiple"
        val triviaApi = RetrofitInstance.retrofit.create(TriviaApi::class.java)
        val call = triviaApi.getTriviaQuestions(amount, category, difficulty, type)

        call.enqueue(object : Callback<TriviaResponse> {
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

            questionTextView.text = Html.fromHtml(question.question, Html.FROM_HTML_MODE_LEGACY).toString()
            option1RadioButton.text = options[0]
            option2RadioButton.text = options[1]
            option3RadioButton.text = options[2]
            option4RadioButton.text = options[3]

            optionsRadioGroup.clearCheck()
        } else {
            // Quiz finished
            // Show results or navigate to results screen
        }
    }

    private fun checkAnswer() {
        val selectedOptionId = optionsRadioGroup.checkedRadioButtonId
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

      *//*  if (isCorrect) {
            view?.findViewById<RadioButton>(selectedOptionId)?.setBackgroundColor(getColor(android.R.color.holo_green_light))
        } else {
            view?.findViewById<RadioButton>(selectedOptionId)?.setBackgroundColor(getColor(android.R.color.holo_red_light))
        }*//*

        if (isCorrect) {
           view?.findViewById<RadioButton>(selectedOptionId)?.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.holo_green_light))
        } else {
            view?.findViewById<RadioButton>(selectedOptionId)?.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_light))
        }

        // Move to next question after a delay
        optionsRadioGroup.postDelayed({
            view?.findViewById<RadioButton>(selectedOptionId)?.setBackgroundColor(ContextCompat.getColor(requireContext(),android.R.color.transparent))
            currentQuestionIndex++
            displayQuestion()
        }, 1000)
    }*/

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
