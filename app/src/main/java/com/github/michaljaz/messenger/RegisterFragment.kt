package com.github.michaljaz.messenger

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.socket.emitter.Emitter

class RegisterFragment : Fragment() {
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.RegLogin).setOnClickListener {
            findNavController().navigate(R.id.action_login)
        }
        view.findViewById<Button>(R.id.Register).setOnClickListener {
            val username=view.findViewById<TextInputLayout>(R.id.Email).editText?.text.toString()
            val password=view.findViewById<TextInputLayout>(R.id.Password).editText?.text.toString()
            if(username != "" && password != ""){
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(username,password)
                    .addOnCompleteListener { task ->
                        if(task.isSuccessful){
                            showLog("sign up success")
                            val firebaseUser: FirebaseUser=task.result!!.user!!
                            try {
                                findNavController().navigate(R.id.action_login)
                            } catch (e: Exception){}
                        }else{
                            Toast.makeText(context,task.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
            }else{
                Toast.makeText(context,"Empty email or password field!", Toast.LENGTH_SHORT).show()
            }

        }
    }
    private fun showLog(message: String){
        Log.d("lul", message.toString())
    }
}
