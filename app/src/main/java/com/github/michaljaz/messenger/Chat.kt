package com.github.michaljaz.messenger

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.android.material.textfield.TextInputEditText

class Chat : Fragment() {
    private lateinit var m: MainActivity
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.chat, container, false)
        m = activity as MainActivity
        m.allowBack=false

        //set toolbar title
        m.setToolbarTitle("Chats")

        //show action bar
        m.supportActionBar!!.show()

        view.findViewById<TextInputEditText>(R.id.Search).setOnClickListener {
            Log.d("xd","xd")
            Navigation.findNavController(m,R.id.nav_host_fragment).navigate(R.id.search_on)
        }

        return view
    }
}
