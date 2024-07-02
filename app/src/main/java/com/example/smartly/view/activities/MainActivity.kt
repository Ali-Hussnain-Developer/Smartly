package com.example.smartly.view.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.smartly.R
import com.example.smartly.databinding.ActivityMainBinding
import com.example.smartly.view.fragments.ResultScreen
import com.example.smartly.view.fragments.SignUpScreen

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (savedInstanceState == null) {
            loadFragment(SignUpScreen())
        }

        if (intent != null && intent.hasExtra("fragment_to_open")) {
            val fragmentToOpen = intent.getStringExtra("fragment_to_open")
            if (fragmentToOpen == "ResultFragment") {
                openFragment(ResultScreen())
            }
        }

    }
    private fun openFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun loadFragment(fragment: Fragment) {
        // Create a new FragmentManager
        val fragmentManager: FragmentManager = supportFragmentManager

        // Begin a new fragment transaction
        val fragmentTransaction = fragmentManager.beginTransaction()

        // Replace the contents of the container with the new fragment
        fragmentTransaction.replace(R.id.fragment_container, fragment)

        // Complete the changes added above
        fragmentTransaction.commit()
    }
}