package com.example.smartly.presentation.view.fragments


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.smartly.BR
import com.example.smartly.R
import com.example.smartly.Util.SharedPreferencesHelper
import com.example.smartly.Util.ToastHandler
import com.example.smartly.databinding.FragmentProfileScreenBinding
import com.example.smartly.databinding.SingleItemViewBinding
import com.example.smartly.domain.model.NotesModelClass
import com.example.smartly.presentation.adapter.GenericRecyclerViewAdapter
import com.example.smartly.presentation.viewModel.ViewModels
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class ProfileScreen : Fragment() {
    val viewModel: ViewModels by viewModels()
    private lateinit var binding: FragmentProfileScreenBinding
    private lateinit var adapter: GenericRecyclerViewAdapter<NotesModelClass, SingleItemViewBinding>

    @Inject
    lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile_screen, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialization()

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
            ToastHandler.showToast(requireContext(),"Please enter your thought")
        } else {
            binding.editTextThought.setText("")
            saveData(thought)
            ToastHandler.showToast(requireContext(),"Thought posted!")
        }
    }

    private fun saveData(thought: String) {
        viewModel.insertNotes(NotesModelClass(thought))
    }

    private fun initialization() {
        clickListener()
        setUserName()
        setProfilePicture()
        setRecyclerView()
    }

    private fun setRecyclerView() {
        viewModel.allNotes.observe(viewLifecycleOwner) { notes ->
            adapter.setItems(notes)
        }
        binding.recyclerViewPost.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewPost.setHasFixedSize(true)
        adapter = GenericRecyclerViewAdapter(
            requireContext(),
            emptyList(),
            R.layout.single_item_view,
            BR.note
        ) { note ->
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, note.thoughts)
                type = "text/plain"
            }
            startActivity(Intent.createChooser(shareIntent, "Share your thought via"))
        }

        binding.recyclerViewPost.adapter = adapter
    }


    private fun setProfilePicture() {
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

    }

    private fun setUserName() {
        val userName = sharedPreferencesHelper.getUserName()
        if (userName != null) {
            binding.txtUserName.text = userName
        }

    }

}
