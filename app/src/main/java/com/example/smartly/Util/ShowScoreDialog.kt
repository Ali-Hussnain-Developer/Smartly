package com.example.smartly.Util

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import com.example.smartly.R

class ShowScoreDialog {
    companion object{

        @SuppressLint("MissingInflatedId")
        fun showScoreDialog(userQuizMode: String, userCorrectAnswer: Int,context: Context,sharedPreferencesHelper: SharedPreferencesHelper): Int?
        {var userTotalScore:Int?=0
            val dialogView =
                LayoutInflater.from(context).inflate(R.layout.dialog_total_score, null)

            val userScoreTextView = dialogView.findViewById<TextView>(R.id.totalScoreTextView)
            val userQuizModeTextView = dialogView.findViewById<TextView>(R.id.userQuizModeTextView)
            val userCorrectedAnswerTextView = dialogView.findViewById<TextView>(R.id.userCorrectedAnswerTextView)
            val alertDialog = AlertDialog.Builder(context)
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
            return userTotalScore

        }
    }
}