package com.github.michaljaz.messenger

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference

class HomeFragment : Fragment() {
    lateinit var auth: FirebaseAuth
    lateinit var db:DatabaseReference
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        (activity as MainActivity).enableDrawer()
        (activity as MainActivity).supportActionBar?.setTitle("Chats")
        auth=(activity as MainActivity).getFirebase()
        db=(activity as MainActivity).getFirebaseDatabase()
        Log.d("xd",auth.currentUser!!.uid)
        db.child("users").child(auth.currentUser!!.uid).child("test").setValue(true)
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu);

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.Logout).setOnClickListener {
            auth.signOut()
            findNavController().navigate(R.id.logout)
        }
    }
}
