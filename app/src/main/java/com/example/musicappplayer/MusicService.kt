package com.example.musicappplayer

import android.annotation.SuppressLint
import android.app.Application
import android.app.PendingIntent
import android.app.Service
import android.app.appsearch.AppSearchSession
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import java.util.concurrent.TimeUnit

class MusicService: Service (){
        private var myBinder= MyBinder()
        private lateinit var mediaSession: MediaSessionCompat
        var mediaPlayer:MediaPlayer?= null
        private lateinit var   runnable: Runnable

        override fun onBind(intent: Intent?): IBinder? {
            mediaSession =MediaSessionCompat(baseContext,"My music")
            return myBinder
        }
        inner class MyBinder:Binder(){
            fun currentService():MusicService{
                return this@MusicService
            }
        }



        @SuppressLint("ForegroundServiceType")
        fun showNotification(playPauseBtn: Int){

            val prevIntent =Intent(baseContext,NotificationReceiver::class.java).setAction(ApplicationClass.PREVIOUS)
            val prePendingIntent = PendingIntent.getBroadcast(baseContext,0,prevIntent,PendingIntent.FLAG_UPDATE_CURRENT)

            val playIntent =Intent(baseContext,NotificationReceiver::class.java).setAction(ApplicationClass.PLAY)
            val playPendingIntent = PendingIntent.getBroadcast(baseContext,0,playIntent,PendingIntent.FLAG_UPDATE_CURRENT)

            val nextIntent =Intent(baseContext,NotificationReceiver::class.java).setAction(ApplicationClass.NEXT)
            val nextPendingIntent = PendingIntent.getBroadcast(baseContext,0,nextIntent,PendingIntent.FLAG_UPDATE_CURRENT)

            val exitIntent =Intent(baseContext,NotificationReceiver::class.java).setAction(ApplicationClass.EXIT)
            val exitPendingIntent = PendingIntent.getBroadcast(baseContext,0,exitIntent,PendingIntent.FLAG_UPDATE_CURRENT)

            val imgArt = getResourceIdFromName(this, PlayerActivity.musicListPA[PlayerActivity.songPosition].image)


            val notification=NotificationCompat.Builder(baseContext,ApplicationClass.CHANNEL_ID)
                .setContentTitle((PlayerActivity.musicListPA[PlayerActivity.songPosition].title))
                .setContentText((PlayerActivity.musicListPA[PlayerActivity.songPosition].artist))
                .setSmallIcon(R.drawable.playlist_icon)
                .setLargeIcon(BitmapFactory.decodeResource(resources,imgArt))
                .setStyle(androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(1))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOnlyAlertOnce(true)
                .addAction(R.drawable.previous_noti_icon,"Previous",prePendingIntent)
                .addAction(playPauseBtn,"Play",playPendingIntent)
                .addAction(R.drawable.next_noti_icon,"Next",nextPendingIntent)
                .addAction(R.drawable.exit_icon,"Exit",exitPendingIntent)
                .build()

            startForeground(13,notification)
        }
        override fun onDestroy() {
            super.onDestroy()
            mediaPlayer?.release()
            mediaPlayer = null
            mediaSession.release()
        }
        fun getResourceIdFromName(context: Context, resourceName: String): Int {
            return context.resources.getIdentifier(resourceName, "drawable", context.packageName)
        }
        fun createMediaPlayer(){
            if (PlayerActivity.musicService!!.mediaPlayer == null) {
                PlayerActivity.musicService!!.mediaPlayer = MediaPlayer()
            }
            PlayerActivity.musicService!!.mediaPlayer!!.reset()

            if (PlayerActivity.musicListPA.isNotEmpty()) {
                // Lấy tên tệp đầu tiên trong danh sách
                val musicName = PlayerActivity.musicListPA[PlayerActivity.songPosition].image
                val resId = resources.getIdentifier(musicName, "raw", packageName)
                if (resId != 0) {
                    val uri = Uri.parse("android.resource://$packageName/$resId")
                    PlayerActivity.musicService!!.mediaPlayer!!.setDataSource(this, uri)
                    PlayerActivity.musicService!!.mediaPlayer!!.prepare()
                    PlayerActivity.musicService!!.mediaPlayer!!.start()
                    PlayerActivity.isPlaying =true
                    PlayerActivity.binding.playPauseBtn.setIconResource(R.drawable.pause_icon)
                    PlayerActivity.musicService!!.showNotification(R.drawable.pause_icon)
                    PlayerActivity.binding.tvSeekbarStart.text= formatDuration(PlayerActivity.musicService!!.mediaPlayer!!.currentPosition.toLong())
                    PlayerActivity.binding.tvSeekbarEnd.text = formatDuration(PlayerActivity.musicListPA[PlayerActivity.songPosition].duration.toLong())
                    PlayerActivity.binding.seekBarPA.progress=0
                    PlayerActivity.binding.seekBarPA.max= PlayerActivity.musicService!!.mediaPlayer!!.duration
                    PlayerActivity.nowPlayingId = PlayerActivity.musicListPA[PlayerActivity.songPosition].id
                } else {
                    // Xử lý khi không tìm thấy tài nguyên trong res/raw
                    Log.e("MediaPlayer", "Resource not found for music name: $musicName")
                }
            }
            else {
                // Xử lý khi danh sách âm nhạc rỗng
                Log.e("MediaPlayer", "Music list is empty")
            }

        }
    private fun formatDuration(duration: Long): String {
        val minutes = TimeUnit.MINUTES.convert(duration,TimeUnit.MILLISECONDS)
        val seconds = TimeUnit.SECONDS.convert(duration,TimeUnit.MILLISECONDS)-minutes*TimeUnit.SECONDS.convert(1,TimeUnit.MINUTES)
        return String.format("%02d:%02d", minutes, seconds)
    }
    fun seekBarSetup(){
        runnable = Runnable {
            PlayerActivity.binding.tvSeekbarStart.text=formatDuration(mediaPlayer!!.currentPosition.toLong())
            PlayerActivity.binding.seekBarPA.progress=mediaPlayer!!.currentPosition
            Handler(Looper.getMainLooper()).postDelayed(runnable,200)
        }
        Handler(Looper.getMainLooper()).postDelayed(runnable,0)
    }

}