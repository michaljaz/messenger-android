package com.github.michaljaz.messenger

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment

class Users : Fragment() {
    private lateinit var m: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.users, container, false)
        m=(activity as MainActivity)

        //set Toolbar title
        m.setToolbarTitle("Users")

        //get list of users from firebase
        val list = view.findViewById<ListView>(R.id.list)
        try{
            m.db.child("users").get().addOnSuccessListener {

                val array = ArrayList<String>()
                val drawables = ArrayList<Int>()
                for(ds in it.children) {
                    m.db.child("usersData").child(ds.key.toString()).child("displayName").get().addOnSuccessListener { itx ->
                        array.add(itx.value.toString())
                        drawables.add(R.drawable.ic_profile_user)
                        list.adapter=CustomAdapter(requireContext(), array, drawables)
                    }
                }
            }
        }catch(e:Exception){ }
        return view
    }
}
