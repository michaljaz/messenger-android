package com.github.michaljaz.messenger

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment

class Users : Fragment() {
    lateinit var mactivity: MainActivity
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.users, container, false)
        mactivity=(activity as MainActivity)
        mactivity.supportActionBar?.title = "Users"
        val list = view.findViewById<ListView>(R.id.list)
        val users = arrayListOf ("Steve","Alex","Bob")
        list.adapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_list_item_1,
            users
        )
        return view
    }
}
