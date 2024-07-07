package com.example.smartly.Util

import android.content.Context
import android.widget.Toast

object ToastHandler {
    fun showToast(context: Context, message: String){
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show()
    }

}