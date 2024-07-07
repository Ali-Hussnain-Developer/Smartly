package com.example.smartly.Util

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.smartly.R

object FragmentTransactionClass {
    fun fragmentTransaction(parentFragmentManager: FragmentManager, fragment: Fragment){
    val transaction = parentFragmentManager.beginTransaction()
    transaction.replace(R.id.fragment_container, fragment)
    transaction.commit()}
}