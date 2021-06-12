package com.github.michaljaz.messenger

import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference

class Chat : Fragment() {
    lateinit var mactivity: MainActivity
    lateinit var auth: FirebaseAuth
    lateinit var db: DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mactivity=(activity as MainActivity)
        mactivity.supportActionBar?.setTitle("Chats")
        auth=mactivity.getFirebase()
        db = mactivity.getFirebaseDatabase()
        return inflater.inflate(R.layout.chat, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<Button>(R.id.Logout).setOnClickListener {
            auth.signOut()
            findNavController().navigate(R.id.logout)
        }
        view.findViewById<Button>(R.id.Remove).setOnClickListener {
            db.child("users").child(auth.currentUser!!.uid).removeValue().addOnCompleteListener {
                auth.currentUser!!.delete()
                auth.signOut()
                findNavController().navigate(R.id.logout)
            }
        }

    }

}
