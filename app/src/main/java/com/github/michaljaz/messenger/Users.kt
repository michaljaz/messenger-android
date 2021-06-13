package com.github.michaljaz.messenger

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import kotlin.reflect.typeOf

class Users : Fragment() {
    private lateinit var mactivity: MainActivity
    private lateinit var auth: FirebaseAuth
    private lateinit var db: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.users, container, false)

        mactivity=(activity as MainActivity)
        auth=mactivity.getFirebase()
        db=mactivity.getFirebaseDatabase()

        mactivity.supportActionBar?.title = "Users"

        val list = view.findViewById<ListView>(R.id.list)

        db.child("users").get().addOnSuccessListener {
            try{
                val array = ArrayList<String>()
                for(ds in it.children) {
                    array.add(ds.child("displayName").value.toString())
                }
                list.adapter = ArrayAdapter<String>(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    array
                )
            }catch(e:Exception){}

        }
        return view
    }
}
