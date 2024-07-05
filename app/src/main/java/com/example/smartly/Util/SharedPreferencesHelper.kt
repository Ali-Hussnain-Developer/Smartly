package com.example.smartly.Util

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesHelper(context: Context) {

    private val prefsName = "login_prefs"
    private val keyIsLoggedIn = "is_logged_in"
    private val keyUserName = "user_name"
    private val keyUserCategory = "user_category"
    private val keyUserTotalScore = "user_total_score"
    private val keyUserImagePath = "user_image_path"
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

    fun setUserCategory(category:String) {
        val editor = sharedPreferences.edit()
        editor.putString(keyUserCategory, category)
        editor.apply()
    }
    fun getUserCategory(): String? {
        return sharedPreferences.getString(keyUserCategory,"")
    }

    fun setUserTotalScore(marks:String) {
        val editor = sharedPreferences.edit()
        editor.putString(keyUserTotalScore, marks)
        editor.apply()
    }
    fun getUserTotalScore(): String? {
        return sharedPreferences.getString(keyUserTotalScore,"")
    }
    fun setUserProfilePic(imagePath:String) {
        val editor = sharedPreferences.edit()
        editor.putString(keyUserImagePath, imagePath)
        editor.apply()
    }
    fun getUserProfilePic(): String? {
        return sharedPreferences.getString(keyUserImagePath,"")
    }
}