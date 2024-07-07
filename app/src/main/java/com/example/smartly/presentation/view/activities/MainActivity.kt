package com.example.smartly.presentation.view.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.smartly.R
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
        initialization(savedInstanceState)
        setContentView(binding.root)
    }

    private fun initialization(savedInstanceState: Bundle?) {
        if (sharedPreferencesHelper.isLoggedIn()) {
            if (intent != null && intent.hasExtra("fragment_to_open")) {
                val fragmentToOpen = intent.getStringExtra("fragment_to_open")
                if (fragmentToOpen == "ResultFragment") {

                    openFragment(ResultScreen())
                }
            }
            else{
                if (savedInstanceState == null) {
                    loadFragment(SignUpScreen())
                }
            }

        }
        else{
            loadFragment(SignUpScreen())
        }


    }

    private fun openFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun loadFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
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
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, SignUpScreen())
                    .commit()
            }
            R.id.btn_profile -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, ProfileScreen())
                    .commit()
            }
            R.id.btn_result -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, ResultScreen())
                    .commit()
            }
        }
        return true
    }

}