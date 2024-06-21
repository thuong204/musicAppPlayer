package com.example.musicappplayer

import Helper.MusicDatabaseHelper
import Model.Music
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicappplayer.databinding.MusicViewBinding
import java.util.concurrent.TimeUnit

class MusicAdapter(private val context: Context, private var musicList: ArrayList<Music>,private val playlistDetails: Boolean=false, private val selectionActivity: Boolean=false):RecyclerView.Adapter<MusicAdapter.MyHolder> (){
    private lateinit var musicDatabaseHelper: MusicDatabaseHelper
    class   MyHolder(binding:MusicViewBinding):RecyclerView.ViewHolder(binding.root){
        val title = binding.songNameMV
        val album = binding.songAlbumMV
        val image = binding.imageMV
        val duration = binding.songDuration
        val root = binding.root


    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicAdapter.MyHolder {
        return MyHolder(MusicViewBinding.inflate(LayoutInflater.from(context),parent,false))

    }

        override fun onBindViewHolder(holder: MusicAdapter.MyHolder, position: Int) {
            musicDatabaseHelper=MusicDatabaseHelper(context)
            val music = musicList[position]
            holder.title.text=music.title
            holder.album.text=music.album
            holder.duration.text= formatDuration(music.duration.toLong())
            Glide.with(holder.itemView.context)
                .load(getResourceIdFromName(holder.itemView.context, music.image)) // Assuming image is a URL or file path
                .apply(RequestOptions().placeholder(R.drawable.blinding_lights).centerCrop())
                .into(holder.image)
                when{
                    playlistDetails-> {
                        holder.root.setOnClickListener {
                            sendIntent(ref = "PlaylistDetailsAdapter", pos = position)
                        }
                    }
                    selectionActivity->{

                        holder.root.setOnClickListener{
                            if(!musicDatabaseHelper.checkSongInPlaylist(musicList[position].id,PlaylistActivity.musicPlaylist[PlaylistDetails.songPosition].id)){
                            musicDatabaseHelper.addSongToPlaylist(musicList[position].id , PlaylistActivity.musicPlaylist[PlaylistDetails.songPosition].id)
                            holder.root.setBackgroundColor(ContextCompat.getColor(context,R.color.cool_pink))}
                        else{
                            holder.root.setBackgroundColor(ContextCompat.getColor(context,R.color.cool_blue))
                        }
                        }
                    }

                    else-> {
                        holder.root.setOnClickListener {
                            when {
                                MainActivity.search -> sendIntent(
                                    ref = "MusicAdapterSearch",
                                    pos = position
                                )

                                musicList[position].id == PlayerActivity.nowPlayingId ->
                                    sendIntent(
                                        ref = "NowPlaying",
                                        pos = PlayerActivity.songPosition
                                    )

                                else -> sendIntent(ref = "MusicAdapter", pos = position)
                            }
                        }
                    }

            }

    }


    override fun getItemCount(): Int {
        return musicList.size
    }
    fun refreshData(newNotes: ArrayList<Music>){
        musicList    = newNotes
        notifyDataSetChanged()
    }
    fun getResourceIdFromName(context: Context, resourceName: String): Int {
        return context.resources.getIdentifier(resourceName, "drawable", context.packageName)
    }
    fun formatDuration(duration: Long): String {
        val minutes = TimeUnit.SECONDS.toMinutes(duration)
        val seconds = duration - minutes * TimeUnit.MINUTES.toSeconds(1)
        return String.format("%02d:%02d", minutes, seconds)
    }
    fun updateMusicList(searchList: ArrayList<Music>){
        musicList= ArrayList()
        musicList.addAll(searchList)
        notifyDataSetChanged()

    }
    fun sendIntent(ref:String, pos: Int){
        val intent = Intent(context,PlayerActivity::class.java)
        intent.putExtra("index",pos)
        intent.putExtra("class",ref)
        ContextCompat.startActivity(context,intent,null)
    }
    private fun addSong(song:Music) :Boolean{

        return true
    }
}