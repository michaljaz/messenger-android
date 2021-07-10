package com.github.michaljaz.messenger.fragments.frames

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.michaljaz.messenger.R
import com.github.michaljaz.messenger.activities.MainActivity
import com.github.michaljaz.messenger.adapters.Chat
import com.github.michaljaz.messenger.adapters.ChatsAdapter
import com.github.michaljaz.messenger.adapters.UsersAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

class ChatsFrame : Fragment() {
    private lateinit var m: MainActivity
    private lateinit var list: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.frame_chats, container, false)
        m = activity as MainActivity

        //change menu
        m.home.changeMenu("chats")

        //not allow to go back
        m.allowBack=false

        //set toolbar title
        m.setToolbarTitle("Chats")

        //show action bar
        m.supportActionBar!!.show()

        //on click search
        view.findViewById<TextInputEditText>(R.id.Search).setOnClickListener {
            Navigation.findNavController(m, R.id.nav_host_fragment).navigate(R.id.search_on)
        }

        list = view.findViewById(R.id.list)
        list.layoutManager = LinearLayoutManager(context)
        val chatsUserIds = mutableMapOf<String,Boolean>()
        m.db.child("/usersData/${m.auth.currentUser!!.uid}/chats").addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d("ADDED",snapshot.key.toString())
                chatsUserIds[snapshot.key.toString()]=true
                updateChats(chatsUserIds)
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                Log.d("DELETED",snapshot.key.toString())
                chatsUserIds.remove(snapshot.key.toString())
                updateChats(chatsUserIds)
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        return view
    }

    fun updateChats(chatUserIds:MutableMap<String,Boolean>){
        val chats = ArrayList<Chat>()
        for ((k, _) in chatUserIds) {
            m.db.child("/usersData/$k/displayName").get().addOnSuccessListener { displayName ->
                m.db.child("/usersData/$k/photoUrl").get().addOnSuccessListener { photoUrl ->
                    m.getChatRef(k).child("lastMessage").get().addOnSuccessListener { lastMessage->
                        chats.add(Chat(displayName.value.toString(),photoUrl.value.toString(),k,lastMessage.value.toString()))
                        try{
                            list.adapter=ChatsAdapter(chats)
                            (list.adapter as ChatsAdapter).onItemClick= {
                                Log.d("xd",it.displayName)
                                m.chatWithUid=it.userId
                                m.chatWith=it.displayName
                                m.chatWithPhoto=it.photoUrl
                                findNavController().navigate(R.id.userChat_on)
                            }
                            (list.adapter as ChatsAdapter).onItemLongClick= {
                                m.bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                            }
                        }catch(e:Exception){ }
                    }
                }
            }
        }
    }
}
