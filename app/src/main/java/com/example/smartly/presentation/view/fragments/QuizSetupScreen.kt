package com.example.smartly.presentation.view.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.smartly.Util.FragmentTransactionClass
import com.example.smartly.Util.SharedPreferencesHelper
import com.example.smartly.Util.ToastHandler
import com.example.smartly.databinding.FragmentQuizSetupScreenBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class QuizSetupScreen : Fragment() {
    private var _binding: FragmentQuizSetupScreenBinding? = null
    private val binding get() = _binding!!
    private var selectedCategory: String? = null
    private var selectedDifficulty: String? = null
    private var selectedQuestionType: String? = null
    private var categoryId: Int? = 0
    @Inject
    lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                ToastHandler.showToast(requireContext(),"Permission Granted")
            } else {
                ToastHandler.showToast(requireContext(),"Permission not Granted")
            }
        }
    private val categories = listOf(
        "General Knowledge",
        "Entertainment: Books",
        "Entertainment: Film",
        "Entertainment: Music",
        "Entertainment: Musicals & Theatres",
        "Entertainment: Television",
        "Entertainment: Video Games",
        "Entertainment: Board Games",
        "Science & Nature",
        "Science: Computers",
        "Science: Mathematics",
        "Mythology",
    )
    private val difficulties = listOf(
        "Easy",
        "Medium",
        "Hard"
    )
    private val questionTypes = listOf(
        "Multiple Choice",
        "True/False"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizSetupScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialization()

    }

    private fun initialization() {
        askUserNotificationPermission(requireContext())
        clickListener()
        setupSpinner(binding.spinnerCategories, categories) { selectedCategory = it }
        setupSpinner(binding.spinnerDifficulty, difficulties) { selectedDifficulty = it }
        setupSpinner(binding.spinnerQuestionType, questionTypes) { selectedQuestionType = it }
    }

    private fun clickListener() {
        binding.buttonNext.setOnClickListener {
            if (selectedCategory != null && selectedDifficulty != null && selectedQuestionType != null) {
                val bundle = Bundle()
                categoryId?.let { it1 -> bundle.putInt("categoryId", it1) }
                bundle.putString("selectedDifficulty", "$selectedDifficulty")
                bundle.putString("selectedQuestionType", "$selectedQuestionType")
                val quizFragment: Fragment = QuizScreen()
                quizFragment.arguments = bundle
                FragmentTransactionClass.fragmentTransaction(parentFragmentManager,quizFragment)
                sharedPreferencesHelper.setUserCategory(selectedCategory.toString())

            } else {
                ToastHandler.showToast(requireContext(),"Please select all options")
            }
        }

    }



    private fun askUserNotificationPermission(requireContext: Context) {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(
                    requireContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) ==
                PackageManager.PERMISSION_GRANTED
            ) {

            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

    }

    private fun setupSpinner(spinner: Spinner, items: List<String>, onItemSelected: (String) -> Unit) {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                categoryId = position
                val selectedItem = parent.getItemAtPosition(position) as String
                onItemSelected(selectedItem)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
