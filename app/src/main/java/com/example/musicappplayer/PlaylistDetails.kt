package com.example.musicappplayer

import Helper.MusicDatabaseHelper
import Model.Music
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicappplayer.databinding.ActivityPlaylistDetailsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlin.system.exitProcess

class PlaylistDetails : AppCompatActivity() {
    lateinit var musicDatabaseHelper: MusicDatabaseHelper
    companion object{
        var currentPlaylistPos:Int =-1
        lateinit var songListPlayList: ArrayList<Music>
        lateinit var adapter: MusicAdapter
        var songPosition=0
        lateinit var binding: ActivityPlaylistDetailsBinding


    }
    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setTheme(R.style.coolPink)
            binding =  ActivityPlaylistDetailsBinding.inflate(layoutInflater)
            setContentView(binding.root)
//        currentPlaylistPos=intent.extras?.get("Index") as Int
        binding.playlistDetailsRV.setItemViewCacheSize(10)
        binding.playlistDetailsRV.setHasFixedSize(true)
        binding.playlistDetailsRV.layoutManager=LinearLayoutManager(this)


        songPosition =intent.getIntExtra("index",0)

        musicDatabaseHelper = MusicDatabaseHelper(this)
        songListPlayList = musicDatabaseHelper.getSongsInPlaylist(PlaylistActivity.musicPlaylist[songPosition].id)

        adapter = MusicAdapter(this, songListPlayList,playlistDetails = true)
        binding.playlistDetailsRV.adapter=adapter

        setLayoutPlaylist()



//        binding.playlistDetailsRV.setOnClickListener{
//            val intent = Intent(this, PlayerActivity::class.java)
//            intent.putExtra("index",0)
//            intent.putExtra("class","PlaylistDetailsAdapter")
//            startActivity(intent)
//
//        }
       binding.shuffleBtnPD.setOnClickListener{
           val intent = Intent(this, PlayerActivity::class.java)
           intent.putExtra("index",0)
           intent.putExtra("class","PlaylistDetailsShuffle")
           startActivity(intent)
       }

        binding.backBtnPD.setOnClickListener{startActivity(Intent(this,PlaylistActivity::class.java))}
        binding.addBtnDT.setOnClickListener{
            startActivity(Intent(this, SelectionActivity::class.java))
        }
        binding.removeALlBtnPD.setOnClickListener{
            val builder = MaterialAlertDialogBuilder(this)
            builder.setTitle("Exit")
                .setMessage("Do you want remove all songs from playlist?")
                .setPositiveButton("Yes"){dialog, _ ->
                    musicDatabaseHelper.deleteAllSongsFromPlaylist(PlaylistActivity.musicPlaylist[songPosition].id)
                    val intent = Intent(this, PlaylistDetails::class.java)
                    intent.putExtra("index", songPosition)
                    startActivity(intent)
                    Toast.makeText(this,"Deleted all songs from playlist successfully!",Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
                .setNegativeButton("No"){dialog, _ ->
                    dialog.dismiss()
                }
            val customDialog = builder.create()
            customDialog.show()
            customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
            customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
        }


        }
    override fun onResume() {
        super.onResume()
//        binding.playListNamePD.text=PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].name
    }
    fun setLayoutPlaylist(){

        Glide.with(this)
            .load(getResourceIdFromName(PlaylistActivity.musicPlaylist[songPosition].image)) // Assuming image is a URL or file path
            .apply(RequestOptions().placeholder(R.drawable.blinding_lights).centerCrop())
            .into(binding.playlistImgPD)
        binding.moreInfo.text = PlaylistActivity.musicPlaylist[songPosition].name.uppercase() + "\n\nCreate on:  " +
                                PlaylistActivity.musicPlaylist[songPosition].create
        binding.totalSong.text="Total songs: "+ songListPlayList.size
    }
    fun getResourceIdFromName(resourceName: String): Int {
        return resources.getIdentifier(resourceName, "drawable", packageName)
    }

}