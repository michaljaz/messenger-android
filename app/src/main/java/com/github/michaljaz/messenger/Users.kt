package com.github.michaljaz.messenger

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment

class Users : Fragment() {
    lateinit var mactivity: MainActivity
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mactivity=(activity as MainActivity)
        mactivity.supportActionBar?.title = "Users"
        return inflater.inflate(R.layout.users, container, false)
    }
}
