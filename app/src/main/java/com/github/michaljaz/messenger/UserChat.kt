package com.github.michaljaz.messenger

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController


class UserChat : Fragment() {
    private lateinit var m: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.userchat, container, false)
        m = activity as MainActivity

        //not allow to go back
        m.allowBack=true

        //disable drawer
        m.disableDrawer()

        //add arrow to toolbar
        m.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        m.supportActionBar!!.setDisplayShowHomeEnabled(true)

        //set toolbar title user
        m.setToolbarTitle(m.chatWith)

        //arrow click listener
        m.toolbar.setNavigationOnClickListener {
            try{
                findNavController().navigate(R.id.userchat_off)
            }catch(e:Exception){}
        }

        return view
    }
}
