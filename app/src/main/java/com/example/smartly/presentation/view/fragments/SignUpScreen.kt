package com.example.smartly.presentation.view.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.example.smartly.R
import com.example.smartly.Util.SharedPreferencesHelper
import com.example.smartly.databinding.FragmentSignUpScreenBinding



class SignUpScreen : Fragment() {
    private var _binding: FragmentSignUpScreenBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private lateinit var selectedImagePath: String

    companion object {
        private const val REQUEST_PERMISSION_CODE = 123
        private const val REQUEST_IMAGE_CODE = 124
    }


    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                intent?.data?.let { selectedImageUri ->
                    binding.profilePic.setImageURI(selectedImageUri)
                    selectedImagePath = getPathFromUri(selectedImageUri)
                    saveImagePath(selectedImagePath)
                }
            }
        }
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
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, QuizSetupScreen())
            transaction.commit()

        }

    }

    private fun clickListener() {

        binding.profilePic.setOnClickListener {
            Toast.makeText(requireContext(),"click",Toast.LENGTH_SHORT).show()



            if (ContextCompat.checkSelfPermission(
                    requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                    requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                    requireActivity(), Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {

                openGallery()
            } else {
                if (Build.VERSION.SDK_INT >= 33) {
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA
                        ),
                        3
                    )
                } else {
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA
                        ),
                        3
                    )
                }
            }




        }


        binding.submitButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString()
            if (username.isEmpty()) {
                binding.usernameTextInputLayout.error = "Username cannot be empty"
            } else {
                binding.usernameTextInputLayout.error = null
                sharedPreferencesHelper.setLoggedIn(true)
                sharedPreferencesHelper.setUserName(username)
                Toast.makeText(requireContext(), "Hello, $username!", Toast.LENGTH_SHORT).show()
                val transaction = parentFragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_container, QuizSetupScreen())
                transaction.commit()
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

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            type = "image/*"  // Only allow images to be selected
        }
        resultLauncher.launch(intent)
    }

    private fun getPathFromUri(uri: Uri): String {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = requireContext().contentResolver.query(uri, projection, null, null, null)
        cursor?.let {
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            it.moveToFirst()
            val path = it.getString(columnIndex)
            it.close()
            return path
        }
        return ""
    }

    private fun saveImagePath(imagePath: String) {
        sharedPreferencesHelper.setUserProfilePic(imagePath)
    }
}






