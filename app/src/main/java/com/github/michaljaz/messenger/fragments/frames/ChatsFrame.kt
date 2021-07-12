package com.github.michaljaz.messenger.fragments.frames

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.michaljaz.messenger.R
import com.github.michaljaz.messenger.activities.MainActivity
import com.github.michaljaz.messenger.adapters.Chat
import com.github.michaljaz.messenger.adapters.ChatsAdapter
import com.github.michaljaz.messenger.fragments.HomeFragment
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError


class ChatsFrame : Fragment() {
    private lateinit var m: MainActivity
    private lateinit var list: RecyclerView
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout

    @SuppressLint("ResourceAsColor")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.frame_chats, container, false)
        m = activity as MainActivity

        val home=(parentFragment as HomeFragment)

        home.appbar.findViewById<TextView>(R.id.toolbarTitle).text="Chats"

        mSwipeRefreshLayout=view.findViewById(R.id.swipe_refresh)
        mSwipeRefreshLayout.setOnRefreshListener {
            val handler = Handler()
            handler.postDelayed( {
                if (mSwipeRefreshLayout.isRefreshing) {
                    mSwipeRefreshLayout.isRefreshing = false
                }
            }, 1000)
        }


        //not allow to go back
        m.allowBack=false

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            home.appbar.elevation = 0f
        }

        list = view.findViewById(R.id.list)
        list.addOnScrollListener(object: RecyclerView.OnScrollListener(){
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy);

                if(!recyclerView.canScrollVertically(-1)) {
                    home.appbar.elevation = 0f
                } else {
                    home.appbar.elevation = 4f
                }
            }
        })

        val chatsInit = ArrayList<Chat>()
        chatsInit.add(Chat("","","empty","",""))
        list.adapter=ChatsAdapter(chatsInit)

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

        //on click search
        (list.adapter as ChatsAdapter).onSearchClick={
            Navigation.findNavController(m, R.id.nav_host_fragment).navigate(R.id.search_on)
        }

        return view
    }

    fun updateChats(chatUserIds:MutableMap<String,Boolean>){
        val chats = ArrayList<Chat>()
        chats.add(Chat("","","empty","",""))
        for ((k, _) in chatUserIds) {
            m.db.child("/usersData/$k/displayName").get().addOnSuccessListener { displayName ->
                m.db.child("/usersData/$k/photoUrl").get().addOnSuccessListener { photoUrl ->
                    m.getChatRef(k).child("lastMessage").get().addOnSuccessListener { lastMessage->
                        m.getChatRef(k).child("lastMessageTimeStamp").get().addOnSuccessListener { lastMessageTimeStamp->
                            chats.add(Chat(
                                displayName.value.toString(),
                                photoUrl.value.toString(),
                                k,
                                lastMessage.value.toString(),
                                lastMessageTimeStamp.value.toString()
                            ))
                            chats.sortWith { lhs, rhs ->
                                if(lhs.userId=="empty"){
                                    -1
                                }else if(rhs.userId=="empty"){
                                    1
                                }else{
                                    if (lhs.lastMessageTimeStamp > rhs.lastMessageTimeStamp) -1 else if (lhs.lastMessageTimeStamp < rhs.lastMessageTimeStamp) 1 else 0
                                }

                            }
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
                                    m.dialog.show()
                                }
                                (list.adapter as ChatsAdapter).onSearchClick={
                                    Navigation.findNavController(m, R.id.nav_host_fragment).navigate(R.id.search_on)
                                }
                            }catch(e:Exception){ }
                        }
                    }
                }
            }
        }
    }
}
