package com.github.michaljaz.messenger.fragments

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.Image
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.findNavController
import com.github.michaljaz.messenger.R
import com.github.michaljaz.messenger.activities.MainActivity
import com.github.michaljaz.messenger.fragments.frames.ChatsFrame
import com.github.michaljaz.messenger.fragments.frames.UsersFrame
import com.github.michaljaz.messenger.utils.RoundedTransformation
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.messaging.FirebaseMessaging
import com.squareup.picasso.Picasso


class HomeFragment : Fragment() {
    private lateinit var m: MainActivity
    lateinit var appbar: AppBarLayout

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

        //setup appbar
        appbar=view.findViewById(R.id.appBar)

        //Enable drawers
        m.enableDrawer()
        m.sessionHelper(this)
        appbar.findViewById<ImageView>(R.id.userIcon).setOnClickListener {
            m.mNavDrawer.openDrawer(GravityCompat.START)
        }

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


        //update user icon
        val userIcon=view.findViewById<ImageView>(R.id.userIcon)
        if(photoUrl=="default"){
            userIcon.setImageResource(R.drawable.ic_profile_user)
        }else{
            Picasso.get()
                .load(photoUrl)
                .transform(RoundedTransformation(100, 0))
                .into(userIcon)
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
}
