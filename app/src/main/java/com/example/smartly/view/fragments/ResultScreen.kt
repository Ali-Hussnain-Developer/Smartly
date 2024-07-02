package com.example.smartly.view.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.smartly.R


class ResultScreen : Fragment() {
var correctAnswer:Int?=0
var inCorrectAnswer:Int?=0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_result_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
if (arguments != null) {
    correctAnswer = requireArguments().getInt("correctCount")
    inCorrectAnswer = requireArguments().getInt("inCorrectCount")
}
Log.d("correctAnswer","$correctAnswer")
Log.d("inCorrectAnswer","$inCorrectAnswer")
}

}