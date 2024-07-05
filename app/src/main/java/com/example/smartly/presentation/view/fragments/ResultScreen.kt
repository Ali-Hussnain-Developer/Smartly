package com.example.smartly.presentation.view.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartly.NotesApplication
import com.example.smartly.R
import com.example.smartly.Util.SharedPreferencesHelper
import com.example.smartly.databinding.FragmentResultScreenBinding
import com.example.smartly.presentation.adapter.ResultAdapter
import com.example.smartly.presentation.viewModel.NotesViewModel
import com.example.smartly.presentation.viewModel.NotesViewModelFactory


class ResultScreen : Fragment() {
    private var _binding: FragmentResultScreenBinding? = null
    private val binding get() = _binding!!
    lateinit var notesViewModel: NotesViewModel
    private lateinit var resultAdapter: ResultAdapter
    lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentResultScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialization()
        clickListener()
    }

    private fun clickListener() {

        binding.btnStartQuiz.setOnClickListener {
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, QuizSetupScreen())
            transaction.commit()
        }
    }

    private fun initialization() {
        sharedPreferencesHelper=SharedPreferencesHelper(requireContext())
        val userSelectedCategory=sharedPreferencesHelper.getUserCategory()
        val userTotalScore=sharedPreferencesHelper.getUserTotalScore()
        binding.userCategoryTextView.text=userSelectedCategory
        binding.totalScoreTextViewResultScreen.text=userTotalScore
        sharedPreferencesHelper.getUserTotalScore()
        val application = requireActivity().application as NotesApplication
        val viewModelFactory = NotesViewModelFactory(application.repository)
        notesViewModel = ViewModelProvider(this, viewModelFactory).get(NotesViewModel::class.java)
        binding.recyclerViewResult.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewResult.setHasFixedSize(true)
        notesViewModel.allAnswers.observe(viewLifecycleOwner) { result ->
            Log.d("result","${result.size}")
            resultAdapter= ResultAdapter(result)
            binding.recyclerViewResult.adapter=resultAdapter
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}