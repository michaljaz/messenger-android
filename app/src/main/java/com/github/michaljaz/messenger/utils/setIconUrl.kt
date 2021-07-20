package com.github.michaljaz.messenger.utils

import android.widget.ImageView
import com.github.michaljaz.messenger.R
import com.squareup.picasso.Picasso

fun ImageView.setIconUrl(url:String) {
    if(url=="default"){
        setImageResource(R.drawable.ic_profile_user)
    }else{
        Picasso
            .get()
            .load(url)
            .transform(RoundedTransformation(100, 0))
            .into(this)
    }
}