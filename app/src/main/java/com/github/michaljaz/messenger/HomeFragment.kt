package com.github.michaljaz.messenger

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.messaging.FirebaseMessaging


class HomeFragment : Fragment() {
    private lateinit var m: MainActivity

    fun logout() {
        try{
            findNavController().navigate(R.id.logout)
        }catch(e:Exception){}
    }
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.home_fragment, container, false)
        m = activity as MainActivity
        setHasOptionsMenu(true)

        //Enable drawer
        m.enableDrawer()
        m.sessionHelper(this)

        //Update user data in firebase
        val userdb = m.db.child("usersData").child(m.auth.currentUser!!.uid)
        m.db.child("users").child(m.auth.currentUser!!.uid).setValue(true)
        val profile=m.auth.currentUser!!.providerData[1]
        userdb.child("email").setValue(profile.email.toString())
        userdb.child("providerId").setValue(profile.providerId)
        userdb.child("displayName").setValue(m.auth.currentUser!!.providerData[0].displayName.toString())
        FirebaseMessaging.getInstance().token.addOnCompleteListener OnCompleteListener@{ task ->
            if (!task.isSuccessful) {
                Log.w("XD", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            val token = task.result
            userdb.child("fcm_token").setValue(token.toString())
        }
        val photoUrl = if(profile.providerId=="google"){
            profile.photoUrl.toString()
        }else{
            m.auth.currentUser!!.providerData[0].photoUrl.toString()
        }
        userdb.child("photoUrl").setValue(photoUrl)

        //Update drawer header
        m.updateHeader(
            m.auth.currentUser!!.providerData[0].displayName.toString(),
            profile.email.toString(),
            photoUrl
        )

        //Bottom navigation bar control
        childFragmentManager
            .beginTransaction()
            .replace(R.id.frame_layout,Chat())
            .setTransition(FragmentTransaction.TRANSIT_NONE)
            .commit()
        var prev=1
        view.findViewById<BottomNavigationView>(R.id.bottom_navigation).setOnNavigationItemSelectedListener { item ->
            when(item.itemId){
                R.id.page_1 -> {
                    if(prev!=1){
                        childFragmentManager
                            .beginTransaction()
                            .replace(R.id.frame_layout,Chat())
                            .commit()
                        prev=1
                    }

                }
                R.id.page_2 -> {
                    if(prev!=2){
                        childFragmentManager
                            .beginTransaction()
                            .replace(R.id.frame_layout,Users())
                            .commit()
                        prev=2
                    }
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
