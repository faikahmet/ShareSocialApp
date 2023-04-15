package com.ahmet.socialmediaapp.data.adapters

import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.view.ActionMode
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.ahmet.socialmediaapp.data.model.Post
import com.example.socialmediaapp.R
import com.example.socialmediaapp.databinding.PostMainRowLayoutBinding
import java.util.*


class PostAdapter(private val currentId:String, private val onItemClicked:(post:Post)->Unit,private val onLikeClicked:(post:Post,view:ImageView)->Unit): RecyclerView.Adapter<PostAdapter.MyViewHolder>() {

    private var postList = emptyList<Post>()

    class MyViewHolder(private val binding:PostMainRowLayoutBinding):
    RecyclerView.ViewHolder(binding.root){


        private fun howToPastUploadedTime(date: Date):String{
            val now = Calendar.getInstance()
            val start = Calendar.getInstance()
            start.time = date
            val milliseconds1 = start.timeInMillis
            val milliseconds2 = now.timeInMillis
            val diff = milliseconds2 - milliseconds1
            val diffSeconds = diff / 1000
            val diffMinutes = diff / (60 * 1000)
            val diffHours = diff / (60 * 60 * 1000)
            val diffDays = diff / (24 * 60 * 60 * 1000)
            if(diffDays>0){
                return "$diffDays day ago."
            }
            if(diffHours>0){
                return "$diffHours hours ago."
            }
            if (diffMinutes>0){
                return "$diffMinutes minutes ago."
            }
            if (diffSeconds>0){
                return "$diffSeconds seconds ago."
            }
            println("\nThe Date Different Example");
            println("Time in milliseconds: " + diff
                    + " milliseconds.");
            println("Time in seconds: " + diffSeconds
                    + " seconds.");
            println("Time in minutes: " + diffMinutes
                    + " minutes.");
            println("Time in hours: " + diffHours
                    + " hours.");
            println("Time in days: " + diffDays
                    + " days.");
            return "Just now posted."


        }

        fun test(DEFINITION:Int){
            binding.postMainRowPostUsernameAndComment.customSelectionActionModeCallback =
                object : android.view.ActionMode.Callback {

                    override fun onCreateActionMode(
                        p0: android.view.ActionMode?,
                        menu: Menu?
                    ): Boolean {
                        menu?.add(0, DEFINITION, 0, "Definition")?.setIcon(com.example.socialmediaapp.R.drawable.ic_home)
                        return true
                    }

                    override fun onPrepareActionMode(
                        p0: android.view.ActionMode?,
                        menu: Menu?
                    ): Boolean {
                        // Remove the "select all" option
                        menu?.removeItem(android.R.id.selectAll)
                        // Remove the "cut" option
                        menu?.removeItem(android.R.id.cut)
                        // Remove the "copy all" option
                        menu?.removeItem(android.R.id.copy)
                        return true
                    }

                    override fun onActionItemClicked(
                        mode: android.view.ActionMode?,
                        item: MenuItem?
                    ): Boolean {
                        if (item != null) {
                            when (item.getItemId()) {
                                DEFINITION -> {
                                    var min = 0
                                    var max: Int = binding.postMainRowPostUsernameAndComment.getText().length
                                    if (binding.postMainRowPostUsernameAndComment.isFocused()) {
                                        val selStart: Int = binding.postMainRowPostUsernameAndComment.getSelectionStart()
                                        val selEnd: Int = binding.postMainRowPostUsernameAndComment.getSelectionEnd()
                                        min = Math.max(0, Math.min(selStart, selEnd))
                                        max = Math.max(0, Math.max(selStart, selEnd))
                                    }
                                    // Perform your definition lookup with the selected text
                                    val selectedText: CharSequence =
                                        binding.postMainRowPostUsernameAndComment.getText().subSequence(min, max)
                                    // Finish and close the ActionMode
                                    mode?.finish()
                                    return true
                                }
                                else -> {}
                            }
                        }
                        return false
                    }

                    override fun onDestroyActionMode(p0: android.view.ActionMode?) {
                        println("132")
                        // Called when an action mode is about to be exited and
                        // destroyed
                    }
                }
        }


        fun bind(post: Post) {
         binding.postMainRowPostUsernameAndComment.text = "I can select a word in sentence.."
            test(1)


         binding.postMainRowUserNameText.text = "${post.user?.userName}"

            binding.postMainRowPostTime.text = howToPastUploadedTime(post.postDate as Date)



            //binding.postMainRowPostTime.text = post.postDate.toString()//Todo from firebasevalue to date
            println("user?.userName:"+post.user?.userName+" userlogo:"+post.user?.userLogo)
            binding.postMainRowProfilePhoto.load(post.user?.userLogo){
             crossfade(600)
             error(R.drawable.avatarai)
         }
         binding.postMainRowPostImageview.load(post.postPhoto){
             crossfade(600)
             error(R.drawable.avatarai)
         }
            post.postLikeSize?.let {
                binding.postMainRowPostLikescount.text = ""
                if(it>0){
                    val allLikeSize = post.postLikeSize.toString()+" " + "Likes"
                    binding.postMainRowPostLikescount.text = allLikeSize
                }
            }



        }

        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = PostMainRowLayoutBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentPost = postList[position]
        holder.bind(currentPost)
        holder.itemView.findViewById<TextView>(R.id.post_main_row_userNameText).setOnClickListener {
            onItemClicked(currentPost)
        }
        holder.itemView.findViewById<ImageView>(R.id.post_main_row_profile_photo).setOnClickListener {
            onItemClicked(currentPost)
        }

        if (currentId == currentPost.postLike?.postLikeUserId){
            holder.itemView.findViewById<ImageView>(R.id.post_main_row_post_like_button).setImageDrawable((AppCompatResources.getDrawable(holder.itemView.context, R.drawable.ic_favorite)))
        }else{
            holder.itemView.findViewById<ImageView>(R.id.post_main_row_post_like_button).setImageDrawable((AppCompatResources.getDrawable(holder.itemView.context, R.drawable.ic_favorite_no_border)))
        }

        holder.itemView.findViewById<ImageView>(R.id.post_main_row_post_like_button).setOnClickListener {viewImage->
            onLikeClicked(currentPost,holder.itemView.findViewById<ImageView>(R.id.post_main_row_post_like_button))
        }
    }


    override fun getItemCount(): Int {
        return postList.size
    }

    fun setData(newPostList: List<Post>) {
        val favoriteRecipesDiffUtil =
            RecipesDiffUtil(postList, newPostList)
        val diffUtilResult = DiffUtil.calculateDiff(favoriteRecipesDiffUtil)
        postList = newPostList
        println("adapter setData postlist:$postList")
        postList = postList.sortedByDescending { it.postDate.toString() }
        diffUtilResult.dispatchUpdatesTo(this)
    }

}


class RecipesDiffUtil<T> (
    private val oldList:List<T>,
    private val newList:List<T>
): DiffUtil.Callback(){
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] === newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

}