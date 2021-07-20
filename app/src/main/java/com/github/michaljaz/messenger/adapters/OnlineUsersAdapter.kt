package com.github.michaljaz.messenger.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.michaljaz.messenger.R
import com.github.michaljaz.messenger.utils.RoundedTransformation
import com.github.michaljaz.messenger.utils.setIconUrl
import com.squareup.picasso.Picasso

class OnlineUser(
    val displayName: String,
    val photoUrl: String)

class OnlineUsersAdapter (private val mOnlineUsers: ArrayList<OnlineUser>) : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val displayName: TextView = itemView.findViewById(R.id.DisplayName)
        val surName: TextView = itemView.findViewById(R.id.SurName)
        val icon: ImageView = itemView.findViewById(R.id.imgIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        return ViewHolder(inflater.inflate(R.layout.row_chats_online_user, parent, false))
    }
    override fun getItemViewType(position: Int): Int {
        return if(position==0){ 0 }else{ 1 }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        if(viewHolder is ViewHolder){
            val user: OnlineUser = mOnlineUsers[position]
            viewHolder.displayName.text=user.displayName
            viewHolder.surName.text="Surname"
            viewHolder.icon.setIconUrl(user.photoUrl)
        }
    }

    override fun getItemCount(): Int {
        return mOnlineUsers.size
    }
}