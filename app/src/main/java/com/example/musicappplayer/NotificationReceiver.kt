package com.example.musicappplayer

import Model.exitApplication
import Model.setSongPosition
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

class NotificationReceiver :BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            ApplicationClass.PREVIOUS -> prevNextSong(increment = false,context!!)
            ApplicationClass.PLAY -> if(PlayerActivity.isPlaying) {
                pauseMusic()
                } else {
                    playMusic()
            }
            ApplicationClass.NEXT -> prevNextSong(increment = true,context!!)
            ApplicationClass.EXIT ->{
                exitApplication()
            }
        }
    }
    private fun playMusic(){
        PlayerActivity.binding.playPauseBtn.setIconResource(R.drawable.pause_icon)
        PlayerActivity.musicService!!.showNotification(R.drawable.pause_icon)
        PlayerActivity.isPlaying=true
        PlayerActivity.musicService!!.mediaPlayer!!.start()
        NowPlaying.binding.playBtnNP.setIconResource(R.drawable.pause_icon)

    }
    private fun pauseMusic(){
        PlayerActivity.binding.playPauseBtn.setIconResource(R.drawable.play_icon)
        PlayerActivity.musicService!!.showNotification(R.drawable.play_icon)
        PlayerActivity.isPlaying=false
        PlayerActivity.musicService!!.mediaPlayer!!.pause()
        NowPlaying.binding.playBtnNP.setIconResource(R.drawable.play_icon)

    }
    private fun prevNextSong(increment: Boolean,context: Context){
        setSongPosition(increment=increment)
       PlayerActivity.musicService!!.createMediaPlayer()
        Glide.with(context)
            .load(getResourceIdFromName(context, PlayerActivity.musicListPA[PlayerActivity.songPosition].image)) // Assuming image is a URL or file path
            .apply(RequestOptions().placeholder(R.drawable.blinding_lights).centerCrop())
            .into(PlayerActivity.binding.songImg)
        PlayerActivity.binding.songName.text = PlayerActivity.musicListPA[PlayerActivity.songPosition].title
        Glide.with(context)
            .load(getResourceIdFromName(context, PlayerActivity.musicListPA[PlayerActivity.songPosition].image)) // Assuming image is a URL or file path
            .apply(RequestOptions().placeholder(R.drawable.blinding_lights).centerCrop())
            .into(NowPlaying.binding.songImgNP)
        NowPlaying.binding.songNameNP.text = PlayerActivity.musicListPA[PlayerActivity.songPosition].title

        PlayerActivity.binding.tvSeekbarEnd.text = formatDuration(PlayerActivity.musicListPA[PlayerActivity.songPosition].duration.toLong())
        playMusic()

    }

    fun formatDuration(duration: Long): String {
        val minutes = TimeUnit.SECONDS.toMinutes(duration)
        val seconds = duration - minutes * TimeUnit.MINUTES.toSeconds(1)
        return String.format("%02d:%02d", minutes, seconds)
    }
    fun getResourceIdFromName(context: Context, resourceName: String): Int {
        return context.resources.getIdentifier(resourceName, "drawable", context.packageName)
    }
}