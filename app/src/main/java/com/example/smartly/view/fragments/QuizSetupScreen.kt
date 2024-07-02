package com.example.smartly.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.smartly.R
import com.example.smartly.databinding.FragmentQuizSetupScreenBinding


class QuizSetupScreen : Fragment() {
    private var _binding: FragmentQuizSetupScreenBinding? = null
    private val binding get() = _binding!!
    private var selectedCategory: String? = null
    private var selectedDifficulty: String? = null
    private var selectedQuestionType: String? = null
    private var categoryId:Int?=0

    private val categories = listOf(
         "General Knowledge",
         "Entertainment: Books",
         "Entertainment: Film",
         "Entertainment: Music",
         "Entertainment: Musicals & Theatres",
         "Entertainment: Television",
         "Entertainment: Video Games",
         "Entertainment: Board Games",
         "Science & Nature",
         "Science: Computers",
         "Science: Mathematics",
         "Mythology",

    )
    private val difficulties = listOf(
        "Any Difficulty","Easy", "Medium", "Hard"
    )
    private val questionTypes = listOf(
       "Any Type", "Multiple Choice", "True/False"
    )
    //if user select True/False then send parameter of boolean
    //if user select Multiple Choice then send parameter of multiple
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizSetupScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSpinner(binding.spinnerCategories, categories) { selectedCategory = it }
        setupSpinner(binding.spinnerDifficulty, difficulties) { selectedDifficulty = it }
        setupSpinner(binding.spinnerQuestionType, questionTypes) { selectedQuestionType = it }

        binding.buttonNext.setOnClickListener {
            if (selectedCategory != null && selectedDifficulty != null && selectedQuestionType != null) {
                val bundle = Bundle()
                categoryId?.let { it1 -> bundle.putInt("categoryId", it1) }
                bundle.putString("selectedDifficulty", "$selectedDifficulty")
                bundle.putString("selectedQuestionType", "$selectedQuestionType")
                val quizFragment: Fragment = QuizScreen()
                quizFragment.arguments = bundle
                quizFragment.arguments = bundle
                val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_container, quizFragment)
                transaction.addToBackStack(null)
                transaction.commit()
                val message = "Category: $selectedCategory\nDifficulty: $selectedDifficulty\nQuestion Type: $selectedQuestionType"
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(requireContext(), "Please select all options", Toast.LENGTH_SHORT).show()
            }
        }
    }

  /*  private fun callQuizApi(
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

    private fun setupSpinner(spinner: Spinner, items: List<String>, onItemSelected: (String) -> Unit) {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                 categoryId = position + 8
                val selectedItem = parent.getItemAtPosition(position) as String
                onItemSelected(selectedItem)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }
    }

}