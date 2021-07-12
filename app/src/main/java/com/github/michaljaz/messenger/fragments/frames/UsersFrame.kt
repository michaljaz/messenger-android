package com.github.michaljaz.messenger.fragments.frames

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.github.michaljaz.messenger.R
import com.github.michaljaz.messenger.activities.MainActivity
import com.github.michaljaz.messenger.adapters.User
import com.github.michaljaz.messenger.adapters.UsersAdapter
import com.github.michaljaz.messenger.fragments.HomeFragment

class UsersFrame : Fragment() {
    private lateinit var m: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.frame_users, container, false)
        m=(activity as MainActivity)

        val home=(parentFragment as HomeFragment)

        //not allow to go back
        m.allowBack=false

        //set Toolbar title
        home.appbar.findViewById<TextView>(R.id.toolbarTitle).text="Users"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            home.appbar.elevation = 8f
        }

        //get list of users from firebase
        val list = view.findViewById<ListView>(R.id.list)
        m.db.child("users").get().addOnSuccessListener {
            val users=ArrayList<User>()
            for(ds in it.children) {
                m.db.child("usersData").child(ds.key.toString()).child("displayName").get().addOnSuccessListener { itx ->
                    m.db.child("usersData").child(ds.key.toString()).child("photoUrl").get().addOnSuccessListener { itx2 ->
                        users.add(User(itx.value.toString(),itx2.value.toString(),ds.key.toString()))
                        try{
                            list.adapter= UsersAdapter(requireContext(), users)
                        }catch(e:Exception){ }
                    }
                }
            }
        }


        view.findViewById<ListView>(R.id.list).setOnItemClickListener { parent, _, position, _ ->
            val user = parent.getItemAtPosition(position) as User
            m.chatWithUid=user.userId
            m.chatWith=user.displayName
            m.chatWithPhoto=user.photoUrl
            try {
                findNavController().navigate(R.id.userChat_on)
            }catch(e:Exception){}
        }
        return view
    }
}
