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
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {
    lateinit var auth: FirebaseAuth
    lateinit var db: DatabaseReference
    lateinit var mactivity: MainActivity

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        mactivity=(activity as MainActivity)
        setHasOptionsMenu(true)
        mactivity.enableDrawer()
        mactivity.supportActionBar?.setTitle("Chats")

        auth=mactivity.getFirebase()
        db = mactivity.getFirebaseDatabase()
        Log.d("lul",auth.currentUser!!.uid)
        if(mactivity.isOnline()){
            db.child("users").child(auth.currentUser!!.uid).child("email").setValue(auth.currentUser!!.email)
        }

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
