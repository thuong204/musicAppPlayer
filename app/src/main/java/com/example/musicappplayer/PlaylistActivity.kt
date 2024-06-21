package com.example.musicappplayer

import Helper.MusicDatabaseHelper
import Model.Music
import Model.Playlist
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.musicappplayer.databinding.ActivityPlaylistBinding
import com.example.musicappplayer.databinding.AddPlaylistDialogBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PlaylistActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlaylistBinding
    private lateinit var playlistAdapter: PlaylistViewAdapter
    private lateinit var musicDatabaseHelper: MusicDatabaseHelper
    companion object{
        lateinit var MusicListMA: ArrayList<Music>
        lateinit var musicPlaylist: ArrayList<Playlist>
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaylistBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTheme(R.style.Theme_MusicAppPlayer)

        musicDatabaseHelper = MusicDatabaseHelper(this)
        musicPlaylist =musicDatabaseHelper.getAllPlaylist()
        binding.backBtnPla.setOnClickListener{startActivity(Intent(this,MainActivity::class.java))}

        binding.playlistRV.setHasFixedSize(true)
        binding.playlistRV.setItemViewCacheSize(13)
        binding.playlistRV.layoutManager= GridLayoutManager(this@PlaylistActivity,2)
        playlistAdapter = PlaylistViewAdapter(this@PlaylistActivity, musicPlaylist)
        binding.playlistRV.adapter= playlistAdapter
    }
    private fun customAlertDialog(){
        val customDialog = LayoutInflater.from(this@PlaylistActivity).inflate(R.layout.add_playlist_dialog,binding.root,false)
        val binder = AddPlaylistDialogBinding.bind(customDialog)
        val buider = MaterialAlertDialogBuilder(this)
        buider.setView(customDialog)
            .setTitle("Playlist Details")
            .setPositiveButton("ADD"){
                dialog, _ ->
                val playListName = binder.playListName.text
                val creatyBy =binder.yourname.text
                if(playListName !=null && creatyBy !=null){
                    if(playListName.isNotEmpty() && creatyBy.isNotEmpty()){
                        addPlayList(playListName.toString(),creatyBy.toString())
                    }

            }
                dialog.dismiss()
            }.show()
    }

    private fun addPlayList(anme: String, createBy: String) {

    }

    override fun onResume() {
        super.onResume()
        playlistAdapter.notifyDataSetChanged()
    }
}