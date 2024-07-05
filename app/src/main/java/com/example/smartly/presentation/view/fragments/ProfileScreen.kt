package com.example.smartly.presentation.view.fragments


import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartly.Util.SharedPreferencesHelper
import com.example.smartly.presentation.adapter.NotesAdapter
import com.example.smartly.NotesApplication
import com.example.smartly.databinding.FragmentProfileScreenBinding
import com.example.smartly.domain.model.NotesModelClass
import com.example.smartly.presentation.viewModel.NotesViewModel
import com.example.smartly.presentation.viewModel.NotesViewModelFactory


class ProfileScreen : Fragment() {
    private var _binding: FragmentProfileScreenBinding? = null
    private val binding get() = _binding!!
    lateinit var notesViewModel: NotesViewModel
    private lateinit var notesAdapter: NotesAdapter
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialization()
        clickListener()
    }
    private fun clickListener() {

        binding.buttonPost.setOnClickListener {
            postThought()
        }
        binding.editTextThought.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                true
            } else {
                false
            }
        }
    }

    private fun postThought() {
        val thought = binding.editTextThought.text.toString().trim()
        if (thought.isEmpty()) {
            Toast.makeText(activity, "Please enter your thought", Toast.LENGTH_SHORT).show()
        } else {
            binding.editTextThought.setText("")
            saveData(thought)
            Toast.makeText(activity, "Thought posted!", Toast.LENGTH_SHORT).show()

        }
    }

    private fun saveData(thought: String) {
        notesViewModel.insertNotes(NotesModelClass(thought))
    }


    private fun initialization() {
        val application = requireActivity().application as NotesApplication
        val viewModelFactory = NotesViewModelFactory(application.repository)
        notesViewModel = ViewModelProvider(this, viewModelFactory).get(NotesViewModel::class.java)
        notesAdapter = NotesAdapter(requireContext())
        sharedPreferencesHelper = SharedPreferencesHelper(requireContext())
        val userName=sharedPreferencesHelper.gerUsername()
        val imagePath = sharedPreferencesHelper.getUserProfilePic()
        if (userName != null) {
            if (!imagePath.isNullOrEmpty() && userName.isNotEmpty()) {
                // Load image into ImageView
                binding.userProfilePic.setImageURI(Uri.parse(imagePath))
            }
        }
        binding.txtUserName.text=userName

        binding.recyclerViewPost.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = notesAdapter
        }
        notesViewModel.allNotes.observe(viewLifecycleOwner) { notes ->
            notesAdapter.setNotes(notes)
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}