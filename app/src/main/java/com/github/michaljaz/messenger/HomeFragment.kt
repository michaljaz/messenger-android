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
        try {
            db = mactivity.getFirebaseDatabase()
            db.child("users").child(auth.currentUser!!.uid).child("test").setValue(true)
        }catch (e:Exception){}
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
