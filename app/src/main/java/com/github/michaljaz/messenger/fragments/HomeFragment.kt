package com.github.michaljaz.messenger.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.findNavController
import com.github.michaljaz.messenger.R
import com.github.michaljaz.messenger.activities.MainActivity
import com.github.michaljaz.messenger.fragments.frames.ChatsFrame
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
        val user=m.auth.currentUser!!
        val profile=user.providerData[1]

        val photoUrl = if(profile.providerId=="google"){
            profile.photoUrl.toString()
        }else{
            user.providerData[0].photoUrl.toString()
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("XD", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            val childUpdates = hashMapOf(
                "/usersData/${user.uid}/email" to profile.email.toString(),
                "/usersData/${user.uid}/providerId" to profile.providerId,
                "/usersData/${user.uid}/displayName" to user.providerData[0].displayName.toString(),
                "/usersData/${user.uid}/photoUrl" to photoUrl,
                "/usersData/${user.uid}/fcm_token" to task.result,
                "/users/${user.uid}" to true
            )
            m.db.updateChildren(childUpdates as Map<String, Any>)
        }

        //fix size
        for (i in 0 until m.toolbar.childCount) {
            val it=m.toolbar.getChildAt(i)
//            if (it is ImageButton) {
//                it.scaleX = 0.75f
//                it.scaleY = 0.75f
//            }
            if (it is TextView){
                it.textSize = 25F
            }
        }

        //Update drawer header
        m.updateHeader(
            user.providerData[0].displayName.toString(),
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
        m.invalidateOptionsMenu()
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
