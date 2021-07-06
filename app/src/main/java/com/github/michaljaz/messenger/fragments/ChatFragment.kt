package com.github.michaljaz.messenger.fragments

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.core.view.marginLeft
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.github.michaljaz.messenger.activities.MainActivity
import com.github.michaljaz.messenger.R
import com.google.android.material.textfield.TextInputEditText
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.github.michaljaz.messenger.adapters.MessagesAdapter
import com.github.michaljaz.messenger.utils.RoundedTransformation
import com.squareup.picasso.Picasso
import org.w3c.dom.Text

class ChatFragment : Fragment() {
    private lateinit var m: MainActivity
    private lateinit var list: ListView
    private  var texts= ArrayList<String>()
    private var isMe= ArrayList<Boolean>()


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

        Picasso.get()
            .load(m.chatWithPhoto)
            .transform(RoundedTransformation(100, 0))
            .into(object : com.squareup.picasso.Target {
                override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                    val d: Drawable = BitmapDrawable(resources, bitmap)
                    m.toolbar.logo = d
                }

                override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {}
                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
            })

        //set toolbar title user
        m.setToolbarTitle(m.chatWith)

        //arrow click listener
        m.toolbar.setNavigationOnClickListener {
            try{
                findNavController().navigate(R.id.userchat_off)
                m.hideKeyboard(view)
            }catch(e:Exception){}
        }

        //fix size
        for (i in 0 until m.toolbar.childCount) {
            val it=m.toolbar.getChildAt(i)
            if (it is ImageView) {
                if(it is ImageButton){
                    it.scaleX = 1f
                    it.scaleY = 1f
                }else{
                    it.scaleX = 0.7f
                    it.scaleY = 0.7f
                }
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

        //test messages adapter
        list = view.findViewById(R.id.list)
        loop()
        return view
    }
    private fun loop(){
        Handler().postDelayed(
            {
                addMessage("Hello",Math.random()<0.5)
                updateList()
                loop()
            },
            1000 // value in milliseconds
        )
    }
    private fun addMessage(message:String,isme:Boolean){
        texts.add(message)
        isMe.add(isme)
    }
    private fun updateList(){
        list.adapter=MessagesAdapter(requireContext(),texts,isMe,m.chatWithPhoto)
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
        try{
            m.db.child("/usersData/${m.chatWithUid}/chats/${m.auth.currentUser!!.uid}").setValue(true)
            m.db.child("/usersData/${m.auth.currentUser!!.uid}/chats/${m.chatWithUid}").setValue(true)
        }catch(e:Exception){}

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
