package com.github.michaljaz.messenger.fragments

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.github.michaljaz.messenger.activities.MainActivity
import com.github.michaljaz.messenger.R
import com.google.android.material.textfield.TextInputLayout


class LoginFragment : Fragment() {
    private lateinit var m: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        m = activity as MainActivity
        val view=inflater.inflate(R.layout.fragment_login_email, container, false)
        m.allowBack=true

        //arrow click listener
        view.findViewById<ImageView>(R.id.backIcon).setOnClickListener {
            try{
                findNavController().navigate(R.id.login_to_other)
                m.hideKeyboard(view)
            }catch(e:Exception){}
        }

        //Create new account
        view.findViewById<Button>(R.id.CreateNewAccount).setOnClickListener {
            findNavController().navigate(R.id.create_new_account)
        }

        //manual sign in
        view.findViewById<Button>(R.id.SignIn).setOnClickListener {
            if(m.isOnline()){
                val email=view.findViewById<TextInputLayout>(R.id.Email).editText?.text.toString().trim { it <= ' ' }
                val password=view.findViewById<TextInputLayout>(R.id.Password).editText?.text.toString().trim { it <= ' ' }
                when {
                    TextUtils.isEmpty(email) -> {
                        Toast.makeText(context,"Please enter email!", Toast.LENGTH_SHORT).show()
                    }
                    TextUtils.isEmpty(password) -> {
                        Toast.makeText(context,"Please enter password!", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        m.auth.signInWithEmailAndPassword(email,password)
                            .addOnCompleteListener { task ->
                                if(task.isSuccessful){
                                    try {
                                        m.hideKeyboard(it)
                                        findNavController().navigate(R.id.signin)
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
