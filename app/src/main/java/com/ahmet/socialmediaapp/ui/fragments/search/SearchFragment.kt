package com.ahmet.socialmediaapp.ui.fragments.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahmet.socialmediaapp.data.adapters.SearchAdapter
import com.ahmet.socialmediaapp.data.model.User
import com.example.socialmediaapp.databinding.FragmentSearchBinding
import com.google.firebase.firestore.FirebaseFirestore

class SearchFragment : Fragment() {
   // private val mAdapter by lazy { SearchAdapter() }
    private lateinit var mAdapter : SearchAdapter
    private lateinit var binding: FragmentSearchBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAdapter = SearchAdapter {user->
            println("us us:"+ user.userName)
            Toast.makeText(context,"view search clicked", Toast.LENGTH_SHORT).show()
        }
        setupRecyclerView()

        binding.searchFragmentSearchtext.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                readDatabase(p0.toString())
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
    }

    private fun readDatabase(query: String) {
        if(query.isEmpty()){
            mAdapter.setData(arrayListOf())
            return
        }
        println("readDatabase")
        //  db.collection("posts").document("post").collection("users").document(post.postUserId).collection(post.postId)

        var db: FirebaseFirestore = FirebaseFirestore.getInstance()
        println("36")
        db.collection("users")
            //.whereEqualTo("capital", true)
            .get()
            .addOnSuccessListener { documents ->
                val userList: ArrayList<User> = arrayListOf()
                for (dd in documents) {
                    println("dc:$dd")
                    val userName = dd["userName"] as String
                    println("dc :$userName")
                    if (userName.contains(query)) {
                        //TODO BURADAN USER IDYI BIR SEKILDE AL VE BIR FIREBASE DAHA YURUT ONUNLA DA ALTTAKI YORUM SATIRI HALDEKI ADAPTERI GEÇIR
                        val user = User(
                            userId = dd["userId"] as String,
                            userName = dd["userName"] as String,
                            userEmail = dd["userEmail"] as String,
                            userRegisterDate = dd.getDate("userRegisterDate"),
                            userLogo = dd["userLogo"] as String,
                        )
                        userList.add(user)
                    }
                }
                mAdapter.setData(userList)
            }.addOnFailureListener {
                println("failure")
            }
    }

    private fun setupRecyclerView() {
        binding.searchRecyclerView.adapter = mAdapter
        binding.searchRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

}