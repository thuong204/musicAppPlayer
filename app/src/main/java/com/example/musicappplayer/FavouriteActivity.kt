package com.example.musicappplayer

import Helper.MusicDatabaseHelper
import Model.Music
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.musicappplayer.databinding.ActivityFavouriteBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavouriteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavouriteBinding
    private lateinit var favouriteAdapter: FavouriteAdapter
    private lateinit var dbHelper: MusicDatabaseHelper

    companion object{
        lateinit var tempList: ArrayList<Music>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_MusicAppPlayer)
        binding=ActivityFavouriteBinding.inflate(layoutInflater)
        setContentView(binding.root)


        dbHelper = MusicDatabaseHelper(this)


        binding.favouriteRV.setHasFixedSize(true)
        binding.favouriteRV.setItemViewCacheSize(13)
        binding.favouriteRV.layoutManager= GridLayoutManager(this,4)
        loadFavouriteMusic()

        binding.backBtnFA.setOnClickListener{finish()}

        binding.shuffleBtnFA.setOnClickListener{
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra("index",0)
            intent.putExtra("class","FavouriteShuffle")
            startActivity(intent)


        }
    }

    private fun loadFavouriteMusic() {
        CoroutineScope(Dispatchers.Main).launch {
            tempList = withContext(Dispatchers.IO) {
                dbHelper.getAllMusicFavourite()
            }
            val musicListPA = ArrayList(tempList)

            // Khởi tạo adapter và gán dữ liệu cho RecyclerView sau khi đã lấy được dữ liệu từ cơ sở dữ liệu
            favouriteAdapter = FavouriteAdapter(this@FavouriteActivity, musicListPA)
            binding.favouriteRV.adapter = favouriteAdapter
        }
    }

}