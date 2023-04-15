package com.ahmet.socialmediaapp.ui.fragments.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.ahmet.socialmediaapp.viewmodel.MainViewModel
import com.example.socialmediaapp.R
import com.example.socialmediaapp.databinding.FragmentUserProfileBinding


class UserProfileFragment : Fragment() {

    private lateinit var binding : FragmentUserProfileBinding

    private val viewModel : MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUserProfileBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fillFields()
        isCheckInfoTop()

        binding.userProfileEditProfileButton.setOnClickListener {
            val action = UserProfileFragmentDirections.actionUserProfileFragmentToEditProfileFragment()
            findNavController().navigate(action)
        }

        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // Do custom work here
                    // if you want onBackPressed() to be called as normal afterwards
                    if (isEnabled) {
                        isEnabled = false
                        requireActivity().onBackPressed()
                    }
                }
            }
            )

    }

    private fun isCheckInfoTop() {
        println("ischeckInfoTop started")
        viewModel.db?.let {
            viewModel.db!!.collection("users")
                .document(viewModel.currenctUser.uid)
                .collection("follows")
                .get().addOnSuccessListener {
                    binding.userProfileFollowingCount.text= it.documents.size.toString()
                }

            viewModel.db!!.collection("users")
                .document(viewModel.currenctUser.uid)
                .collection("followers")
                .get().addOnSuccessListener {
                    binding.userProfileFollowerCount.text =it.documents.size.toString()
                }
        }

    }

    private fun fillFields() {
        val user_ = viewModel.user.value
        user_?.let {user->
            binding.userProfileUserName.text = user.userName
            binding.postMainRowProfilePhoto.load(user.userLogo){
                crossfade(600)
                error(R.drawable.avatarai)
            }
        }
    }

}