package com.github.michaljaz.messenger

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference

class HomeFragment : Fragment() {
    private lateinit var mactivity: MainActivity

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        mactivity = activity as MainActivity
        setHasOptionsMenu(true)
        mactivity.enableDrawer()

        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu);

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        childFragmentManager
            .beginTransaction()
            .replace(R.id.frame_layout,Chat())
            .setTransition(FragmentTransaction.TRANSIT_NONE)
            .commit()
        view.findViewById<BottomNavigationView>(R.id.bottom_navigation).setOnNavigationItemSelectedListener { item ->
            childFragmentManager
            when(item.itemId){
                R.id.page_1 -> {
                    childFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame_layout,Chat())
                        .setTransition(FragmentTransaction.TRANSIT_NONE)
                        .commit()
                }
                R.id.page_2 -> {
                    childFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame_layout,Users())
                        .setTransition(FragmentTransaction.TRANSIT_NONE)
                        .commit()
                }
            }
            true
        }
    }
}
