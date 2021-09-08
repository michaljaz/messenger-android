package com.github.michaljaz.messenger.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.github.michaljaz.messenger.R
import com.github.michaljaz.messenger.activities.MainActivity
import com.google.android.material.appbar.AppBarLayout


class ChatSettingsFragment : Fragment() {
    private lateinit var m: MainActivity
    lateinit var appbar: AppBarLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_chat_settings, container, false)
        m = activity as MainActivity

        //setup appbar
        appbar=view.findViewById(R.id.appBar)

        view.findViewById<ImageView>(R.id.backIcon).setOnClickListener {
            findNavController().navigate(R.id.chatSettings_off)
        }

        //Enable drawers
        m.enableDrawer()

        return view
    }
}
