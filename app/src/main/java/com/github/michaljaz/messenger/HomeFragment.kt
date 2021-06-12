package com.github.michaljaz.messenger

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView
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

        auth=mactivity.getFirebase()
        db = mactivity.getFirebaseDatabase()
        if(mactivity.isOnline()){
            db.child("users").child(auth.currentUser!!.uid).child("email").setValue(auth.currentUser!!.email)
            auth.currentUser?.let {
                for (profile in it.providerData) {
                    // Id of the provider (ex: google.com)
                    val providerId = profile.providerId

                    // UID specific to the provider
                    val uid = profile.uid

                    // Name, email address, and profile photo Url
                    val name = profile.displayName
                    val email = profile.email
                    val photoUrl = profile.photoUrl
                    Log.d("x", providerId)
                    Log.d("x", uid)
                    Log.d("x", name.toString())
                    Log.d("x", email.toString())
                    Log.d("x",photoUrl.toString())
                }
            }
//            db.child("users").child(auth.currentUser!!.uid).child("provider").setValue(auth.currentUser!!.providerData)
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
            Log.d("xd","clicked")
            true
        }
    }
}
