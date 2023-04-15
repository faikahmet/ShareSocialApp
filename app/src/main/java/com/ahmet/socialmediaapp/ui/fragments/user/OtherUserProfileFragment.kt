package com.ahmet.socialmediaapp.ui.fragments.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import coil.load
import com.ahmet.socialmediaapp.viewmodel.MainViewModel
import com.ahmet.socialmediaapp.data.model.Follow
import com.ahmet.socialmediaapp.data.model.User
import com.example.socialmediaapp.databinding.FragmentOtherUserProfileBinding


class OtherUserProfileFragment : Fragment() {

    private lateinit var binding : FragmentOtherUserProfileBinding
    private val viewModel: MainViewModel by activityViewModels()
     var user :User? = null
    private var isFollow:Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentOtherUserProfileBinding.inflate(inflater,container,false)


        val args = arguments
        user  = args?.getParcelable("user")
        println("myBundle:$user")
        binding.otherUserProfileImageview.load(user?.userLogo)
        binding.otherUserProfileViewUsername.text = user?.userName.toString()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

       isCheckInfoTop()

        isCheckFollowThisUser()
        //PRogress must be start this section.

        binding.otherUserProfileViewUsernameFollowButton.setOnClickListener {
            followOrUnfollowUser()
        }

    }

    private fun isCheckInfoTop() {
        println("ischeckInfoTop started")
        viewModel.db?.let {
        viewModel.db!!.collection("users")
            .document(user!!.userId)
            .collection("follows")
            .get().addOnSuccessListener {
                binding.otherUserFollowingCount.text= it.documents.size.toString()
           }

        viewModel.db!!.collection("users")
            .document(user!!.userId)
            .collection("followers")
            .get().addOnSuccessListener {
                binding.otherUserFollowersCount.text =it.documents.size.toString()
            }

    }
    }

    private fun isCheckFollowThisUser() {
        viewModel.db?.let {
            viewModel.db!!.collection("users")
            .document(viewModel.currenctUser.uid)
            .collection("follows")
            .get()
            .addOnSuccessListener { qs->
                val documents = qs.documents
                if(documents.size<1){
                    isFollow= false
                    binding.otherUserProfileViewUsernameFollowButton.text = "Follow"
                }
                for (dc in documents){
                    val follow = Follow(
                        followId = dc["followId"] as String,
                        followDate = dc["followDate"] as String
                    )
                    println("isCheckFollowThisUser:$follow")
                    if(follow.followId == user?.userId){
                        //
                        binding.otherUserProfileViewUsernameFollowButton.text = "UnFollow"
                        isFollow = true
                        isCheckInfoTop()
                        return@addOnSuccessListener
                    }else{
                        isFollow = false
                        binding.otherUserProfileViewUsernameFollowButton.text = "Follow"
                    }
                }
            }
            }
    }

    private fun followOrUnfollowUser() {
        //user -> followed -> Follow data class
        val follow = Follow(
            followId = user?.userId?:"null",//takip etmek istediğin kişinin idsi
            followDate = "1"
        )
        if(isFollow){
            unFollow()
        }else{
            viewModel.db?.let {
            viewModel.db!!.collection("users")
                .document(viewModel.currenctUser.uid)
                .collection("follows")
                .add(follow).addOnSuccessListener {
                    println("başarıyla kayıt işlemi gerçekleşti")
                    binding.otherUserProfileViewUsernameFollowButton.text = "UnFollow"
                    isFollow = true
                    val follower = Follow(
                        followId = viewModel.currenctUser.uid,//takip etmek istediğin kişinin idsi
                        followDate = "1"
                    )
                    viewModel.db!!.collection("users")
                        .document(user!!.userId)
                        .collection("followers")
                        .add(follower).addOnSuccessListener {
                            println("başarıyla kayıt işlemi gerçekleşti")
                            isCheckInfoTop()
                        }
                }
                }

        }

    }

    private fun unFollow(){
        viewModel.db?.let {
            viewModel.db!!.collection("users")
            .document(viewModel.currenctUser.uid)
            .collection("follows")
            .get()
            .addOnSuccessListener { qs->
                val documents = qs.documents
                if(documents.size<1){
                    isFollow= false
                    binding.otherUserProfileViewUsernameFollowButton.text = "Follow"
                }
                for (dc in documents){
                    val follow = Follow(
                        followId = dc["followId"] as String,
                        followDate = dc["followDate"] as String
                    )
                    println("isCheckFollowThisUser:$follow")
                    if(follow.followId == user?.userId){
                        //
                        dc.reference.delete().addOnSuccessListener {
                            binding.otherUserProfileViewUsernameFollowButton.text = "Follow"
                            isFollow = false
                            isCheckInfoTop()
                        }
                        return@addOnSuccessListener
                    }
                }
            }

        viewModel.db!!.collection("users")
            .document(user!!.userId)
            .collection("followers")
            .get()
            .addOnSuccessListener { qs->
                val documents = qs.documents
                if (documents.size<1){
                    //
                }
                for (dc in documents){
                    val follow = Follow(
                        followId = dc["followId"] as String,
                        followDate = dc["followDate"] as String
                    )
                    println("isCheckFollowThisUser:$follow")
                    if(follow.followId == viewModel.currenctUser.uid){
                        //
                        dc.reference.delete().addOnSuccessListener {
                            isCheckInfoTop()
                        }
                        return@addOnSuccessListener
                    }
                }
            }
      }
    }

}