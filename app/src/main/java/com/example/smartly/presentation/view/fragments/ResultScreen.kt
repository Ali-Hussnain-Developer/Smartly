package com.example.smartly.presentation.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartly.BR
import com.example.smartly.R
import com.example.smartly.Util.FragmentTransactionClass
import com.example.smartly.Util.SharedPreferencesHelper
import com.example.smartly.databinding.FragmentResultScreenBinding
import com.example.smartly.databinding.ResultScreenSingleItemBinding
import com.example.smartly.domain.model.UserAnswer
import com.example.smartly.presentation.adapter.GenericRecyclerViewAdapter
import com.example.smartly.presentation.viewModel.ViewModels
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class ResultScreen : Fragment() {
    private lateinit var binding: FragmentResultScreenBinding
    private lateinit var adapter: GenericRecyclerViewAdapter<UserAnswer, ResultScreenSingleItemBinding>

    @Inject
    lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    val viewModel: ViewModels by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_result_screen, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialization()
    }

    private fun clickListener() {
        binding.btnStartQuiz.setOnClickListener {
            FragmentTransactionClass.fragmentTransaction(parentFragmentManager,QuizSetupScreen())
        }
    }

    private fun initialization() {
        clickListener()
        setUserCategoryAndScore()
        setRecyclerView()
    }

    private fun setRecyclerView() {
        viewModel.allAnswers.observe(viewLifecycleOwner) { result ->
            adapter.setItems(result)
        }
        binding.recyclerViewResult.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewResult.setHasFixedSize(true)
        adapter = GenericRecyclerViewAdapter(
            requireContext(),
            emptyList(),
            R.layout.result_screen_single_item,
            BR.userAnswer
        ) {
        }

        binding.recyclerViewResult.adapter = adapter

    }

    private fun setUserCategoryAndScore() {
        val userSelectedCategory=sharedPreferencesHelper.getUserCategory()
        val userTotalScore=sharedPreferencesHelper.getUserTotalScore()
        binding.userCategoryTextView.text=userSelectedCategory
        binding.totalScoreTextViewResultScreen.text=userTotalScore
    }


}