package com.example.smartly.view.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartly.adapter.ResultAdapter
import com.example.smartly.applicationClass.NotesApplication
import com.example.smartly.databinding.FragmentResultScreenBinding
import com.example.smartly.viewModel.NotesViewModel
import com.example.smartly.viewModel.NotesViewModelFactory


class ResultScreen : Fragment() {
    private var _binding: FragmentResultScreenBinding? = null
    private val binding get() = _binding!!
    var correctAnswer: Int? = 0
    var inCorrectAnswer: Int? = 0
    var selectedDifficultyLevel: String? = null
    lateinit var notesViewModel: NotesViewModel
    private lateinit var resultAdapter: ResultAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentResultScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val application = requireActivity().application as NotesApplication
        val viewModelFactory = NotesViewModelFactory(application.repository)
        notesViewModel = ViewModelProvider(this, viewModelFactory).get(NotesViewModel::class.java)
        binding.recyclerViewResult.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewResult.setHasFixedSize(true)
        notesViewModel.allAnswers.observe(viewLifecycleOwner) { result ->
            Log.d("result","${result.size}")
            resultAdapter=ResultAdapter(result)
            binding.recyclerViewResult.adapter=resultAdapter
        }
        if (arguments != null) {
            correctAnswer = requireArguments().getInt("correctCount")
            inCorrectAnswer = requireArguments().getInt("inCorrectCount")
            selectedDifficultyLevel = requireArguments().getString("selectedDifficultyLevel")
        }
        if (selectedDifficultyLevel != null) {
            if (selectedDifficultyLevel!!.contains("Easy")) {
                var totalPoints = correctAnswer!! * 1
                //binding.tvCorrectAnswer.text= totalPoints.toString()
                Toast.makeText(requireContext(), "$totalPoints", Toast.LENGTH_SHORT).show()
            } else if (selectedDifficultyLevel!!.contains("Medium")) {
                var totalPoints = correctAnswer!! * 2
                //binding.tvCorrectAnswer.text= totalPoints.toString()
                Toast.makeText(requireContext(), "$totalPoints", Toast.LENGTH_SHORT).show()
            } else if (selectedDifficultyLevel!!.contains("Hard")) {
                var totalPoints = correctAnswer!! * 3
                //  binding.tvCorrectAnswer.text= totalPoints.toString()
                Toast.makeText(requireContext(), "$totalPoints", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Else", Toast.LENGTH_SHORT).show()
            }
        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}