package com.github.michaljaz.messenger

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.messaging.FirebaseMessaging


class HomeFragment : Fragment() {
    private lateinit var mactivity: MainActivity
    private lateinit var auth: FirebaseAuth
    private lateinit var db: DatabaseReference

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
        mactivity = activity as MainActivity
        auth = mactivity.getFirebase()
        db = mactivity.getFirebaseDatabase()
        setHasOptionsMenu(true)
        mactivity.enableDrawer()
        mactivity.sessionHelper(this)
        val userdb = db.child("usersData").child(auth.currentUser!!.uid)
        db.child("users").child(auth.currentUser!!.uid).setValue(true)

        val profile=auth.currentUser!!.providerData[1]
        userdb.child("email").setValue(profile.email.toString())
        userdb.child("providerId").setValue(profile.providerId)
        userdb.child("displayName").setValue(auth.currentUser!!.providerData[0].displayName.toString())
        FirebaseMessaging.getInstance().token.addOnCompleteListener OnCompleteListener@{ task ->
            if (!task.isSuccessful) {
                Log.w("XD", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            val token = task.result
            userdb.child("fcm_token").setValue(token.toString())
        }
        var photoUrl=""
        if(profile.providerId=="google"){
            photoUrl=profile.photoUrl.toString()
        }else{
            photoUrl=auth.currentUser!!.providerData[0].photoUrl.toString()
        }
        userdb.child("photoUrl").setValue(photoUrl)
        mactivity.updateHeader(
            auth.currentUser!!.providerData[0].displayName.toString(),
            profile.email.toString(),
            photoUrl
        )


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
                            .setCustomAnimations(R.anim.slide_out_left, R.anim.slide_in_left)
                            .replace(R.id.frame_layout,Chat())
                            .commit()
                        prev=1
                    }

                }
                R.id.page_2 -> {
                    if(prev!=2){
                        childFragmentManager
                            .beginTransaction()
                            .setCustomAnimations(R.anim.slide_out_right, R.anim.slide_in_right)
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
