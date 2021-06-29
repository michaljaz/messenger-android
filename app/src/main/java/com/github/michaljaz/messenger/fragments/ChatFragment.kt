package com.github.michaljaz.messenger.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.github.michaljaz.messenger.activities.MainActivity
import com.github.michaljaz.messenger.R
import com.google.android.material.textfield.TextInputEditText
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result;


class ChatFragment : Fragment() {
    private lateinit var m: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_chat, container, false)
        m = activity as MainActivity

        //not allow to go back
        m.allowBack=true

        //disable drawer
        m.disableDrawer()

        //add arrow to toolbar
        m.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        m.supportActionBar!!.setDisplayShowHomeEnabled(true)

        //set toolbar title user
        m.setToolbarTitle(m.chatWith)

        //arrow click listener
        m.toolbar.setNavigationOnClickListener {
            try{
                findNavController().navigate(R.id.userchat_off)
            }catch(e:Exception){}
        }

        //on click send
        view.findViewById<ImageView>(R.id.imageView2).setOnClickListener {
            sendMessage(view)
        }

        //on enter in textview
        view.findViewById<TextInputEditText>(R.id.NewMessage).setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                sendMessage(view)
                return@OnKeyListener true
            }
            false
        })
        return view
    }
    private fun sendMessage(v:View) {
        var message=v.findViewById<TextInputEditText>(R.id.NewMessage).text
        v.findViewById<TextInputEditText>(R.id.NewMessage).setText("")
        Log.d("xd", "Sending message... $message")
        val url="https://us-central1-messenger-e3854.cloudfunctions.net/notify?uid=${m.chatWithUid}&title=Notification&body=$message"
        Log.d("xd",url)
    }
}
