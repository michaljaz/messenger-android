package com.github.michaljaz.messenger.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.github.michaljaz.messenger.activities.MainActivity
import com.github.michaljaz.messenger.R
import com.google.android.material.textfield.TextInputEditText
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result

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

        //fix size
        for (i in 0 until m.toolbar.childCount) {
            val it=m.toolbar.getChildAt(i)
            if (it is ImageButton) {
                it.scaleX = 1f
                it.scaleY = 1f
            }
            if (it is TextView){
                it.textSize = 20F
            }
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
        val message=v.findViewById<TextInputEditText>(R.id.NewMessage).text
        v.findViewById<TextInputEditText>(R.id.NewMessage).setText("")
        Log.d("xd", "Sending message... $message")
        val url="https://us-central1-messenger-e3854.cloudfunctions.net/notify?uid=${m.chatWithUid}&title=Notification&body=$message"
        url.httpGet().responseString { _, _, result ->
            when (result) {
                is Result.Failure -> {
                    val ex = result.getException()
                    Log.d("xd",ex.toString())
                }
                is Result.Success -> {
                    val data = result.get()
                    Log.d("xd",data)
                }
            }
        }
        m.db.child("/usersData/${m.chatWithUid}/chats/${m.auth.currentUser!!.uid}").setValue(true)
        m.db.child("/usersData/${m.auth.currentUser!!.uid}/chats/${m.chatWithUid}").setValue(true)
        val ref = if(m.chatWithUid>m.auth.currentUser!!.uid){
            m.db.child("/chats/${m.auth.currentUser!!.uid}/${m.chatWithUid}")
        }else{
            m.db.child("/chats/${m.chatWithUid}/${m.auth.currentUser!!.uid}")
        }
        val key=ref.child("messages").push().key
        ref.child("messages/${key}").setValue(mapOf(
            "data" to message.toString(),
            "timestamp" to System.currentTimeMillis().toString(),
            "sender" to m.auth.currentUser!!.uid
        ))
    }
}
