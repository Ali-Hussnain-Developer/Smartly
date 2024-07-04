package com.example.smartly.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smartly.R
import com.example.smartly.Util.SharedPreferencesHelper
import com.example.smartly.databinding.FragmentSignUpScreenBinding


class SignUpScreen : Fragment() {
    private var _binding: FragmentSignUpScreenBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignUpScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialization()
        clickListener()
    }

    private fun initialization() {
        sharedPreferencesHelper = SharedPreferencesHelper(requireContext())
        if (sharedPreferencesHelper.isLoggedIn()) {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, QuizSetupScreen())
                .addToBackStack(null)
                .commit()
        }

    }

    private fun clickListener() {

        binding.submitButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString()
            if (username.isEmpty()) {
                binding.usernameTextInputLayout.error = "Username cannot be empty"
            } else {
                binding.usernameTextInputLayout.error = null
                sharedPreferencesHelper.setLoggedIn(true)
                sharedPreferencesHelper.setUserName(username)
                Toast.makeText(requireContext(), "Hello, $username!", Toast.LENGTH_SHORT).show()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, QuizSetupScreen())
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    override  fun onResume() {
        super.onResume()
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
    }

    override fun onPause() {
        super.onPause()
        (activity as? AppCompatActivity)?.supportActionBar?.show()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}