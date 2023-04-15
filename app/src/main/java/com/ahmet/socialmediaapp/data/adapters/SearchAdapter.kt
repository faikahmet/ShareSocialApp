package com.ahmet.socialmediaapp.data.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.ahmet.socialmediaapp.data.model.User
import com.example.socialmediaapp.R
import com.example.socialmediaapp.databinding.SearchRowLayoutBinding

class SearchAdapter(private val onItemClicked:(user:User)->Unit): RecyclerView.Adapter<SearchAdapter.MyViewHolder>(){
    private var userList = emptyList<User>()

    class MyViewHolder(private val binding: SearchRowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root){
            fun bind(user: User) {
                println("username:"+user.userName+" userlogo:"+user.userLogo)
            binding.searchUsernameText.text=user.userName
            binding.imageViewSearchProfile.load(user.userLogo)
            }
        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = SearchRowLayoutBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return SearchAdapter.MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentPost = userList[position]
        holder.bind(currentPost)
        println("currentPost:"+currentPost+" currentPost.userName"+currentPost.userName+" userlogo:"+currentPost.userLogo)
        holder.itemView.setOnClickListener {
            onItemClicked(currentPost)
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    fun setData(newPostList: List<User>) {
        val favoriteRecipesDiffUtil =
            RecipesDiffUtil(userList, newPostList)
        val diffUtilResult = DiffUtil.calculateDiff(favoriteRecipesDiffUtil)
        userList = newPostList
        diffUtilResult.dispatchUpdatesTo(this)
    }

}

