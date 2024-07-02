package com.example.smartly.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.smartly.R
import com.example.smartly.databinding.FragmentSignUpScreenBinding


class SignUpScreen : Fragment() {
    private var _binding: FragmentSignUpScreenBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignUpScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.submitButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString()
            if (username.isEmpty()) {
                binding.usernameTextInputLayout.error = "Username cannot be empty"
            } else {
                binding.usernameTextInputLayout.error = null
                Toast.makeText(requireContext(), "Hello, $username!", Toast.LENGTH_SHORT).show()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, QuizSetupScreen())
                    .addToBackStack(null)
                    .commit()
            }
        }
    }
}