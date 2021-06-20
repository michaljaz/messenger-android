package com.github.michaljaz.messenger

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText

class Chat : Fragment() {
    private lateinit var m: MainActivity
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        m = activity as MainActivity
        m.setToolbarTitle("Chats")

        return inflater.inflate(R.layout.chat, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<TextInputEditText>(R.id.Search).setOnClickListener {
            Log.d("xd","xd")
        }
    }
}
