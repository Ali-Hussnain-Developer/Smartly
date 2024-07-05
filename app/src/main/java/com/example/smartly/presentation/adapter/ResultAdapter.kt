package com.example.smartly.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smartly.R
import com.example.smartly.domain.model.UserAnswer

class ResultAdapter(private val items: List<UserAnswer>) : RecyclerView.Adapter<ResultAdapter.ResultViewHolder>() {

    class ResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvQuestion: TextView = itemView.findViewById(R.id.tv_question)
        val tvCorrectAnswer: TextView = itemView.findViewById(R.id.tv_correct_answer)
        val tvSelectedAnswer: TextView = itemView.findViewById(R.id.tv_selected_answer)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.result_screen_single_item, parent, false)
        return ResultViewHolder(itemView)
    }
    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        val currentItem = items[position]
        holder.tvQuestion.text = currentItem.question
        holder.tvCorrectAnswer.text = currentItem.correctAnswer
        holder.tvSelectedAnswer.text = currentItem.selectedAnswer
    }
    override fun getItemCount() = items.size
}
