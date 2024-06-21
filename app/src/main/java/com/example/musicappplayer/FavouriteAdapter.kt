package com.example.musicappplayer


import Model.Music
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicappplayer.databinding.FavouriteViewBinding


class FavouriteAdapter(private val context: Context, private var musicList: ArrayList<Music>):RecyclerView.Adapter<FavouriteAdapter.MyHolder> (){
    class   MyHolder(binding:FavouriteViewBinding):RecyclerView.ViewHolder(binding.root){
        val image =binding.songImgFV
        val name = binding.songNameFV
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(FavouriteViewBinding.inflate(LayoutInflater.from(context),parent,false))

    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.name.text= musicList[position].title
        Glide.with(context)
            .load(getResourceIdFromName(context, musicList[position].image)) // Assuming image is a URL or file path
            .apply(RequestOptions().placeholder(R.drawable.blinding_lights).centerCrop())
            .into(holder.image)
    }


    override fun getItemCount(): Int {
        return musicList.size
    }
    fun getResourceIdFromName(context: Context, resourceName: String): Int {
        return context.resources.getIdentifier(resourceName, "drawable", context.packageName)
    }
}