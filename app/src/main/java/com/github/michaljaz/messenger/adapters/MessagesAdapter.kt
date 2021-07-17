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

class Message(
    val text: String,
    val isMe: Boolean)

class MessagesAdapter (private val mMessages: ArrayList<Message>,private val friendPhotoUrl: String,private val friendName: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    inner class FriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val message:TextView=itemView.findViewById(R.id.message)
        val icon:ImageView=itemView.findViewById(R.id.imgIcon)
    }
    open inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val message:TextView=itemView.findViewById(R.id.message)
    }
    inner class IntroViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val icon:ImageView=itemView.findViewById(R.id.intro_icon)
        val name:TextView=itemView.findViewById(R.id.intro_name)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        return when (viewType) {
            0 -> {
                val v=inflater.inflate(R.layout.row_chat_intro, parent, false)
                IntroViewHolder(v)
            }
            1 -> {
                val v=inflater.inflate(R.layout.row_message_my, parent, false)
                MyViewHolder(v)
            }
            else -> {
                val v=inflater.inflate(R.layout.row_message_friend, parent, false)
                FriendViewHolder(v)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            position==0 -> { 0 }
            mMessages[position].isMe -> { 1 }
            else -> { 2 }
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        if(viewHolder is MyViewHolder) {
            val message: Message = mMessages[position]
            viewHolder.message.text=message.text

            val up=if(position==0){false}else{mMessages[position-1].isMe}
            val down=if(position==mMessages.lastIndex){false}else{mMessages[position+1].isMe}

            if(up && down){
                viewHolder.message.setBackgroundResource(R.drawable.my_bubble_shape_middle)
            }
            if(up && !down){
                viewHolder.message.setBackgroundResource(R.drawable.my_bubble_shape_up)
            }
            if(!up && down){
                viewHolder.message.setBackgroundResource(R.drawable.my_bubble_shape_down)
            }
            if(!up && !down){
                viewHolder.message.setBackgroundResource(R.drawable.my_bubble_shape)
            }
        }else if(viewHolder is FriendViewHolder){
            val message: Message = mMessages[position]
            viewHolder.message.text=message.text

            val up=if(position==1){false}else{!mMessages[position-1].isMe}
            val down=if(position==mMessages.lastIndex){false}else{!mMessages[position+1].isMe}

            if(up && down){
                viewHolder.message.setBackgroundResource(R.drawable.friend_bubble_shape_middle)
            }
            if(up && !down){
                viewHolder.message.setBackgroundResource(R.drawable.friend_bubble_shape_up)
            }
            if(!up && down){
                viewHolder.message.setBackgroundResource(R.drawable.friend_bubble_shape_down)
            }
            if(!up && !down){
                viewHolder.message.setBackgroundResource(R.drawable.friend_bubble_shape)
            }

            if(!up){
                if(friendPhotoUrl=="default"){
                    viewHolder.icon.setImageResource(R.drawable.ic_profile_user)
                }else{
                    Picasso
                        .get()
                        .load(friendPhotoUrl)
                        .transform(RoundedTransformation(100, 0))
                        .into(viewHolder.icon)
                }
            }else{
                viewHolder.icon.setImageResource(0)
            }
        }else if(viewHolder is IntroViewHolder){
            if(friendPhotoUrl=="default"){
                viewHolder.icon.setImageResource(R.drawable.ic_profile_user)
            }else{
                Picasso
                    .get()
                    .load(friendPhotoUrl)
                    .transform(RoundedTransformation(100, 0))
                    .into(viewHolder.icon)
            }
            viewHolder.name.text=friendName
        }

    }

    override fun getItemCount(): Int {
        return mMessages.size
    }
}