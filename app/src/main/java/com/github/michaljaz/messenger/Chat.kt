package com.github.michaljaz.messenger

import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference

class Chat : Fragment() {
    private lateinit var mactivity: MainActivity
    private lateinit var auth: FirebaseAuth
    private lateinit var db: DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mactivity = activity as MainActivity
        mactivity.supportActionBar?.title = "Chats"
        auth = mactivity.getFirebase()
        db = mactivity.getFirebaseDatabase()

        return inflater.inflate(R.layout.chat, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }
}
