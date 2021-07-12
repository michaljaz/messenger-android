package com.github.michaljaz.messenger.fragments

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toolbar
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.github.michaljaz.messenger.R
import com.github.michaljaz.messenger.activities.MainActivity
import com.github.michaljaz.messenger.adapters.Message
import com.github.michaljaz.messenger.adapters.MessagesAdapter
import com.github.michaljaz.messenger.utils.RoundedTransformation
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.squareup.picasso.Picasso

class ChatFragment : Fragment() {
    private lateinit var m: MainActivity
    private lateinit var list: RecyclerView
    private var messages = ArrayList<Message>()

    fun loadHamburger(){
        Picasso.get()
            .load(m.chatWithPhoto)
            .transform(RoundedTransformation(100, 0))
            .into(object : com.squareup.picasso.Target {
                override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                    val d: Drawable = BitmapDrawable(resources, bitmap)
                    m.toolbar.logo = d
                }

                override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {
                    loadHamburger()
                }
                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
            })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_chat, container, false)
        m = activity as MainActivity

        //show action bar
        m.supportActionBar!!.show()

        //not allow to go back
        m.allowBack=true

        //disable drawer
        m.disableDrawer()

        //add arrow to toolbar
        m.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        m.supportActionBar!!.setDisplayShowHomeEnabled(true)

        if(m.chatWithPhoto=="default"){
            m.toolbar.setLogo(R.drawable.ic_profile_user)
        }else{
            loadHamburger()

        }

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

        //dynamic like and arrow icon
        view.findViewById<TextInputEditText>(R.id.NewMessage).doAfterTextChanged {
            if(view.findViewById<TextInputEditText>(R.id.NewMessage).text.toString()==""){
                view.findViewById<ImageView>(R.id.imageView2).setImageResource(R.drawable.ic_like)
            }else{
                view.findViewById<ImageView>(R.id.imageView2).setImageResource(R.drawable.ic_right_arrow)
            }
        }

        //test messages adapter
        list = view.findViewById(R.id.list)
        list.layoutManager = LinearLayoutManager(context)
        m.getChatRef(m.chatWithUid).child("messages").addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message=snapshot.value as Map<*, *>
                Log.d("xd",message["data"].toString())
                addMessage(message["data"].toString(),message["sender"].toString()==m.auth.currentUser!!.uid)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
        return view
    }
    private fun addMessage(message:String,isMe:Boolean){
        messages.add(Message(message,isMe))
        if(list.adapter==null){
            list.adapter=MessagesAdapter(messages,m.chatWithPhoto)
        }else{
            (list.adapter as MessagesAdapter).notifyDataSetChanged()
            list.smoothScrollToPosition(messages.size - 1)
        }

    }

    private fun sendMessage(v:View) {
        val message=v.findViewById<TextInputEditText>(R.id.NewMessage).text
        if(message.toString()!=""){
            v.findViewById<TextInputEditText>(R.id.NewMessage).setText("")
            Log.d("xd", "Sending message... $message")
            val url="https://us-central1-messenger-e3854.cloudfunctions.net/notify?to=${m.chatWithUid}&from=${m.auth.currentUser!!.uid}&body=$message"
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

            val timestamp=System.currentTimeMillis().toString()
            m.getChatRef(m.chatWithUid).child("lastMessage").setValue("${m.auth.currentUser!!.displayName}: $message")
            m.getChatRef(m.chatWithUid).child("lastMessageTimeStamp").setValue(timestamp)

            val key=m.getChatRef(m.chatWithUid).child("messages").push().key
            m.getChatRef(m.chatWithUid).child("messages/${key}").setValue(mapOf(
                "data" to message.toString(),
                "timestamp" to timestamp,
                "sender" to m.auth.currentUser!!.uid
            ))
        }
    }
}
