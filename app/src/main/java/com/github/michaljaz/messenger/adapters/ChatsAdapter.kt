package com.github.michaljaz.messenger.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.michaljaz.messenger.R
import com.github.michaljaz.messenger.utils.RoundedTransformation
import com.squareup.picasso.Picasso

class Chat(
    val displayName: String,
    val photoUrl: String,
    val userId: String,
    val lastMessage: String)

class ChatsAdapter (private val mChats: ArrayList<Chat>) : RecyclerView.Adapter<ChatsAdapter.ViewHolder>()
{
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val displayName: TextView = itemView.findViewById(R.id.DisplayName)
        val lastMessage: TextView = itemView.findViewById(R.id.LastMessage)
        val icon: ImageView = itemView.findViewById(R.id.imgIcon)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatsAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val chatView = inflater.inflate(R.layout.row_chat, parent, false)
        return ViewHolder(chatView)
    }

    override fun onBindViewHolder(viewHolder: ChatsAdapter.ViewHolder, position: Int) {
        val chat: Chat = mChats[position]
        viewHolder.displayName.text=chat.displayName
        viewHolder.lastMessage.text=chat.lastMessage
        if(chat.photoUrl=="default"){
            viewHolder.icon.setImageResource(R.drawable.ic_profile_user)
        }else{
            Picasso
                .get()
                .load(chat.photoUrl)
                .transform(RoundedTransformation(100, 0))
                .into(viewHolder.icon)
        }
    }

    override fun getItemCount(): Int {
        return mChats.size
    }
}