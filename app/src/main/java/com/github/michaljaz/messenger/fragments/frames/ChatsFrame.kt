package com.github.michaljaz.messenger.fragments.frames

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.michaljaz.messenger.R
import com.github.michaljaz.messenger.activities.MainActivity
import com.github.michaljaz.messenger.adapters.Chat
import com.github.michaljaz.messenger.adapters.ChatsAdapter
import com.google.android.material.textfield.TextInputEditText

class ChatsFrame : Fragment() {
    private lateinit var m: MainActivity

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

        val list = view.findViewById<RecyclerView>(R.id.list)

        val chats = ArrayList<Chat>()
        chats.add(Chat("John","default","xd","m"))
        chats.add(Chat("Richard","default","xd","m2"))
        chats.add(Chat("BOB","default","x","m"))
        val adapter=ChatsAdapter(chats)

        list.adapter = adapter
        list.layoutManager = LinearLayoutManager(context)
        return view
    }
}
