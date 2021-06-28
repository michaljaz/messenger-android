package com.github.michaljaz.messenger.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.findNavController
import com.github.michaljaz.messenger.fragments.frames.ChatsFrame
import com.github.michaljaz.messenger.activities.MainActivity
import com.github.michaljaz.messenger.R
import com.github.michaljaz.messenger.fragments.frames.UsersFrame
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.messaging.FirebaseMessaging


class HomeFragment : Fragment() {
    private lateinit var m: MainActivity
    private lateinit var menuString:String
    fun logout() {
        try{
            findNavController().navigate(R.id.logout)
        }catch(e:Exception){}
    }
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_home, container, false)
        m = activity as MainActivity
        m.home=this

        //enable options menu
        setHasOptionsMenu(true)

        //disable arrow
        m.supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        m.supportActionBar!!.setDisplayShowHomeEnabled(false)

        //Enable drawers
        m.enableDrawer()
        m.sessionHelper(this)
        m.toggle=ActionBarDrawerToggle(
            m,m.mNavDrawer,m.toolbar,
            R.string.app_name,
            R.string.nav_app_bar_open_drawer_description
        )
        m.mNavDrawer.addDrawerListener(m.toggle)
        m.toggle.syncState()

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
            .replace(R.id.frame_layout, ChatsFrame())
            .setTransition(FragmentTransaction.TRANSIT_NONE)
            .commit()
        var prev=1
        view.findViewById<BottomNavigationView>(R.id.bottom_navigation).setOnNavigationItemSelectedListener { item ->
            when(item.itemId){
                R.id.page_1 -> {
                    if(prev!=1){
                        childFragmentManager
                            .beginTransaction()
                            .replace(R.id.frame_layout, ChatsFrame())
                            .commit()
                        prev=1
                    }

                }
                R.id.page_2 -> {
                    if(prev!=2){
                        childFragmentManager
                            .beginTransaction()
                            .replace(R.id.frame_layout, UsersFrame())
                            .commit()
                        prev=2
                    }
                }
            }
            true
        }
        return view
    }

    fun changeMenu(menuS:String){
        menuString=menuS
        m.invalidateOptionsMenu();
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.clear()
        val inflater: MenuInflater = m.menuInflater
        if (menuString === "chats") {
            inflater.inflate(R.menu.chats, menu)
        } else if (menuString === "users") {
            inflater.inflate(R.menu.users, menu)
        }
    }
}