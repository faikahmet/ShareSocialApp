package com.ahmet.socialmediaapp.ui.fragments.user

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import coil.load
import com.ahmet.socialmediaapp.viewmodel.MainViewModel
import com.example.socialmediaapp.R
import com.example.socialmediaapp.databinding.FragmentEditProfileBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*


class EditProfileFragment : Fragment() {

    private lateinit var binding : FragmentEditProfileBinding
    private var choosenImage: Bitmap? = null
    private var storage = Firebase.storage
    var user_id ="0"

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEditProfileBinding.inflate(inflater,container,false)

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Firebase.auth.currentUser?.let {user_->
            user_id = user_.uid
        }
        fillFields()
        viewModel.user.observe(viewLifecycleOwner){user->
            println("55.satır:${viewModel.user.value}")
        }
        (requireActivity() as AppCompatActivity).supportActionBar?.let {actionBarsupport->
            val actionBar: ActionBar =actionBarsupport

            // providing title for the ActionBar
            actionBar.title = "Edit Profile"
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_cancel)

            // providing subtitle for the ActionBar

            // providing subtitle for the ActionBar
           // actionBar.subtitle = ""

            // adding icon in the ActionBar

           // // adding icon in the ActionBar
           // actionBar.setIcon(R.drawable.ic_cancel)

            // methods to display the icon in the ActionBar

            // methods to display the icon in the ActionBar
            //actionBar.setDisplayUseLogoEnabled(true)
           // actionBar.setDisplayShowHomeEnabled(true)
        }
        // providing title for the ActionBar

        if(!permissionChecked()){
            requestPermission()
        }
    binding.editUserProfileProfilePhoto.setOnClickListener { chooseImage()  }


    }

    private fun fillFields() {
        binding.edtProfileUsernameEdittext.setText(viewModel.user.value?.userName ?: "")
        binding.editUserProfileProfilePhoto.load (viewModel.user.value?.userLogo ?: ""){
            crossfade(600)
            error(R.drawable.avatarai)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.edit_profile_actionbar_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.edit_profile_actionbar_okay->{
                if (!uploadPhoto())
                {
                    uploadName()
                }

            }
            android.R.id.home->{
                requireActivity().onBackPressed()
            }
        }
        return true
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


    fun chooseImage() {
        if(permissionChecked()){
            println("86.satır")
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
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
                binding.editUserProfileProfilePhoto.setImageBitmap(choosenImage)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }



    @SuppressLint("SuspiciousIndentation")
    private fun uploadPhoto():Boolean {
        println("uploadPhoto 182.satır")
        //TODO CHECK EDITTEXT AND PHOTO BITMAP IS NULL OR EMPTY ETC.
        var chDurum = false

        choosenImage?.let {choosenImage->
                /**
                 * NewVersion. This was for firebaseversion
                 */
            println("uploadPhoto 188.satır")
            chDurum = true
            val storageRef = storage.reference
                val stringUrlBase = "images/"
                val stringUrl = stringUrlBase+"/"+user_id+"/"+ FieldValue.serverTimestamp()+"/"+ UUID.randomUUID()
                val mountainsRef = storageRef.child(stringUrl)
                val mountainImagesRef = storageRef.child("images/mountains.jpg")
                // Get the data from an ImageView as bytes
                binding.editUserProfileProfilePhoto.isDrawingCacheEnabled = true
                binding.editUserProfileProfilePhoto.buildDrawingCache()
                val bitmap = (binding.editUserProfileProfilePhoto.drawable as BitmapDrawable).bitmap
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()

                var uploadTask = mountainsRef.putBytes(data)

                   uploadTask.continueWith { task->
                      if(!task.isSuccessful){
                          task.exception?.let {
                              throw it
                          }
                      }
                      println("mountainsRef.downloadUrl:${mountainsRef.downloadUrl}")
                  }
                uploadTask.addOnFailureListener {
                    // Handle unsuccessful uploads
                }.addOnSuccessListener { taskSnapshot ->
                    mountainsRef.downloadUrl.addOnSuccessListener {uri->
                        println("url :${uri}")
                        val db : FirebaseFirestore = FirebaseFirestore.getInstance()
                    println("viewModel.user().value:${viewModel.user.value}")
                        viewModel.user.value?.let {user->
                         user.userLogo =uri.toString()
                            user.userName =binding.edtProfileUsernameEdittext.text.toString()
                            db.collection("users").document(user_id)/*.document(post.postUserId)*/
                                .set(user).addOnSuccessListener { dr->
                                    Toast.makeText(requireContext(), "Profile change succesfully..",
                                        Toast.LENGTH_SHORT).show()
                                    requireActivity().onBackPressed()
                                }.addOnFailureListener {
                                    //Todo delete this user
                                }

                        }

                        // taskSnapshot.metadata contains file metadata such as size, content-type, etc.

                    }
                    //println("storageRef.downloadUrl:${storageRef.downloadUrl}")


               }
        }
        return chDurum
    }

    private fun uploadName() {
        println("256.satır uploadName")
        viewModel.user.value?.let {user->
            println("258.satır uploadName")
            if (user.userName!=binding.edtProfileUsernameEdittext.text.toString()){
                user.userName =binding.edtProfileUsernameEdittext.text.toString()
                val db : FirebaseFirestore = FirebaseFirestore.getInstance()
                db.collection("users").document(user_id)/*.document(post.postUserId)*/
                    .set(user).addOnSuccessListener { dr->
                        Toast.makeText(requireContext(), "Name change succesfully..",
                            Toast.LENGTH_SHORT).show()
                        requireActivity().onBackPressed()
                    }.addOnFailureListener {
                        //Todo delete this user
                    }
            }
        }
    }
}