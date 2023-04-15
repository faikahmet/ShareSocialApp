package com.ahmet.socialmediaapp.ui.fragments.main

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.ahmet.socialmediaapp.viewmodel.MainViewModel
import com.ahmet.socialmediaapp.data.model.Post
import com.example.socialmediaapp.databinding.FragmentSharePostBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*


class SharePostFragment : Fragment() {

    private lateinit var binding: FragmentSharePostBinding
    private var choosenImage: Bitmap? = null

    private var storage = Firebase.storage
    private val viewModel: MainViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSharePostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(!permissionChecked()){
            requestPermission()
        }
        clickListeners()
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

    private fun clickListeners() {
        binding.apply {
            uploadShareImageview.setOnClickListener { chooseImage()   }
            uploadShareUploadbutton.setOnClickListener {  uploadPhoto()  }
        }
    }

    private fun getUserId():String?{
        return Firebase.auth.currentUser?.uid
    }

    private fun uploadPhoto() {
        //TODO CHECK EDITTEXT AND PHOTO BITMAP IS NULL OR EMPTY ETC.
        choosenImage?.let {choosenImage->
            getUserId()?.let { user_id->
            /**
             * NewVersion. This was for firebaseversion
             */

            val storageRef = storage.reference
                val stringUrlBase = "images/"
                val stringUrl = stringUrlBase+"/"+user_id+"/"+FieldValue.serverTimestamp()+"/"+UUID.randomUUID()
                val mountainsRef = storageRef.child(stringUrl)
                val mountainImagesRef = storageRef.child("images/mountains.jpg")
                // Get the data from an ImageView as bytes
                binding.uploadShareImageview.isDrawingCacheEnabled = true
                binding.uploadShareImageview.buildDrawingCache()
                val bitmap = (binding.uploadShareImageview.drawable as BitmapDrawable).bitmap
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()

                var uploadTask = mountainsRef.putBytes(data)

              /*  val urlTask = uploadTask.continueWith { task->
                    if(!task.isSuccessful){
                        task.exception?.let {
                            throw it
                        }
                    }
                    println("mountainsRef.downloadUrl:${mountainsRef.downloadUrl}")
                }*/
                uploadTask.addOnFailureListener {
                    // Handle unsuccessful uploads
                }.addOnSuccessListener { taskSnapshot ->
                    mountainsRef.downloadUrl.addOnSuccessListener {uri->
                        println("url :${uri}")
               val db : FirebaseFirestore = FirebaseFirestore.getInstance()
                   val post = Post(
                       postId= UUID.randomUUID().toString(),
                       postContent=binding.uploadSharePostText.text.toString(),
                     //  postPhoto="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRnxeDb0QORflc3unJHI20YliSmlFnN6_WGDLAQfvoyLKIh6Thk_lLmf0Cw1wqKx3zlIOE&usqp=CAU",
                       postPhoto=uri.toString(),
                       postDate= FieldValue.serverTimestamp(),
                       postUserId = user_id
                   )


                   val postData =
                       HashMap<String, Any>()
                   postData["postId"] = post.postId
                   postData["postContent"] = post.postContent
                   postData["postPhoto"] = post.postPhoto
                   postData["postDate"] = post.postDate?:0
                   postData["postUserId"] = post.postUserId

                   db.collection("posts").document("post").collection("users")/*.document(post.postUserId)*/.document(post.postId)
                       .set(postData).addOnSuccessListener {dr->
                           viewModel.postList
                           Toast.makeText(requireContext(), "Post Added succesfully..",
                               Toast.LENGTH_SHORT).show()
                           toMainFragmentWithBackPress()
                       }.addOnFailureListener {
                           //Todo delete this user
                       }

                        // taskSnapshot.metadata contains file metadata such as size, content-type, etc.

                    }
                    //println("storageRef.downloadUrl:${storageRef.downloadUrl}")


                }


                //uploaded section



            /**
             * OldVersion. this was made for parse
             *   val byteArrayOutputStream = ByteArrayOutputStream()
            choosenImage.compress(Bitmap.CompressFormat.PNG, 50, byteArrayOutputStream)
            val bytes = byteArrayOutputStream.toByteArray()
            /*   val parseFile = ParseFile("image.png", bytes)
            //Fotoğrafı parse ye byte çevirip attık

            //Fotoğrafı parse ye byte çevirip attık
            val `object` = ParseObject("Posts")
            `object`.put("image", parseFile)
            `object`.put("comment", comment)
            `object`.put("username", ParseUser.getCurrentUser().getUsername())
            `object`.saveInBackground()*/
             */
        }
        }
    }

    private fun toMainFragmentWithBackPress() {
        viewModel.db=null
        requireActivity().onBackPressed()
    }

    fun chooseImage() {
        if(permissionChecked()){
            println("86.satır")
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI) //Kullanıcı galerisinden foto seçebilmesini sağlar
            startActivityForResult(intent, 1)
        }else{
            println("90.satır")
            requestPermission()
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode == 2) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, 1)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

     override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            val uri = data.data
            try {
                choosenImage = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
                binding.uploadShareImageview.setImageBitmap(choosenImage)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}