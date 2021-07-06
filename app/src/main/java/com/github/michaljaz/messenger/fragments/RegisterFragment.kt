package com.github.michaljaz.messenger.fragments

import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.github.michaljaz.messenger.activities.MainActivity
import com.github.michaljaz.messenger.R
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest


class RegisterFragment : Fragment() {
    private lateinit var m: MainActivity

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        m = activity as MainActivity
        val view=inflater.inflate(R.layout.fragment_register_email, container, false)
        //allow to go back
        m.allowBack=true

        //add arrow to toolbar
        m.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        m.supportActionBar!!.setDisplayShowHomeEnabled(true)

        //arrow click listener
        m.toolbar.setNavigationOnClickListener {
            try{
                findNavController().navigate(R.id.register_to_other)
                m.hideKeyboard(view)
            }catch(e:Exception){}
        }

        //Already have account button
        view.findViewById<Button>(R.id.AlreadyHaveAccount).setOnClickListener {
            findNavController().navigate(R.id.already_have_account)
        }

        //Register button
        view.findViewById<Button>(R.id.SignUp).setOnClickListener { v ->
            if(m.isOnline()){
                val username=view.findViewById<TextInputLayout>(R.id.Email).editText?.text.toString().trim { it <= ' ' }
                val password=view.findViewById<TextInputLayout>(R.id.Password).editText?.text.toString().trim { it <= ' ' }
                val displayName=view.findViewById<TextInputLayout>(R.id.DisplayName).editText?.text.toString().trim { it <= ' ' }

                when {
                    TextUtils.isEmpty(username) -> {
                        Toast.makeText(context,"Please enter email!", Toast.LENGTH_SHORT).show()
                    }
                    TextUtils.isEmpty(password) -> {
                        Toast.makeText(context,"Please enter password!", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        FirebaseAuth.getInstance().createUserWithEmailAndPassword(username,password)
                            .addOnCompleteListener { task ->
                                if(task.isSuccessful){
                                    try {
                                        val profileUpdates = UserProfileChangeRequest.Builder()
                                            .setDisplayName(displayName)
                                            .setPhotoUri(Uri.parse("default"))
                                            .build()
                                        m.auth.currentUser!!.updateProfile(profileUpdates).addOnCompleteListener {
                                            m.hideKeyboard(v)
                                            findNavController().navigate(R.id.register)
                                        }
                                    } catch (e: Exception){}
                                }else{
                                    Toast.makeText(context,task.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                }
            }else{
                Toast.makeText(context, "You are offline", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}
