package com.github.michaljaz.messenger

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference

class HomeFragment : Fragment() {
    private lateinit var mactivity: MainActivity
    private lateinit var auth: FirebaseAuth
    private lateinit var db: DatabaseReference
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.home_fragment, container, false)
        mactivity = activity as MainActivity
        auth = mactivity.getFirebase()
        db = mactivity.getFirebaseDatabase()
        setHasOptionsMenu(true)
        mactivity.enableDrawer()

        val userdb = db.child("usersData").child(auth.currentUser!!.uid)
        db.child("users").child(auth.currentUser!!.uid).setValue(true)

        val profile=auth.currentUser!!.providerData[1]
        userdb.child("email").setValue(profile.email.toString())
        userdb.child("providerId").setValue(profile.providerId)
        userdb.child("displayName").setValue(auth.currentUser!!.providerData[0].displayName.toString())
        mactivity.updateHeader(
            auth.currentUser!!.providerData[0].displayName.toString(),
            profile.email.toString()
        )
        if(profile.providerId!="password"){
            userdb.child("photoUrl").setValue(profile.photoUrl.toString())
        }else{
            userdb.child("photoUrl").setValue(auth.currentUser!!.providerData[0].photoUrl.toString())
        }


        childFragmentManager
            .beginTransaction()
            .replace(R.id.frame_layout,Chat())
            .setTransition(FragmentTransaction.TRANSIT_NONE)
            .commit()
        view.findViewById<BottomNavigationView>(R.id.bottom_navigation).setOnNavigationItemSelectedListener { item ->
            childFragmentManager
            when(item.itemId){
                R.id.page_1 -> {
                    childFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame_layout,Chat())
                        .setTransition(FragmentTransaction.TRANSIT_NONE)
                        .commit()
                }
                R.id.page_2 -> {
                    childFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame_layout,Users())
                        .setTransition(FragmentTransaction.TRANSIT_NONE)
                        .commit()
                }
            }
            true
        }

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu);

        super.onCreateOptionsMenu(menu, inflater)
    }
}
