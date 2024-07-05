package com.example.smartly.presentation.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.smartly.R
import com.example.smartly.domain.model.NotesModelClass

class NotesAdapter(
    val context: Context
) : RecyclerView.Adapter<NotesAdapter.NotesViewHolder>(){

    private var notesList: List<NotesModelClass> = ArrayList()

    class NotesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDescription: TextView = itemView.findViewById(R.id.tv_description)
        val cardView: CardView = itemView.findViewById(R.id.card_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.single_item_view, parent, false)
        return NotesViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        val currentNotes = notesList[position]
        holder.tvDescription.text = currentNotes.thoughts
        holder.cardView.setOnClickListener {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, currentNotes.thoughts)
                type = "text/plain"
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share your thought via"))
        }
    }

    fun setNotes(notes: List<NotesModelClass>) {
        this.notesList = notes
        notifyDataSetChanged()
    }

    override fun getItemCount() = notesList.size


}
