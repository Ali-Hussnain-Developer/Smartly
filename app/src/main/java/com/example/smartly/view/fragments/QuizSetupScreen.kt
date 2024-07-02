package com.example.smartly.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.example.smartly.R
import com.example.smartly.databinding.FragmentQuizSetupScreenBinding
import com.example.smartly.databinding.FragmentSignUpScreenBinding


class QuizSetupScreen : Fragment() {
    private var _binding: FragmentQuizSetupScreenBinding? = null
    private val binding get() = _binding!!
    private var selectedCategory: String? = null
    private var selectedDifficulty: String? = null
    private var selectedQuestionType: String? = null

    private val categories = listOf(
         "Any Category",
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
                val message = "Category: $selectedCategory\nDifficulty: $selectedDifficulty\nQuestion Type: $selectedQuestionType"
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(requireContext(), "Please select all options", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun setupSpinner(spinner: Spinner, items: List<String>, onItemSelected: (String) -> Unit) {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position) as String
                onItemSelected(selectedItem)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }
    }

}