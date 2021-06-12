package com.github.michaljaz.messenger

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference

class HomeFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: DatabaseReference
    private lateinit var mactivity: MainActivity

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        mactivity = activity as MainActivity
        setHasOptionsMenu(true)
        mactivity.enableDrawer()

        auth = mactivity.getFirebase()
        db = mactivity.getFirebaseDatabase()
        val userdb = db.child("users").child(auth.currentUser!!.uid)
        if(mactivity.isOnline()){
            val profile=auth.currentUser!!.providerData[1]
            Log.d("xd",profile.providerId)
            userdb.child("email").setValue(profile.email)
            userdb.child("providerId").setValue(profile.providerId)
            when(profile.providerId){
                "google.com","facebook.com" -> {
                    userdb.child("displayName").setValue(profile.displayName)
                    userdb.child("photoUrl").setValue(profile.photoUrl.toString())
                }
                "password" -> {
                    Log.d("xd","There is no display name to save")
                }
            }

//            Log.d("xd", auth.currentUser!!.providerData[1].providerId)
//            auth.currentUser?.let {
//                for (profile in it.providerData) {
//                    // Id of the provider (ex: google.com)
//                    val providerId = profile.providerId
//
//                    // UID specific to the provider
//                    val uid = profile.uid
//
//                    // Name, email address, and profile photo Url
//                    val name = profile.displayName
//                    val email = profile.email
//                    val photoUrl = profile.photoUrl
//                    Log.d("x", providerId)
//                    Log.d("x", uid)
//                    Log.d("x", name.toString())
//                    Log.d("x", email.toString())
//                    Log.d("x",photoUrl.toString())
//                }
//            }
        }

        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu);

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
    }
}
