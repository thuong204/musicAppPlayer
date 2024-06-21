package com.example.musicappplayer

import Helper.MusicDatabaseHelper
import Model.Music
import Model.exitApplication
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.media.MediaPlayer
import android.media.audiofx.AudioEffect
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicappplayer.databinding.ActivityPlayerBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.IOException
import java.util.concurrent.TimeUnit

class PlayerActivity : AppCompatActivity(), ServiceConnection, MediaPlayer.OnCompletionListener {
//    private lateinit var binding: ActivityPlayerBinding
    private lateinit var dbHelper: MusicDatabaseHelper
    private lateinit var musicAdapter: MusicAdapter
    private lateinit var context:Context

    companion object{
        lateinit var musicListPA: ArrayList<Music>
        var songPosition:Int =0
        var isPlaying:Boolean = false;
        var musicService: MusicService?= null
        lateinit var binding: ActivityPlayerBinding
        var repeat: Boolean = false
        var min15:Boolean =false
        var min30:Boolean =false
        var min60:Boolean =false
        var nowPlayingId: Int = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setTheme(R.style.Theme_MusicAppPlayer)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initialLayout()
        binding.backBtnPA.setOnClickListener{
            finish()
        }
        binding.playPauseBtn.setOnClickListener {
            if (isPlaying) pauseMusic()
            else playMusic()

        }
        isFavourite(musicListPA[songPosition].id) //chonj icon tym phu hop


        binding.favouriteBtnPA.setOnClickListener {
            if (isFavourite(musicListPA[songPosition].id)==false) {
                saveFavouriteSong(musicListPA[songPosition].id)
                binding.favouriteBtnPA.setImageResource(R.drawable.favourite_empty_icon)
                Toast.makeText(this, "Song added to favorites", Toast.LENGTH_SHORT).show()
            } else {
                deleteFavouriteSong(musicListPA[songPosition].id)
                binding.favouriteBtnPA.setImageResource(R.drawable.favourite_icon_empty)
                Toast.makeText(this, "Song deleted to favorites", Toast.LENGTH_SHORT).show()


            }
        }
        binding.previousBtn.setOnClickListener{preNextSong(increment = false)}
        binding.nextBtn.setOnClickListener{preNextSong(increment = true)}
        binding.seekBarPA.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(fromUser) musicService!!.mediaPlayer!!.seekTo(progress)

            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) =Unit
            override fun onStopTrackingTouch(seekBar: SeekBar?) =Unit
        })

        binding.repeatBtnPA.setOnClickListener{
            if(!repeat){
                repeat=true
                binding.repeatBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.purple_500))
            }
            else{
                repeat=false
                binding.repeatBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.cool_pink))

            }
        }
        binding.equalizerBtnPA.setOnClickListener{
           try {
               val eqIntent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
               eqIntent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, musicService!!.mediaPlayer!!.audioSessionId)
               eqIntent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME,baseContext.packageName)
               eqIntent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE,AudioEffect.CONTENT_TYPE_MUSIC)
               startActivityForResult(eqIntent,13)
           }
           catch (e: Exception){Toast.makeText(this, "Equalizer Feature not Supported!!", Toast.LENGTH_SHORT).show()}

        }
        binding.timerBtnPA.setOnClickListener{
            val timer =min15 || min30 || min60
            if(!timer) showBottomSheetDialog()
            else{
               val builder = MaterialAlertDialogBuilder(this)
               builder.setTitle("Stop Timer")
                   .setMessage("Do you want to stop timer?")
                   .setPositiveButton("Yes"){_, _ ->
                       min15 = false
                       min30 = false
                       min60=false
                       binding.timerBtnPA.setColorFilter(ContextCompat.getColor(this,R.color.cool_pink))

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
        binding.shareBtnPA.setOnClickListener{
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type="audio/*"
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(musicListPA[songPosition].path))
            startActivity(Intent.createChooser(shareIntent,"Sharing Music File!"))
        }
    }

    private fun saveFavouriteSong(songId: Int) {
        dbHelper.saveFavoriteSong(songId)
    }
    private fun deleteFavouriteSong(songId: Int){
        dbHelper.deleteFavoriteSong(songId)
    }
    private fun isFavourite(songId: Int): Boolean{
        if(dbHelper.isFavoriteSong(songId)){
            binding.favouriteBtnPA.setImageResource(R.drawable.favourite_empty_icon)
            return true
        }
        else {
            binding.favouriteBtnPA.setImageResource(R.drawable.favourite_icon_empty)
            return false
        }
    }


    fun createMediaPlayer() {
        if (musicService!!.mediaPlayer == null) {
            musicService!!.mediaPlayer = MediaPlayer()
        }
        musicService!!.mediaPlayer!!.reset()

        val musicUri = "https://firebasestorage.googleapis.com/v0/b/login-register-20a46.appspot.com/o/mp3%2Fblinding_light.mp3?alt=media&token=bca86d40-f918-4e43-80b3-a9afc0d75fcb"

        try {
            musicService!!.mediaPlayer!!.setDataSource(musicUri)
            musicService!!.mediaPlayer!!.prepare()
            musicService!!.mediaPlayer!!.start()
            isPlaying=true
            Companion.binding.playPauseBtn.setIconResource(R.drawable.pause_icon)
            musicService!!.showNotification(R.drawable.pause_icon)
            Companion.binding.tvSeekbarStart.text=formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
            Companion.binding.tvSeekbarEnd.text = formatDuration(musicListPA[songPosition].duration.toLong())
            Companion.binding.seekBarPA.progress=0
            Companion.binding.seekBarPA.max =PlayerActivity!!.musicService!!.mediaPlayer!!.duration

            musicService!!.mediaPlayer!!.setOnCompletionListener(this)
            nowPlayingId= musicListPA[songPosition].id

        } catch (e: IOException) {
            // Xử lý lỗi nếu không thể thiết lập nguồn dữ liệu hoặc chuẩn bị MediaPlayer
            Toast.makeText(this,e.printStackTrace().toString() ,Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }
    private fun initialLayout(){
        dbHelper = MusicDatabaseHelper(this)
        musicListPA = dbHelper.getAllMusic()
        songPosition = intent.getIntExtra("index", 0)
        when (intent.getStringExtra("class")) {
            "NowPlaying"->{
                setLayout()
                binding.tvSeekbarStart.text=formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
                binding.tvSeekbarEnd.text = formatDuration(musicListPA[songPosition].duration.toLong())
                binding.seekBarPA.progress=0
                binding.seekBarPA.max =PlayerActivity!!.musicService!!.mediaPlayer!!.duration
                if(isPlaying) binding.playPauseBtn.setIconResource(R.drawable.pause_icon)
                else binding.playPauseBtn.setIconResource(R.drawable.play_icon)
            }
            "MusicAdapterSearch"->{
                val intent =Intent(this, MusicService::class.java)
                bindService(intent,this, Context.BIND_AUTO_CREATE)
                startService(intent)
                musicListPA = ArrayList()
                musicListPA.addAll(MainActivity.musicListMA)
                setLayout()
            }
            "MusicAdapter" -> {
                val intent =Intent(this, MusicService::class.java)
                bindService(intent,this, Context.BIND_AUTO_CREATE)
                startService(intent)
                musicListPA= ArrayList()
                musicListPA.addAll(MainActivity.musicListMA)
                setLayout()
            }
            "MainActivity" ->{
                val intent =Intent(this, MusicService::class.java)
                bindService(intent,this, Context.BIND_AUTO_CREATE)
                startService(intent)
                musicListPA= ArrayList()
                musicListPA.addAll(MainActivity.musicListMA)
                musicListPA.shuffle()
                setLayout()
//                createMediaPlayer()
            }
            "FavouriteShuffle"->{
                val intent =Intent(this, MusicService::class.java)
                bindService(intent,this, Context.BIND_AUTO_CREATE)
                startService(intent)
                musicListPA= ArrayList()
                musicListPA.addAll(FavouriteActivity.tempList)
                musicListPA.shuffle()
                setLayout()
            }
            "PlaylistDetailsAdapter"->{
                val intent =Intent(this, MusicService::class.java)
                bindService(intent,this, Context.BIND_AUTO_CREATE)
                startService(intent)
                musicListPA= ArrayList()
                musicListPA.addAll(PlaylistDetails.songListPlayList)
                setLayout()

            }
            "PlaylistDetailsShuffle"->{
                val intent =Intent(this, MusicService::class.java)
                bindService(intent,this, Context.BIND_AUTO_CREATE)
                startService(intent)
                musicListPA= ArrayList()
                musicListPA.addAll(PlaylistDetails.songListPlayList)
                musicListPA.shuffle()
                setLayout()

            }

        }
    }
    private fun playMusic(){
        binding.playPauseBtn.setIconResource(R.drawable.pause_icon)
        musicService!!.showNotification(R.drawable.pause_icon)
        isPlaying=true
        musicService!!. mediaPlayer!!.start()
    }
    private fun pauseMusic(){
        binding.playPauseBtn.setIconResource(R.drawable.play_icon)
        musicService!!.showNotification(R.drawable.play_icon)
        isPlaying=false
        musicService!!.mediaPlayer!!.pause()
    }
    private fun getResourceIdFromName(resourceName: String): Int {
        return resources.getIdentifier(resourceName, "drawable", packageName)
    }
    private fun setLayout() {
        Glide.with(this)
            .load(getResourceIdFromName(musicListPA[songPosition].image)) // Assuming image is a URL or file path
            .apply(RequestOptions().placeholder(R.drawable.blinding_lights).centerCrop())
            .into(binding.songImg)
        binding.songName.text = musicListPA[songPosition].title
        if(repeat) binding.repeatBtnPA.setColorFilter(ContextCompat.getColor(this,R.color.purple_500))
        if(min15 || min30 || min60) binding.timerBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.purple_500))
    }
    fun formatDuration(duration: Long): String {
        val minutes = TimeUnit.SECONDS.toMinutes(duration)
        val seconds = duration - minutes * TimeUnit.MINUTES.toSeconds(1)
        return String.format("%02d:%02d", minutes, seconds)
    }
    private fun preNextSong(increment: Boolean){
        if(increment){
            setSongPosition(increment=true)
            setLayout()
            createMediaPlayer()
        }
        else{
            setSongPosition(increment=false)
            setLayout()
            createMediaPlayer()
        }
    }
    private fun setSongPosition(increment: Boolean){
        if(increment){
            if(musicListPA.size-1 == songPosition){
                songPosition=0
            }
            else
                ++songPosition
        }
        else{
            if(0== songPosition){
                songPosition= musicListPA.size-1
            }
            else
                --songPosition
        }
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as MusicService.MyBinder
        musicService = binder.currentService()
        createMediaPlayer()
        musicService!!.seekBarSetup()



    }

    override fun onServiceDisconnected(name: ComponentName?) {
        musicService=null
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onCompletion(mp: MediaPlayer?) {
        if(!repeat)
        setSongPosition(increment = true)
        createMediaPlayer()
        try {
            setLayout()
        }
        catch (e: Exception)
        {
            return
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 13 || resultCode==RESULT_OK)
            return
    }

    private fun showBottomSheetDialog(){
        val dialog =  BottomSheetDialog(this@PlayerActivity)
        dialog.setContentView(R.layout.bottom_sheet_dialog)
        dialog.show()
        dialog.findViewById<LinearLayout>(R.id.min_15)?.setOnClickListener{
            Toast.makeText(baseContext,"Music will stop after 15 minutes", Toast.LENGTH_SHORT).show()
            binding.timerBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.purple_500))
            min15=true
            Thread{Thread.sleep((15*60000).toLong())
            if(min15) exitApplication()}.start()
            dialog.dismiss()
        }
        dialog.findViewById<LinearLayout>(R.id.min_30)?.setOnClickListener{
            Toast.makeText(baseContext,"Music will stop after 30 minutes", Toast.LENGTH_SHORT).show()
            binding.timerBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.purple_500))
            min30=true
            Thread{Thread.sleep((30*60000).toLong())
                if(min30) exitApplication()}.start()
            dialog.dismiss()
        }
        dialog.findViewById<LinearLayout>(R.id.min_60)?.setOnClickListener{
            Toast.makeText(baseContext,"Music will stop after 60 minutes", Toast.LENGTH_SHORT).show()
            binding.timerBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.purple_500))
            min60=true
            Thread{Thread.sleep((60*60000).toLong())
                if(min60) exitApplication()}.start()
            dialog.dismiss()
        }

    }


}