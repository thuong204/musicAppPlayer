package com.example.musicappplayer

import Model.Playlist
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicappplayer.databinding.PlaylistViewBinding

class PlaylistViewAdapter(private val context: Context, private var playlistList: ArrayList<Playlist>):RecyclerView.Adapter<PlaylistViewAdapter.MyHolder> (){
    class   MyHolder(binding: PlaylistViewBinding):RecyclerView.ViewHolder(binding.root){
        val image =binding.playlistImg
        val name = binding.playlistName
        val root =binding.root
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(PlaylistViewBinding.inflate(LayoutInflater.from(context),parent,false))

    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.name.text=playlistList[position].name
        Glide.with(holder.itemView.context)
            .load(getResourceIdFromName(holder.itemView.context,playlistList[position].image)) // Assuming image is a URL or file path
            .apply(RequestOptions().placeholder(R.drawable.music_player).centerCrop())
            .into(holder.image)
        holder.name.isSelected=true
        holder.root.setOnClickListener{
            val intent  =   Intent(context,PlaylistDetails::class.java)
            intent.putExtra("index",position)
            ContextCompat.startActivity(context,intent,null)
        }
    }


    override fun getItemCount(): Int {
        return playlistList.size
    }
    fun getResourceIdFromName(context: Context, resourceName: String): Int {
        return context.resources.getIdentifier(resourceName, "drawable", context.packageName)
    }
}