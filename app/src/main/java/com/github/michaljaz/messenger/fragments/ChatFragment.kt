package com.github.michaljaz.messenger.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.view.marginTop
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
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.squareup.picasso.Picasso

class ChatFragment : Fragment() {
    private lateinit var m: MainActivity
    private lateinit var list: RecyclerView
    private lateinit var friendIcon: ImageView
    private lateinit var friendName: TextView
    private var messages = ArrayList<Message>()

    private fun hideFriend(){
        friendIcon.animate().setDuration(100).alpha(0f)
        friendName.animate().setDuration(100).alpha(0f)
    }
    private fun showFriend(){
        friendIcon.animate().setDuration(100).alpha(1f)
        friendName.animate().setDuration(100).alpha(1f)
    }

    private fun friendLoop(llm:LinearLayoutManager){
        if(llm.findFirstVisibleItemPosition()==0){
            hideFriend()
        }else{
            showFriend()
        }
        val handler = Handler()
        handler.postDelayed( {
            friendLoop(llm)
        }, 100)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_chat, container, false)
        m = activity as MainActivity

        friendIcon=view.findViewById(R.id.userIcon)
        friendName=view.findViewById(R.id.toolbarTitle)

        messages.add(Message("intro",false))

        //not allow to go back
        m.allowBack=true

        //disable drawer
        m.disableDrawer()

        //update user icon
        if(m.chatWithPhoto!="default"){
            Picasso.get()
                .load(m.chatWithPhoto)
                .transform(RoundedTransformation(100, 0))
                .into(friendIcon)
        }

        friendName.text=m.chatWith

        view.findViewById<ImageView>(R.id.backIcon).setOnClickListener {
            try{
                findNavController().navigate(R.id.userchat_off)
                m.hideKeyboard(view)
            }catch(e:Exception){}
        }

        //on click send
        view.findViewById<ImageView>(R.id.imageView2).setOnClickListener {
            sendMessage(view)
        }

        //on enter in textview
        view.findViewById<EditText>(R.id.NewMessage).setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                sendMessage(view)
                return@OnKeyListener true
            }
            false
        })

        //dynamic like and arrow icon
        view.findViewById<EditText>(R.id.NewMessage).doAfterTextChanged {
            if(view.findViewById<EditText>(R.id.NewMessage).text.toString()==""){
                view.findViewById<ImageView>(R.id.imageView2).setImageResource(R.drawable.ic_like)
            }else{
                view.findViewById<ImageView>(R.id.imageView2).setImageResource(R.drawable.ic_right_arrow)
            }
        }

        //test messages adapter
        list = view.findViewById(R.id.list)
        val llm=LinearLayoutManager(context)
        llm.stackFromEnd = true
        list.layoutManager = llm

        friendIcon.alpha=0f
        friendName.alpha=0f
        val handler = Handler()
        handler.postDelayed( {
            friendLoop(llm)
        }, 100)


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

    @SuppressLint("ResourceAsColor")
    fun createReactionDialog(message:TextView){
        val dialog = Dialog(m)
        val v = layoutInflater.inflate(R.layout.dialog_chat_reaction, null)
        val reaction=v.findViewById<LinearLayout>(R.id.reaction)
        dialog.window?.attributes?.gravity=Gravity.TOP
        val param=reaction.layoutParams as LinearLayout.LayoutParams
        val marginTop=m.lastTouch.toInt()-250
        param.setMargins(0,marginTop,0,0)
        reaction.layoutParams=param

        message.background.alpha=150
        val r1=v.findViewById<ImageView>(R.id.r1)
        val r2=v.findViewById<ImageView>(R.id.r2)
        val r3=v.findViewById<ImageView>(R.id.r3)
        val r4=v.findViewById<ImageView>(R.id.r4)
        val r5=v.findViewById<ImageView>(R.id.r5)
        val r6=v.findViewById<ImageView>(R.id.r6)
        val r7=v.findViewById<ImageView>(R.id.r7)
        val delay:Long=150

        r1.alpha=0f
        r2.alpha=0f
        r3.alpha=0f
        r4.alpha=0f
        r5.alpha=0f
        r6.alpha=0f
        r7.alpha=0f
        r1.animate().setDuration(delay).alpha(1f)
        r2.animate().setDuration(delay*2).alpha(1f)
        r3.animate().setDuration(delay*3).alpha(1f)
        r4.animate().setDuration(delay*4).alpha(1f)
        r5.animate().setDuration(delay*5).alpha(1f)
        r6.animate().setDuration(delay*6).alpha(1f)
        r7.animate().setDuration(delay*7).alpha(1f)
        v.findViewById<LinearLayout>(R.id.reactionbg).setOnClickListener {
            Log.d("xd",it.toString())
            dialog.dismiss()
        }
        dialog.setOnDismissListener {
            message.background.alpha=255
        }

        dialog.setCancelable(true)
        dialog.setContentView(v)
        dialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.window?.setBackgroundDrawable(ColorDrawable(android.R.color.transparent))
        dialog.show()
    }

    private fun addMessage(message:String, isMe:Boolean){
        messages.add(Message(message,isMe))
        if(list.adapter==null){
            list.adapter=MessagesAdapter(messages,m.chatWithPhoto,m.chatWith)
            (list.adapter as MessagesAdapter).onFriendLongClick={ _,msg->
                createReactionDialog(msg)
            }
            (list.adapter as MessagesAdapter).onMyLongClick={ _,msg->
                createReactionDialog(msg)
            }
        }else{
            (list.adapter as MessagesAdapter).notifyDataSetChanged()
            list.smoothScrollToPosition(messages.size - 1)
        }

    }

    private fun sendMessage(v:View) {
        val message=v.findViewById<EditText>(R.id.NewMessage).text
        if(message.toString()!=""){
            v.findViewById<EditText>(R.id.NewMessage).setText("")
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
