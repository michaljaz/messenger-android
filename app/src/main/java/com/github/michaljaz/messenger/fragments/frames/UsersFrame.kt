package com.github.michaljaz.messenger.fragments.frames

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.github.michaljaz.messenger.R
import com.github.michaljaz.messenger.activities.MainActivity
import com.github.michaljaz.messenger.adapters.UsersAdapter

class UsersFrame : Fragment() {
    private lateinit var m: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.frame_users, container, false)
        m=(activity as MainActivity)

        //change menu
        m.home.changeMenu("users")

        //not allow to go back
        m.allowBack=false

        //set Toolbar title
        m.setToolbarTitle("Users")

        //get list of users from firebase
        val list = view.findViewById<ListView>(R.id.list)
        m.db.child("users").get().addOnSuccessListener {
            val displayNames = ArrayList<String>()
            val photoUrls = ArrayList<String>()
            val userIds = ArrayList<String>()
            for(ds in it.children) {
                m.db.child("usersData").child(ds.key.toString()).child("displayName").get().addOnSuccessListener { itx ->
                    m.db.child("usersData").child(ds.key.toString()).child("photoUrl").get().addOnSuccessListener { itx2 ->
                        displayNames.add(itx.value.toString())
                        photoUrls.add(itx2.value.toString())
                        userIds.add(ds.key.toString())
                        try{
                            list.adapter= UsersAdapter(requireContext(), displayNames, photoUrls, userIds)
                        }catch(e:Exception){ }
                    }
                }
            }
        }


        view.findViewById<ListView>(R.id.list).setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position) as ArrayList<String>
            m.chatWithUid=selectedItem[0]
            m.chatWith=selectedItem[1]
            m.chatWithPhoto=selectedItem[2]
            try {
                findNavController().navigate(R.id.userChat_on)
            }catch(e:Exception){}
        }
        return view
    }
}
