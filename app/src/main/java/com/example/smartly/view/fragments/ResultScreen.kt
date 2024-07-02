package com.example.smartly.view.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.smartly.R
import com.example.smartly.databinding.FragmentResultScreenBinding
import com.example.smartly.databinding.FragmentSignUpScreenBinding


class ResultScreen : Fragment() {
    private var _binding: FragmentResultScreenBinding? = null
    private val binding get() = _binding!!
var correctAnswer:Int?=0
var inCorrectAnswer:Int?=0
    var selectedDifficultyLevel:String?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentResultScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
if (arguments != null) {
    correctAnswer = requireArguments().getInt("correctCount")
    inCorrectAnswer = requireArguments().getInt("inCorrectCount")
    selectedDifficultyLevel = requireArguments().getString("selectedDifficultyLevel")
}
if(selectedDifficultyLevel!=null){
        if(selectedDifficultyLevel!!.contains("Easy")){
            var totalPoints= correctAnswer!! *1
            binding.tvCorrectAnswer.text= totalPoints.toString()
        }
        else  if(selectedDifficultyLevel!!.contains("Medium")){
            var totalPoints= correctAnswer!! *2
            binding.tvCorrectAnswer.text= totalPoints.toString()
        }else  if(selectedDifficultyLevel!!.contains("Hard")){
            var totalPoints= correctAnswer!! *3
            binding.tvCorrectAnswer.text= totalPoints.toString()
        }else{
            Toast.makeText(requireContext(),"Else",Toast.LENGTH_SHORT).show()
        }
}

}

}