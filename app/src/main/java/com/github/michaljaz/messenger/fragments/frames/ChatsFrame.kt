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
import androidx.core.widget.NestedScrollView
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
import com.github.michaljaz.messenger.adapters.OnlineUser
import com.github.michaljaz.messenger.adapters.OnlineUsersAdapter
import com.github.michaljaz.messenger.fragments.HomeFragment
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError


class ChatsFrame : Fragment() {
    private lateinit var m: MainActivity
    private lateinit var list: RecyclerView
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var home:HomeFragment

    @SuppressLint("ResourceAsColor")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.frame_chats, container, false)
        m = activity as MainActivity

        home=(parentFragment as HomeFragment)

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

        //toolbar elevation connected with recycler view list
        list = view.findViewById(R.id.list)
        val nested=view.findViewById<NestedScrollView>(R.id.nestedScroll)
        nested.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, _, _, _ ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if(nested.canScrollVertically(-1)){
                    home.appbar.elevation = 4f
                }else{
                    home.appbar.elevation = 0f
                }
            }
        })

        //hide elevation loop
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            home.appbar.elevation = 0f
        }

        //initialize chats recycler view
        list.layoutManager = LinearLayoutManager(context)
        val cacheChats=m.getCacheChats()
        if(cacheChats!=null){
            list.adapter=ChatsAdapter(cacheChats)
        }else{
            list.adapter=ChatsAdapter(ArrayList())
        }

        //listen user chats
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

        //handle clicks
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
        val search: TextInputEditText = view.findViewById(R.id.Search)
        search.setOnClickListener {
            Navigation.findNavController(m, R.id.nav_host_fragment).navigate(R.id.search_on)
        }

        //setup online users rv
        val hlist: RecyclerView=view.findViewById(R.id.horizontalList)
        val onlineUsers=ArrayList<OnlineUser>()
        onlineUsers.add(OnlineUser("Steve Jobs","default"))
        onlineUsers.add(OnlineUser("John Doe","default"))
        onlineUsers.add(OnlineUser("John Doe","default"))
        onlineUsers.add(OnlineUser("John Doe","default"))
        onlineUsers.add(OnlineUser("John Doe","default"))
        onlineUsers.add(OnlineUser("John Doe","default"))
        onlineUsers.add(OnlineUser("John Doe","default"))
        hlist.adapter= OnlineUsersAdapter(onlineUsers)
        hlist.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        return view
    }

    fun updateChats(chatUserIds:MutableMap<String,Boolean>){
        val chats = ArrayList<Chat>()
        var counter=0
        for ((k, _) in chatUserIds) {
            m.db.child("/usersData/$k/displayName").get().addOnSuccessListener { displayName ->
                m.db.child("/usersData/$k/photoUrl").get().addOnSuccessListener { photoUrl ->
                    m.getChatRef(k)?.child("lastMessage")?.get()?.addOnSuccessListener { lastMessage->
                        m.getChatRef(k)?.child("lastMessageTimeStamp")?.get()?.addOnSuccessListener { lastMessageTimeStamp->
                            counter++
                            chats.add(Chat(
                                displayName.value.toString(),
                                photoUrl.value.toString(),
                                k,
                                lastMessage.value.toString(),
                                lastMessageTimeStamp.value.toString()
                            ))
                            if(counter==chatUserIds.size){
                                chats.sortWith { lhs, rhs ->
                                    if (lhs.lastMessageTimeStamp > rhs.lastMessageTimeStamp) -1 else if (lhs.lastMessageTimeStamp < rhs.lastMessageTimeStamp) 1 else 0

                                }
                                try{
                                    (list.adapter as ChatsAdapter).updateListItems(chats)
                                    m.setCacheChats(chats)
                                }catch(e:Exception){ }
                            }
                        }
                    }
                }
            }
        }
    }
}
