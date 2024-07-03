package com.example.smartly.Util

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesHelper(context: Context) {

    private val prefsName = "login_prefs"
    private val keyIsLoggedIn = "is_logged_in"
    private val keyUserName = "user_name"
    private val sharedPreferences: SharedPreferences

    init {
        sharedPreferences = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
    }

    fun setLoggedIn(isLoggedIn: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(keyIsLoggedIn, isLoggedIn)
        editor.apply()
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(keyIsLoggedIn, false)
    }

    fun setUserName(name:String) {
        val editor = sharedPreferences.edit()
        editor.putString(keyUserName, name)
        editor.apply()
    }
    fun gerUsername(): String? {
        return sharedPreferences.getString(keyUserName,"")
    }
}