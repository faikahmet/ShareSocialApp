package com.ahmet.socialmediaapp.ui.fragments.signup

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import com.ahmet.socialmediaapp.data.model.User
import com.ahmet.socialmediaapp.ui.activities.MainActivity
import com.example.socialmediaapp.databinding.FragmentSignupBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.util.*

class SignupFragment : Fragment() {
    val TAG = "SignupFragment"

    private lateinit var binding : FragmentSignupBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignupBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setClickListeners()

    }

    private fun setClickListeners() {
        binding.signSignupButton.setOnClickListener { saveUser() }
        binding.signupLoginButton.setOnClickListener { toLogin() }
        binding.signupPickdate.setOnClickListener { pickDate() }
    }

    private fun pickDate() {
        // on below line we are getting
        // the instance of our calendar.
        val c = Calendar.getInstance()

        // on below line we are getting
        // our day, month and year.
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        // on below line we are creating a
        // variable for date picker dialog.
        val datePickerDialog = DatePickerDialog(
            // on below line we are passing context.
            requireContext(),
            { view, year_, monthOfYear, dayOfMonth ->
                // on below line we are setting
                // date to our text view.
                binding.signupDateTextview.text = (dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year_)
            },
            // on below line we are passing year, month
            // and day for the selected date in our date picker.
            year,
            month,
            day
        )
        // at last we are calling show
        // to display our date picker dialog.
        datePickerDialog.show()
    }

    private fun toLogin() {

    }

    private fun saveUser() {
        if(checkNullableControl()){
            val email = binding.signupEmailEdtText.text.toString()
            val password = binding.signupPasswordEdtText.text.toString()
            val userName = binding.signupUsernameEdtText.text.toString()
            Firebase.auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success")
                        val user = Firebase.auth.currentUser
                        if(user!=null){
                            val user_ = User(
                                userId = user.uid,
                                userName = userName,
                                userEmail = email,
                                userRegisterDate = FieldValue.serverTimestamp(),
                                userLogo =""
                            )
                            saveToFirebaseUser(user_)
                        }else{
                            //Todo delete this user
                        }

                    // updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(requireContext(), "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                        //updateUI(null)
                    }
                }
        }
        else{
            Toast.makeText(requireContext(), "Fill all the view.",
                Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveToFirebaseUser(user: User) {
        val db : FirebaseFirestore = FirebaseFirestore.getInstance()
        val postData =
            HashMap<String, Any>()
    postData["userId"] = user.userId
    postData["userEmail"] = user.userEmail
    postData["userName"] = user.userName
    postData["userRegisterDate"] = user.userRegisterDate?:0
    postData["userLogo"] = user.userLogo


        db.collection("users").document(user.userId).set(postData).addOnSuccessListener {dr->
            Toast.makeText(requireContext(), "Signup has been succesfully..",
                Toast.LENGTH_SHORT).show()
            toMainActivity()
           }.addOnFailureListener {
            //Todo delete this user
        }
    }

    private fun toMainActivity() {
        val intent = Intent(requireActivity(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }


    private fun checkNullableControl(): Boolean {
        binding.apply {
            if (
                signupEmailEdtText.text.toString().isEmpty()
                ||
                binding.signupUsernameEdtText.text.toString().isEmpty()
                ||
                !onlyOneCheckedRadioButton()
                ||
                binding.signupPasswordEdtText.text.toString().isEmpty()
            ) return false
            return true
        }
    }

    private fun onlyOneCheckedRadioButton():Boolean{
        val selectedId = binding.radioGroup.checkedRadioButtonId
       val rB= requireActivity().findViewById<RadioButton>(selectedId)
        Toast.makeText(context,rB.text,Toast.LENGTH_SHORT).show()
        return true
    }

}