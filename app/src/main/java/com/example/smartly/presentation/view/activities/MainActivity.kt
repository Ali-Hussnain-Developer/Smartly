package com.example.smartly.presentation.view.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.smartly.R
import com.example.smartly.Util.FragmentTransactionClass
import com.example.smartly.Util.SharedPreferencesHelper
import com.example.smartly.databinding.ActivityMainBinding
import com.example.smartly.presentation.view.fragments.ProfileScreen
import com.example.smartly.presentation.view.fragments.ResultScreen
import com.example.smartly.presentation.view.fragments.SignUpScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        initialization()
        setContentView(binding.root)
    }

    private fun initialization() {
        if (sharedPreferencesHelper.isLoggedIn()) {
            handleDeepLink()
        }
        else{
            FragmentTransactionClass.fragmentTransaction(supportFragmentManager,SignUpScreen())
        }


    }

    private fun handleDeepLink() {
        if (intent != null && intent.hasExtra("fragment_to_open")) {
            val fragmentToOpen = intent.getStringExtra("fragment_to_open")
            if (fragmentToOpen == "ResultFragment") {
                FragmentTransactionClass.fragmentTransaction(supportFragmentManager,ResultScreen())
            }
        }
        else{
                FragmentTransactionClass.fragmentTransaction(supportFragmentManager,SignUpScreen())

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.btn_logout -> {
                sharedPreferencesHelper.setLoggedIn(false)
                sharedPreferencesHelper.setUserProfilePic("")
                sharedPreferencesHelper.setUserName("")
                FragmentTransactionClass.fragmentTransaction(supportFragmentManager,SignUpScreen())
            }
            R.id.btn_profile -> {
                FragmentTransactionClass.fragmentTransaction(supportFragmentManager,ProfileScreen())
            }
            R.id.btn_result -> {
                FragmentTransactionClass.fragmentTransaction(supportFragmentManager,ResultScreen())
            }
        }
        return true
    }

}