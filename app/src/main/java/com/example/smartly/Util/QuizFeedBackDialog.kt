package com.example.smartly.Util

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.smartly.R

class QuizFeedBackDialog {

    companion object{

         fun showFeedbackDialog(isCorrect: Boolean,context: Context) {
            val dialogView =
                LayoutInflater.from(context).inflate(R.layout.dialog_feedback, null)
            val feedbackImageView = dialogView.findViewById<ImageView>(R.id.feedbackImageView)
            val feedbackTextView = dialogView.findViewById<TextView>(R.id.feedbackTextView)

            if (isCorrect) {
                feedbackImageView.setImageResource(R.drawable.ic_correct)
                feedbackTextView.text = "Correct Answer"
                feedbackTextView.setTextColor(
                    ContextCompat.getColor(
                        context,
                        android.R.color.holo_green_dark
                    )
                )
            } else {
                feedbackImageView.setImageResource(R.drawable.ic_wrong)
                feedbackTextView.text = "Wrong Answer"
                feedbackTextView.setTextColor(
                    ContextCompat.getColor(
                        context,
                        android.R.color.holo_red_dark
                    )
                )
            }

            val alertDialog = AlertDialog.Builder(context)
                .setView(dialogView)
                .setCancelable(true)
                .create()

            // Set the dialog window background to transparent
            alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            alertDialog.show()

            val fadeOut = AnimationUtils.loadAnimation(context, R.anim.fade_out)
            dialogView.postDelayed({
                dialogView.startAnimation(fadeOut)
                dialogView.postDelayed({
                    alertDialog.dismiss()
                }, fadeOut.duration)
            }, 300)
        }
    }
}