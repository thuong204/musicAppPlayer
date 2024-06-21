package com.example.musicappplayer

import Helper.MusicDatabaseHelper
import Model.Music
import Model.exitApplication
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicappplayer.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var musicAdapter: MusicAdapter
    private lateinit var musicDatabaseHelper: MusicDatabaseHelper
    private lateinit var sharedPreferences: SharedPreferences
    companion object{
        lateinit var musicListMA: ArrayList<Music>
        lateinit var musicListSearch : ArrayList<Music>
        var search: Boolean = false



    }

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

            setTheme(R.style.Theme_MusicAppPlayer)
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)

            if (requestRuntimePermission())
                initializelayout()
            binding.shufflebtn.setOnClickListener {
                val intent = Intent(this@MainActivity, PlayerActivity::class.java)
                intent.putExtra("index", 0)
                intent.putExtra("class", "MainActivity")
                startActivity(intent)
            }

            binding.favouritebtn.setOnClickListener {
                val intent = Intent(this@MainActivity, FavouriteActivity::class.java)
                startActivity(intent)
            }
            binding.playlistbtn.setOnClickListener {
                val intent = Intent(this@MainActivity, PlaylistActivity::class.java)
                startActivity(intent)
            }
            binding.navView.setNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.navFeeback -> Toast.makeText(baseContext, "Feedback", Toast.LENGTH_SHORT)
                        .show()

                    R.id.navAbout -> Toast.makeText(baseContext, "About", Toast.LENGTH_SHORT).show()
                    R.id.navSettings -> Toast.makeText(baseContext, "Settings", Toast.LENGTH_SHORT)
                        .show()

                    R.id.navExit -> {
                        val builder = MaterialAlertDialogBuilder(this)
                        builder.setTitle("Exit")
                            .setMessage("Do you want to close app?")
                            .setPositiveButton("Yes") { _, _ ->
                                if (!PlayerActivity.isPlaying && PlayerActivity.musicService != null) {
                                    PlayerActivity.musicService!!.stopForeground(true)
                                    PlayerActivity.musicService!!.mediaPlayer!!.release()
                                    exitProcess(1)
                                } else {
                                    PlayerActivity.musicService!!.stopForeground(true)
                                    exitProcess(1)
                                }

                            }
                            .setNegativeButton("No") { dialog, _ ->
                                dialog.dismiss()
                            }
                        val customDialog = builder.create()
                        customDialog.show()
                        customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
                        customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
                    }
                }
                true
            }
    }

    // For requewst permission
    private fun requestRuntimePermission() :Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                13
            )
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 13) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                initializelayout()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    13
                )
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item))
            return true
        return super.onOptionsItemSelected(item)
    }
    private fun initializelayout(){


        setSupportActionBar(binding.toolbar)

        toggle = ActionBarDrawerToggle(this, binding.root,binding.toolbar, R.string.open, R.string.close)
        binding.root.addDrawerListener(toggle)
        toggle.syncState()

        search = false
        musicDatabaseHelper = MusicDatabaseHelper(this)
        musicListMA = musicDatabaseHelper.getAllMusic()

        binding.musicRV.setHasFixedSize(true)
        binding.musicRV.setItemViewCacheSize(13)
        binding.musicRV.layoutManager=LinearLayoutManager(this@MainActivity)
        musicAdapter = MusicAdapter(this@MainActivity,musicListMA)
        binding.musicRV.adapter= musicAdapter
        binding.totalSong.text="Total Songs: "+ musicAdapter.itemCount
    }

    override fun onDestroy() {
        super.onDestroy()
       exitApplication()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_view_menu, menu)
        val searchView = menu?.findItem(R.id.searchView)?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Xử lý khi người dùng gửi truy vấn tìm kiếm
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                musicListSearch= ArrayList()
                if(newText!= null){
                    val userInput = newText.lowercase()
                    for(song in musicListMA)
                        if(song.title.lowercase().contains(userInput))
                            musicListSearch.add(song)
                    search = true
                    musicAdapter.updateMusicList(searchList = musicListSearch)

                }
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }
    private fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("is_logged_in", false)
    }

}
