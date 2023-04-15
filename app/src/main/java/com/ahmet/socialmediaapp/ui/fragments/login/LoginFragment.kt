package com.ahmet.socialmediaapp.ui.fragments.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.ahmet.socialmediaapp.ui.activities.MainActivity
import com.example.socialmediaapp.databinding.FragmentLoginBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    val TAG = "LoginFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(controlAuthIsLogin()){//If auth is null enter this section.
            toMainActivity()
        }

        setClickListeners()

    }

    private fun controlAuthIsLogin():Boolean{
        val user = Firebase.auth.currentUser
        return user != null
    }

    private fun setClickListeners() {
        binding.saveLoginButton.setOnClickListener { saveLogin() }
        binding.registerLoginButton.setOnClickListener { toRegister() }
    }

    private fun saveLogin() {
        if (checkNullableControl()) {
            val email = binding.emailLoginEdtText.text.toString()
            val password = binding.passwordLoginEdtText.text.toString()
            //
            Firebase.auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success")
                        toMainActivity()
                        val user = Firebase.auth.currentUser
                       // updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(context, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                        //updateUI(null)
                    }
                }
            //

        } else {
            Toast.makeText(context,"Please fill the all texts.",Toast.LENGTH_SHORT).show()
        }
    }

    private fun toMainActivity(){
        val intent = Intent(requireActivity(),MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun toRegister() {
        val action = LoginFragmentDirections.actionLoginFragmentToSignupFragment()
        findNavController().navigate(action)
    }

    private fun checkNullableControl(): Boolean {
        binding.apply {
            if (emailLoginEdtText.text.toString()
                    .isEmpty() && binding.passwordLoginEdtText.text.toString().isEmpty()
            ) return false
            return true
        }
    }

}