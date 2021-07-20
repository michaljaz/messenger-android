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
import com.github.michaljaz.messenger.utils.setIconUrl
import com.squareup.picasso.Picasso

class User(
    val displayName: String,
    val photoUrl: String,
    val userId:String)

class UsersAdapter(
    private val context: Context,
    private val mUsers:ArrayList<User>
) : BaseAdapter() {

    override fun getCount(): Int {
        return mUsers.size
    }

    override fun getItem(position: Int): User{
        return mUsers[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val row: View = inflater.inflate(R.layout.row_user, parent, false)
        val i1: ImageView = row.findViewById(R.id.imgIcon) as ImageView
        val title: TextView = row.findViewById(R.id.txtTitle)
        title.text = mUsers[position].displayName
        val image=mUsers[position].photoUrl
        i1.setIconUrl(image)
        return row
    }

}