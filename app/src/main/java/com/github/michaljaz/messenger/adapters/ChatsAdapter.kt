package com.github.michaljaz.messenger.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.github.michaljaz.messenger.R
import com.github.michaljaz.messenger.utils.RoundedTransformation
import com.squareup.picasso.Picasso


class ChatsAdapter(
    private val context: Context,
    private val displayNames: ArrayList<String>,
    private val photoUrls: ArrayList<String>,
    private val userIds: ArrayList<String>,
    private val lastMessages: ArrayList<String>
) : BaseAdapter() {

    override fun getCount(): Int {
        return displayNames.size
    }

    override fun getItem(arg0: Int): ArrayList<String> {
        val xd= ArrayList<String>()
        xd.add(userIds[arg0])
        xd.add(displayNames[arg0])
        xd.add(photoUrls[arg0])
        return xd
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val row: View = inflater.inflate(R.layout.row_chat, parent, false)
        val i1: ImageView = row.findViewById(R.id.imgIcon) as ImageView
        row.findViewById<TextView>(R.id.DisplayName).text=displayNames[position]
        row.findViewById<TextView>(R.id.LastMessage).text=lastMessages[position]
        val image=photoUrls[position]
        if(image=="default"){
            i1.setImageResource(R.drawable.ic_profile_user)
        }else{
            Picasso
                .get()
                .load(image)
                .transform(RoundedTransformation(100, 0))
                .into(i1)
        }
        return row
    }

}