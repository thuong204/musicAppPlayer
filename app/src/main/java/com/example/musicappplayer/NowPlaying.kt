package com.example.musicappplayer

import Model.setSongPosition
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicappplayer.databinding.FragmentNowPlayingBinding
import java.util.concurrent.TimeUnit

class NowPlaying : Fragment() {
    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: FragmentNowPlayingBinding
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view= inflater.inflate(R.layout.fragment_now_playing, container, false)
        binding = FragmentNowPlayingBinding.bind(view)
        binding.root.visibility=View.INVISIBLE
        binding.playBtnNP.setOnClickListener{
            if(PlayerActivity.isPlaying) pauseMusic()  else playMusic()
        }
        binding.nextBtnNP.setOnClickListener{
            setSongPosition(increment=true)
            PlayerActivity.musicService!!.createMediaPlayer()
            Glide.with(this)
                .load(context?.let { it1 -> getResourceIdFromName(it1, PlayerActivity.musicListPA[PlayerActivity.songPosition].image) }) // Assuming image is a URL or file path
                .apply(RequestOptions().placeholder(R.drawable.blinding_lights).centerCrop())
                .into(binding.songImgNP)
            binding.songNameNP.text = PlayerActivity.musicListPA[PlayerActivity.songPosition].title
            PlayerActivity.musicService!!.showNotification(R.drawable.pause_icon)
            Glide.with(this)
            playMusic()

        }
        binding.root.setOnClickListener{
            val intent = Intent(context,PlayerActivity::class.java)
            intent.putExtra("index",PlayerActivity.songPosition)
            intent.putExtra("class","NowPlaying")
            ContextCompat.startActivity(requireContext(),intent,null)

        }

        return view
    }

    override fun onResume() {
        super.onResume()
        if(PlayerActivity.musicService != null) {
            binding.root.visibility = View.VISIBLE
            binding.songNameNP.isSelected= true

            Glide.with(this)
                .load(context?.let { getResourceIdFromName(it, PlayerActivity.musicListPA[PlayerActivity.songPosition].image) }) // Assuming image is a URL or file path
                .apply(RequestOptions().placeholder(R.drawable.blinding_lights).centerCrop())
                .into(binding.songImgNP)
//            Toast.makeText(context, getResourceIdFromName(PlayerActivity.musicListPA[PlayerActivity.songPosition].image), Toast.LENGTH_SHORT).show()
            binding.songNameNP.text = PlayerActivity.musicListPA[PlayerActivity.songPosition].title
            if (PlayerActivity.isPlaying) binding.playBtnNP.setIconResource(R.drawable.pause_icon)
            else binding.playBtnNP.setIconResource(R.drawable.play_icon)
        }
    }
    private fun getResourceIdFromName(context: Context,resourceName: String): Int {
        return context.resources.getIdentifier(resourceName, "drawable", context.packageName)
    }
    private fun playMusic(){
        PlayerActivity.musicService!!.mediaPlayer!!.start()
        binding.playBtnNP.setIconResource(R.drawable.pause_icon)
        PlayerActivity.musicService!!.showNotification(R.drawable.pause_icon)
        PlayerActivity.binding.nextBtn.setIconResource(R.drawable.pause_icon)
        PlayerActivity.isPlaying=true

    }
    private fun pauseMusic(){
        PlayerActivity.musicService!!.mediaPlayer!!.pause()
        binding.playBtnNP.setIconResource(R.drawable.play_icon)
        PlayerActivity.musicService!!.showNotification(R.drawable.play_icon)
        PlayerActivity.binding.nextBtn.setIconResource(R.drawable.play_icon)
        PlayerActivity.isPlaying=false
    }
    fun formatDuration(duration: Long): String {
        val minutes = TimeUnit.SECONDS.toMinutes(duration)
        val seconds = duration - minutes * TimeUnit.MINUTES.toSeconds(1)
        return String.format("%02d:%02d", minutes, seconds)
    }

}