package Model

import com.example.musicappplayer.PlayerActivity
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

class Music(var id:Int, val title:String, val album:String, val artist:String, val image:String, val duration:Int, val path:String) {

    class Playlist{
        lateinit var name:String
        lateinit var playlist: ArrayList<Music>
        lateinit var createdBy: String
        lateinit var createdOn: String
    }
    class MusicPlaylist{
        var ref: ArrayList<Model.Playlist> = ArrayList()


    }

}
fun setSongPosition(increment: Boolean){
       if(increment){
           if(PlayerActivity.musicListPA.size-1 == PlayerActivity.songPosition){
               PlayerActivity.songPosition =0
           }
           else
               ++PlayerActivity.songPosition
       }
       else{
           if(0== PlayerActivity.songPosition){
               PlayerActivity.songPosition = PlayerActivity.musicListPA.size-1
           }
           else
               --PlayerActivity.songPosition
       }
}
fun exitApplication(){
    if (PlayerActivity.musicService != null) {
        PlayerActivity.musicService!!.stopForeground(true)
        PlayerActivity.musicService!!.mediaPlayer!!.release()
        exitProcess(1)
    }
}



