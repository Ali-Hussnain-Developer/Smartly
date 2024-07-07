package com.example.smartly.Util

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import com.example.smartly.R


object ShowEmptyListDialog {

    interface OnCategorySelectedListener {
        fun onCategorySelected()
    }

    @SuppressLint("MissingInflatedId")
    fun showEmptyListDialog(context: Context, listener: OnCategorySelectedListener) {
        val dialogView =
            LayoutInflater.from(context).inflate(R.layout.dialog_empty_list, null)

        val selectCategory = dialogView.findViewById<TextView>(R.id.btnSelectAnotherCategory)
        val alertDialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        selectCategory.setOnClickListener {
            alertDialog.dismiss()
            listener.onCategorySelected()
        }

        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog.show()
    }

}

