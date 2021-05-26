package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import io.socket.emitter.Emitter

class LoginFragment : Fragment() {
    private var list: Emitter? = null

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        this.list=(activity as MainActivity).socket.once("auth_ok") {
            showLog("authorized")
            findNavController().navigate(R.id.login)
        }

        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onDestroyView() {
        this.list?.off()
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.LogRegister).setOnClickListener {
            findNavController().navigate(R.id.action_register)
        }
        view.findViewById<Button>(R.id.Login).setOnClickListener {

            val username=view.findViewById<TextInputLayout>(R.id.Username).editText?.text.toString()
            val password=view.findViewById<TextInputLayout>(R.id.Password).editText?.text.toString()
            (activity as MainActivity).socket.emit("auth",username,password)
        }

    }
    private fun showLog(message: String){
        Log.d("lul", message.toString())
    }
}