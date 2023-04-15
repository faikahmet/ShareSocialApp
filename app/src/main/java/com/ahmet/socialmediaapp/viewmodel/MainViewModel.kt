package com.ahmet.socialmediaapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ahmet.socialmediaapp.data.model.Post
import com.ahmet.socialmediaapp.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class MainViewModel : ViewModel() {

   // val user : MutableLiveData<User> = MutableLiveData()
    /*val user: MutableLiveData<User> by lazy {
        MutableLiveData<User>()
    }*/
    var db : FirebaseFirestore?=null
   lateinit var currenctUser: FirebaseUser

    private val user_ = MutableLiveData<User>()
    val user: LiveData<User> = user_

    private  val postList_=MutableLiveData<ArrayList<Post>>()
    val postList: LiveData<ArrayList<Post>> = postList_

    var lastItemInRecycler = 0


    fun setUser(newUser: User) {
        user_.value = newUser
    }

    fun setPostList(postList: ArrayList<Post>) {
        postList_.value = postList
    }
}