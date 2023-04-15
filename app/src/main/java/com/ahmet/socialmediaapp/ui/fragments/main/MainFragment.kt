package com.ahmet.socialmediaapp.ui.fragments.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ahmet.socialmediaapp.viewmodel.MainViewModel
import com.ahmet.socialmediaapp.data.adapters.PostAdapter
import com.ahmet.socialmediaapp.data.model.Post
import com.ahmet.socialmediaapp.data.model.PostLike
import com.ahmet.socialmediaapp.data.model.User
import com.example.socialmediaapp.R
import com.example.socialmediaapp.databinding.FragmentMainBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.util.*


class MainFragment : Fragment() {
  //  private val mAdapter  by lazy {PostAdapter()}
    private lateinit var mAdapter  : PostAdapter
    private lateinit var binding: FragmentMainBinding
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var layoutManager : LinearLayoutManager



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMainBinding.inflate(inflater,container,false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        Firebase.auth.currentUser?.let {
            viewModel.currenctUser = it
            mAdapter = PostAdapter (it.uid,onItemClicked = {clickedPost->
                println("59 mAdapter = PostAdapter")
                //clickuserProfile
                if (clickedPost.postUserId==viewModel.currenctUser.uid){
                    //The person of the uploaded person
                    val action = MainFragmentDirections.actionMainFragmentToUserProfileFragment()
                    findNavController().navigate(action)

                }
                else{
                    //Other person
                    readOtherUser(clickedPost.postUserId)
                }
            }, onLikeClicked = {likePost,imageview->
                likeOrUnlikePost(likePost,imageview)
            })
        }
        setupRecyclerView()

        if (viewModel.db==null){
            viewModel.db = FirebaseFirestore.getInstance()
            readUser()
            readDatabase()
            if(!permissionChecked()){
                requestPermission()
            }

            viewModel.user.observe(viewLifecycleOwner){user->
            }
            viewModel.postList.observe(viewLifecycleOwner){
                for (pst in it){
                }
                mAdapter.setData(it)
            }
        }else{
            println("null değil")
            readUser()
            adapterSetDataFun()
             if(viewModel.postList.value!=null){
                 if(viewModel.lastItemInRecycler>=viewModel.postList.value!!.size){
                     binding.mainFragmentRecyclerView.scrollToPosition(viewModel.lastItemInRecycler)
                 }
             }

        }
        binding.mainFragmentRecyclerView.addOnScrollListener(object:
            RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                viewModel.lastItemInRecycler = layoutManager.findFirstVisibleItemPosition()
            }
            })
    }
    private fun likePost(likedPost:Post,view:ImageView){
        val postLike = PostLike(
            postId = likedPost.postId,
            postLikeUserId = viewModel.user.value?.userId
        )
        println("add postlike userid:${postLike}")
        viewModel.db?.let {
            viewModel.db!!.collection("posts")
                .document("post")
                .collection("users")
                .document(likedPost.postId)
                .collection("postLike")
                .document(postLike.postLikeUserId!!)
                .set(postLike).addOnSuccessListener {
                    likedPost.postLike = postLike
                    viewModel.postList.value!!.toList().find { it.postId == likedPost.postId }?.postLike = postLike
                    view.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_favorite))
                }
        }

    }
    private fun likeOrUnlikePost(likedPost: Post,view:ImageView) {
        viewModel.db?.let {
        println("likeOrunlikePost:$likedPost")
        if(likedPost.postLike==null){
            likePost(likedPost,view)
        }else{
            if(viewModel.user.value?.userId == likedPost.postLike?.postLikeUserId){
                println("delete postlike userid:${likedPost.postLike?.postLikeUserId}")
                //delete postlike
                viewModel.db!!.collection("posts")
                    .document("post")
                    .collection("users")
                    .document(likedPost.postId)
                    .collection("postLike")
                    .document(likedPost.postLike!!.postLikeUserId!!).delete()
                    .addOnSuccessListener {
                        likedPost.postLike=null
                        viewModel.postList.value!!.toList().find { it.postId == likedPost.postId }?.postLike = likedPost.postLike
                       // mAdapter.setData( viewModel.postList.value!!)
                        view.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_favorite_no_border))
                        println("100.satır")
                    }.addOnFailureListener {
                        println("102.satır")
                    }.addOnCanceledListener {
                        println("104.satır")
                    }

            }else{
                likePost(likedPost,view)
            }
            }
        }

    }

    private fun readUser() {


            viewModel.db?.let {

            viewModel.db!!.collection("users")
                .document(viewModel.currenctUser.uid)
                //.whereEqualTo("capital", true)
                .get().addOnSuccessListener { ds->
                    viewModel.setUser(User(
                        userId = ds["userId"]as String,
                        userName = ds["userName"]as String,
                        userEmail = ds["userEmail"]as String,
                        userRegisterDate = ds.getDate("userRegisterDate"),
                        userLogo = ds["userLogo"]as String,
                    ))
                    println("viewmodeluser:${viewModel.user.value}")
                }
        }


    }

    private fun readOtherUser(uid:String) {
        viewModel.db?.let {
        viewModel.db!!.collection("users")
                .document(uid)
                //.whereEqualTo("capital", true)
                .get().addOnSuccessListener { ds->

                    val user_=  User(
                        userId = ds["userId"]as String,
                        userName = ds["userName"]as String,
                        userEmail = ds["userEmail"]as String,
                        userRegisterDate = ds.getDate("userRegisterDate"),
                        userLogo = ds["userLogo"]as String,
                    )
                    val action = MainFragmentDirections.actionMainFragmentToOtherUserProfileFragment(user_)
                    findNavController().navigate(action)
                }
                }
    }

    private fun setupRecyclerView() {
        binding.mainFragmentRecyclerView.adapter = mAdapter
        layoutManager = LinearLayoutManager(requireContext())
        binding.mainFragmentRecyclerView.layoutManager = layoutManager

    }


    private fun readDatabase() {
        //  db.collection("posts").document("post").collection("users").document(post.postUserId).collection(post.postId)
        viewModel.db?.let {
            viewModel.db!!.collection("posts")
                .document("post")
                .collection("users")
                //  .orderBy("postDate",Query.Direction.ASCENDING)
                //.whereEqualTo("capital", true)
                .get()
                .addOnSuccessListener {postDocuments->
                    viewModel.setPostList(arrayListOf())
                    val postDocumentSize = postDocuments.size()
                    var idPostCount = 0
                    for (document in postDocuments){
                        val post = Post(
                            postId =document["postId"] as String,
                            postContent = document["postContent"] as String,
                            postDate = document.getDate("postDate"),
                            //["postDate"] as Date?,
                            postPhoto = document["postPhoto"] as String,
                            postUserId = document["postUserId"] as String,
                            user = User(
                                userName = "",
                                userId = "",
                                userLogo = "",
                                userEmail = "",
                                userRegisterDate = Date(),
                            ),
                        )
                        viewModel.db!!.collection("users").document(post.postUserId).get().addOnSuccessListener { documentForUserName->
                            post.user?.let {
                                if (documentForUserName.exists()){
                                    it.userName = documentForUserName["userName"]as String
                                    it.userId = documentForUserName["userId"]as String
                                    it.userLogo = documentForUserName["userLogo"]as String
                                    it.userEmail = documentForUserName["userEmail"]as String
                                    it.userRegisterDate = documentForUserName.getDate("postDate")
                                }
                            }
                            println("post.user:${post.user}")

                            //PostlikeSectionStart

                            viewModel.db!!.collection("posts")
                                .document("post")
                                .collection("users")
                                .document(post.postId)
                                .collection("postLike")
                                //.whereEqualTo("capital", true)
                                .get().addOnSuccessListener { ds->
                                    println("263 ds start")
                                    val postLikeDocument = ds.size()
                                    post.postLikeSize = postLikeDocument
                                    println("265 postlikedocumentsize:${postLikeDocument}")
                                    var idLikeCount = 0
                                    if (postLikeDocument>0){
                                        for (postlikeItem in ds){
                                            val postLike = PostLike(
                                                postId = postlikeItem["postId"]as String,
                                                postLikeUserId = postlikeItem["postLikeUserId"]as String?,
                                                //postLikeId =ds["postLikeId"]as String
                                            )
                                            post.postLike = postLike
                                            if (postLike.postLikeUserId==viewModel.user.value?.userId){

                                            }

                                            viewModel.postList.value!!.add(post)
                                            idLikeCount++
                                            //PostLikeSectionEnd

                                            if (idLikeCount>=postLikeDocument){
                                                idPostCount++
                                                if (idPostCount>= postDocumentSize){
                                                    adapterSetDataFun()
                                                }
                                            }

                                        }
                                    }else{
                                        viewModel.postList.value!!.add(post)
                                        idPostCount++
                                        if (idPostCount>= postDocumentSize){
                                            adapterSetDataFun()
                                        }
                                    }


                                }
                        }
                    }
                    for (post_ in viewModel.postList.value!!){
                        println("post 74 : $post_")
                    }

                }.addOnFailureListener { e->
                    println("e:"+e.localizedMessage)
                }
        }
    }
    private fun adapterSetDataFun(){
        if(viewModel.postList.value !=null){
            mAdapter.setData(viewModel.postList.value!!)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_actionbar_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

            when(item.itemId){
                 R.id.main_actionbar_post_add -> {
                     val action = MainFragmentDirections.actionMainFragmentToSharePostFragment()
                     findNavController().navigate(action)
                 }
                R.id.main_actionbar_favorite -> {
                    Toast.makeText(context,"Favorite Click",Toast.LENGTH_SHORT).show()
                }
                R.id.main_actionbar_messages -> {
                    Toast.makeText(context,"Messages Click",Toast.LENGTH_SHORT).show()
                }
            }
        return super.onOptionsItemSelected(item)
    }





    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            2
        )
    }
    private fun permissionChecked():Boolean {

        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

    }

}