package com.github.michaljaz.messenger

import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest


class RegisterFragment : Fragment() {
    private lateinit var mactivity: MainActivity
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        mactivity = activity as MainActivity
        auth=mactivity.getFirebase()
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Already have account button
        view.findViewById<Button>(R.id.RegLogin).setOnClickListener {
            findNavController().navigate(R.id.action_login)
        }

        //Register button
        view.findViewById<Button>(R.id.Register).setOnClickListener {
            if(mactivity.isOnline()){
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
                                        auth.currentUser!!.updateProfile(profileUpdates).addOnCompleteListener { itx->
                                            mactivity.hideKeyboard(it)
                                            findNavController().navigate(R.id.action_login)
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
    }
}
