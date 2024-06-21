package com.example.musicappplayer

import Helper.MusicDatabaseHelper
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicappplayer.databinding.ActivityFavouriteBinding
import com.example.musicappplayer.databinding.ActivitySelectionBinding

class SelectionActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectionBinding
    private lateinit var adapter: MusicAdapter
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= ActivitySelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.selectionRV.setItemViewCacheSize(10)
        binding.selectionRV.setHasFixedSize(true)
        binding.selectionRV.layoutManager= LinearLayoutManager(this)



        adapter = MusicAdapter(this, MainActivity.musicListMA, selectionActivity = true)

        binding.selectionRV.adapter=adapter

            binding.backBtnSA.setOnClickListener{
                val intent = Intent(this, PlaylistDetails::class.java)
                intent.putExtra("index", PlaylistDetails.songPosition)
                startActivity(intent)
                finish() }

        binding.searchViewSA.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                // Xử lý khi người dùng gửi truy vấn tìm kiếm
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                MainActivity.musicListSearch = ArrayList()
                if (newText != null) {
                    val userInput = newText.lowercase()
                    for (song in MainActivity.musicListMA)
                        if (song.title.lowercase().contains(userInput))
                            MainActivity.musicListSearch.add(song)
                    MainActivity.search = true
                    adapter.updateMusicList(searchList = MainActivity.musicListSearch)

                }
                return true
            }
        })
    }
}