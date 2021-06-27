package com.github.michaljaz.messenger

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
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
            Navigation.findNavController(m,R.id.nav_host_fragment).navigate(R.id.search_on)
        }

        return view
    }
}
