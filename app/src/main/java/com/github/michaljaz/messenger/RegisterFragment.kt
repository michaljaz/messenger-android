package com.github.michaljaz.messenger

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import io.socket.emitter.Emitter

class RegisterFragment : Fragment() {
    private var list: Emitter? = null
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        this.list=(activity as MainActivity).getSocket()?.once("signup_ok") {
            showLog("sign up success")
            try {
                findNavController().navigate(R.id.action_login)
            } catch (e: Exception){}
        }
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
            (activity as MainActivity).getSocket()?.emit("signup",username,password)
        }
    }
    private fun showLog(message: String){
        Log.d("lul", message.toString())
    }
}
