package com.github.michaljaz.messenger

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class Users : Fragment() {
    private lateinit var m: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.users, container, false)
        m=(activity as MainActivity)

        //not allow to go back
        m.allowBack=false

        //set Toolbar title
        m.setToolbarTitle("Users")

        //get list of users from firebase
        val list = view.findViewById<ListView>(R.id.list)
        m.db.child("users").get().addOnSuccessListener {
            val array = ArrayList<String>()
            val drawables = ArrayList<String>()
            for(ds in it.children) {
                m.db.child("usersData").child(ds.key.toString()).child("displayName").get().addOnSuccessListener { itx ->
                    m.db.child("usersData").child(ds.key.toString()).child("photoUrl").get().addOnSuccessListener { itx2 ->
                        array.add(itx.value.toString())
                        drawables.add(itx2.value.toString())
                        try{
                            list.adapter=CustomAdapter(requireContext(), array, drawables)
                        }catch(e:Exception){ }
                    }
                }
            }
        }


        view.findViewById<ListView>(R.id.list).setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position)
            Log.d("xd", selectedItem as String)
            try {
                findNavController().navigate(R.id.userChat_on)
            }catch(e:Exception){}
        }
        return view
    }
}
