package com.github.michaljaz.messenger.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.github.michaljaz.messenger.R
import com.github.michaljaz.messenger.utils.RoundedTransformation
import com.squareup.picasso.Picasso


class MessagesAdapter(
    private val context: Context,
    private val texts: ArrayList<String>,
    private val isMe: ArrayList<Boolean>,
    private val friendPhotoUrl: String
) : BaseAdapter() {

    override fun getCount(): Int {
        return texts.size
    }

    override fun getItem(arg0: Int): Nothing? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val row: View
        if(isMe[position]){
            row = inflater.inflate(R.layout.row_message_my, parent, false)
            row.findViewById<TextView>(R.id.message).text=texts[position]
        }else{
            row = inflater.inflate(R.layout.row_message_friend, parent, false)
            row.findViewById<TextView>(R.id.message).text=texts[position]
            val i = row.findViewById<ImageView>(R.id.imgIcon)
            if(friendPhotoUrl=="default"){
                i.setImageResource(R.drawable.ic_profile_user)
            }else{
                Picasso
                    .get()
                    .load(friendPhotoUrl)
                    .transform(RoundedTransformation(100, 0))
                    .into(i)
            }

        }
        return row
    }

}