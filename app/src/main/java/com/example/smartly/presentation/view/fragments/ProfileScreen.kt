package com.example.smartly.presentation.view.fragments


import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.smartly.R
import com.example.smartly.Util.SharedPreferencesHelper
import com.example.smartly.databinding.FragmentProfileScreenBinding
import com.example.smartly.domain.model.NotesModelClass
import com.example.smartly.presentation.adapter.NotesAdapter
import com.example.smartly.presentation.viewModel.NotesViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class ProfileScreen : Fragment() {
    private var _binding: FragmentProfileScreenBinding? = null
    private val binding get() = _binding!!
    val notesViewModel: NotesViewModel by viewModels()
    private lateinit var notesAdapter: NotesAdapter

    @Inject
    lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
            actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT
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
        notesAdapter = NotesAdapter(requireContext())
        val userName = sharedPreferencesHelper.getUserName()

        if (userName != null) {
            binding.txtUserName.text = userName
        }

        val imagePath = sharedPreferencesHelper.getUserProfilePic()
        if (!imagePath.isNullOrEmpty()) {
            val file = File(imagePath)
            if (file.exists()) {
                Glide.with(this)
                    .load(Uri.fromFile(file))
                    .placeholder(R.drawable.person_avatar)
                    .error(R.drawable.person_avatar)
                    .into(binding.userProfilePic)
            } else {
                binding.userProfilePic.setImageResource(R.drawable.person_avatar)
            }
        } else {
            binding.userProfilePic.setImageResource(R.drawable.person_avatar)
        }

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
