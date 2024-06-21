package Helper

import Model.Music
import Model.Playlist
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class MusicDatabaseHelper(context: Context) :SQLiteOpenHelper(context, DATABASE_NAME, null,DATABASE_VERSION) {
    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "MusicPlayer.db"

        // Table names
        private const val TABLE_SONGS = "songs"
        private const val TABLE_ALBUMS = "albums"
        private const val TABLE_ARTISTS = "artists"
        private const val TABLE_PLAYLISTS = "playlists"
        private const val TABLE_PLAYLIST_SONGS = "playlist_songs"
        private const val TABLE_FAVORITES="favourites"

        // Common column
        private const val COLUMN_ID = "id"

        // Songs table columns
        private const val COLUMN_SONG_TITLE = "title"
        private const val COLUMN_SONG_ARTIST_ID = "artist_id"
        private const val COLUMN_SONG_ALBUM_ID = "album_id"
        private const val COLUMN_SONG_DURATION = "duration"
        private const val COLUMN_SONG_PATH = "path"
        private const val COLUMN_SONG_ID="song_id"
        private const val COLUMN_PLAYLIST_IMAGE="image_playlist"
        private const val COLUMN_PLAYLIST_CREATE= "create_playlist"

        // Albums table columns
        private const val COLUMN_ALBUM_NAME = "name"
        private const val COLUMN_ALBUM_ARTIST_ID = "artist_id"

        // Artists table columns
        private const val COLUMN_ARTIST_NAME = "name"

        // Playlists table columns
        private const val COLUMN_PLAYLIST_NAME = "name"

        // Playlist_Songs table columns
        private const val COLUMN_PLAYLIST_ID = "playlist_id"
        private const val COLUMN_PLAYLIST_SONG_ID = "song_id"

        // Create table SQL queries
        private const val CREATE_TABLE_SONGS = (
                "CREATE TABLE $TABLE_SONGS (" +
                        "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "$COLUMN_SONG_TITLE TEXT," +
                        "$COLUMN_SONG_ARTIST_ID INTEGER," +
                        "$COLUMN_SONG_ALBUM_ID INTEGER," +
                        "image TEXT,"+
                        "$COLUMN_SONG_DURATION INTEGER," +
                        "$COLUMN_SONG_PATH TEXT," +
                        "FOREIGN KEY($COLUMN_SONG_ARTIST_ID) REFERENCES $TABLE_ARTISTS($COLUMN_ID)," +
                        "FOREIGN KEY($COLUMN_SONG_ALBUM_ID) REFERENCES $TABLE_ALBUMS($COLUMN_ID))"
                )

        private const val CREATE_TABLE_ALBUMS = (
                "CREATE TABLE $TABLE_ALBUMS (" +
                        "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "$COLUMN_ALBUM_NAME TEXT," +
                        "$COLUMN_ALBUM_ARTIST_ID INTEGER," +
                        "FOREIGN KEY($COLUMN_ALBUM_ARTIST_ID) REFERENCES $TABLE_ARTISTS($COLUMN_ID))"
                )

        private const val CREATE_TABLE_ARTISTS = (
                "CREATE TABLE $TABLE_ARTISTS (" +
                        "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "$COLUMN_ARTIST_NAME TEXT)"
                )

        private const val CREATE_TABLE_PLAYLISTS = (
                "CREATE TABLE $TABLE_PLAYLISTS (" +
                        "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "$COLUMN_PLAYLIST_NAME TEXT,"+
                        "$COLUMN_PLAYLIST_IMAGE TEXT,"+
                        "$COLUMN_PLAYLIST_CREATE TEXT)"
                )

        private const val CREATE_TABLE_PLAYLIST_SONGS = (
                "CREATE TABLE $TABLE_PLAYLIST_SONGS (" +
                        "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "$COLUMN_PLAYLIST_ID INTEGER," +
                        "$COLUMN_PLAYLIST_SONG_ID INTEGER," +
                        "FOREIGN KEY($COLUMN_PLAYLIST_ID) REFERENCES $TABLE_PLAYLISTS($COLUMN_ID)," +
                        "FOREIGN KEY($COLUMN_PLAYLIST_SONG_ID) REFERENCES $TABLE_SONGS($COLUMN_ID))"
                )
        private  val CREATE_TABLE_FAVORITES = (
                "CREATE TABLE IF NOT EXISTS $TABLE_FAVORITES (" +
                        "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "$COLUMN_SONG_ID INTEGER," +
                        "FOREIGN KEY($COLUMN_SONG_ID) REFERENCES $TABLE_SONGS($COLUMN_ID))"
                )

        private const val INSERT_INTO_SONGS = """
    INSERT INTO songs (title, artist_id, album_id,duration, image, path) VALUES 
    ('Shape of You', 1, 1, 208,'shape_of_you', 'shape_of_you.mp3'),
    ('Blinding Lights', 2, 2, 262,'blinding_lights', 'blinding_lights.mp3'),
    ('Someone Like You', 3, 3, 428,'someone_like_you', 'someone_like_you.mp3'),
    ('Uptown Funk', 4, 4, 270,'uptown_fun', 'uptown_fun.mp3'),
    ('Havana', 5, 5, 216,'havana', 'havana.mp3'),
    ('Perfect', 1, 1, 263,'perfect', 'perfect.mp3'),
    ('Bad Guy', 6, 6, 194,'bad_guy', 'bad_guy.mp3'),
    ('Rockstar', 7, 7, 218,'rock_star', 'rockstar.mp3'),
    ('Senorita', 8, 8, 191,'senorita', 'senorita.mp3'),
    ('Sunflower', 7, 9, 158,'sunflower', 'sunflower.mp3'),
('Không Thể Say',11,11,227,'khongthesay','khongthesay.mp3'),
('Exit Sign',11,11,202,'exitsign','exit_sign.mp3'),
('NOLOVENOLIVE',11,11,171,'nolovenolive','nolovenolive.mp3'),
('Không Phải Gu',11,11,201,'khongphaigu','khongphaigu.mp3'),
('Nghe Như Tình Yêu',11,11,193,'nghenhutinhyeu','nghenhutinhyeu.mp3'),
('Săp Nổi Tiếng',11,11,222,'sapnoitieng','sapnoitieng.mp3');
"""

        private const val INSERT_INTO_ARTIST = """
    INSERT INTO artists (name) VALUES
    ('Ed Sheeran'),
    ('The Weeknd'),
    ('Adele'),
    ('Mark Ronson'),
    ('Camila Cabello'),
    ('Billie Eilish'),
    ('Post Malone'),
    ('Shawn Mendes'),
    ('Justin Bieber'),
    ('Swae Lee'),
    ('Hiếu Thứ Hai'),
    ('Rose');
"""

        private const val INSERT_INTO_ALBUMS = """
    INSERT INTO albums (name) VALUES
 ('Divide'),
    ('After Hours'),
    ('21'),
    ('Uptown Special'),
    ('Camila'),
    ('When We All Fall Asleep, Where Do We Go?'),
    ('Beerbongs & Bentleys'),
    ('Shawn Mendes'),
    ('Hollywood’s Bleeding'),
    ('Purpose'),
    ('Ai cũng phải bắt đầu từ đâu đó'),
    ('SOUR'),
    ('MONTERO'),
    ('FUCK LOVE'),
    ('positions'),
    ('Tickets To My Downfall'),
    ('Shoot For The Stars Aim For The Moon'),
    ('El Dorado'),
    ('Planet Her');
"""
        private const val INSERT_INTO_PLAYLIST = """
    INSERT INTO playlists(name,image_playlist,create_playlist) VALUES 
          ('Playlist Hiếu Thú Hai','hathuhieu','23 May 2018'),
          ('Top trending ','toptrending','04 Jan 2004'),
          ('Nhạc trẻ 7x 8x 9x','nhactre','23 Feb 2008'),
          ('Playlist KPOP 2023','playlist_kpop','04 Jan 2004'),
          ('Playlist nhạc Hàn Quốc','nhachanquoc','04 Jan 2004'),
          ('Playlist KPOP nổi tiếng','playlist_kpop','04 Jan 2004'),
          ('Chill Hits','chillhits','04 Jan 2004'),
          ('Hot Acoustics','acoustic','04 Jan 2004'),
          ('Alone Again','aloneagain','04 Jan 2004'),
          ('Mega HitMixix','megahitmix','04 Jan 2004');
"""
        private const val INSERT_INTO_PLAYLIST_SONGS = """
    INSERT INTO playlist_songs (playlist_id, song_id) VALUES
    (2, 1), (2, 2), (2, 3), (2, 4), (3, 5), (3, 6), (4, 7), (4, 8), (5, 9), (5, 10),(1,11),(1,12),(1,13),(1,14),(1,15),(1,16);
"""
    }


    override fun onCreate(db: SQLiteDatabase) {
        //tao bang
        db.execSQL(CREATE_TABLE_ARTISTS)
        db.execSQL(CREATE_TABLE_ALBUMS)
        db.execSQL(CREATE_TABLE_SONGS)
        db.execSQL(CREATE_TABLE_PLAYLISTS)
        db.execSQL(CREATE_TABLE_PLAYLIST_SONGS)
        db.execSQL(CREATE_TABLE_FAVORITES)

        //chen du lieu
        db.beginTransaction()
        try {
            db.execSQL(INSERT_INTO_ARTIST)
            db.execSQL(INSERT_INTO_ALBUMS)
            db.execSQL(INSERT_INTO_SONGS)
            db.execSQL(INSERT_INTO_PLAYLIST)
            db.execSQL(INSERT_INTO_PLAYLIST_SONGS)
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SONGS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ALBUMS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ARTISTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PLAYLISTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_FAVORITES")
        // Add more DROP TABLE statements for additional tables

        // Create tables again
        onCreate(db)


    }
    fun getAllMusic(): ArrayList<Music>{
        val musicList = ArrayList<Music>()
        val db =  readableDatabase
        val query = "SELECT songs.id AS song_id, songs.title AS song_title,artists.name AS artist_name,albums.name AS album_name,songs.image,songs.duration, songs.path FROM songs JOIN artists ON songs.artist_id = artists.id JOIN albums ON songs.album_id = albums.id;"
        val cursor = db.rawQuery(query,null)

        while(cursor.moveToNext()){
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("song_id"))
            val title = cursor.getString(cursor.getColumnIndexOrThrow("song_title"))
            val artist = cursor.getString(cursor.getColumnIndexOrThrow("artist_name"))
            val album = cursor.getString(cursor.getColumnIndexOrThrow("album_name"))
            val image = cursor.getString(cursor.getColumnIndexOrThrow("image"))
            val duration = cursor.getInt(cursor.getColumnIndexOrThrow("duration"))
            val path = cursor.getString(cursor.getColumnIndexOrThrow("path"))
            val music = Music(id,title,artist,album,image,duration,path)
            musicList.add(music)
        }
        cursor.close()
        db.close()
        return musicList
    }

    suspend fun getAllMusicFavourite(): ArrayList<Music> = withContext(Dispatchers.IO) {
        val musicFavouriteList = ArrayList<Music>()
        val db = readableDatabase
        val query = """
            SELECT songs.id AS song_id, 
                   songs.title AS song_title, 
                   artists.name AS artist_name, 
                   albums.name AS album_name, 
                   songs.duration, 
                   songs.path,
                   songs.image -- Thêm cột `image` ở đây
            FROM songs 
            JOIN favourites ON songs.id = favourites.song_id 
            JOIN artists ON songs.artist_id = artists.id 
            JOIN albums ON songs.album_id = albums.id;
        """
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("song_id"))
                val title = cursor.getString(cursor.getColumnIndexOrThrow("song_title"))
                val artist = cursor.getString(cursor.getColumnIndexOrThrow("artist_name"))
                val album = cursor.getString(cursor.getColumnIndexOrThrow("album_name"))
                val image = cursor.getString(cursor.getColumnIndexOrThrow("image")) // Đảm bảo lấy cột `image`
                val duration = cursor.getInt(cursor.getColumnIndexOrThrow("duration"))
                val path = cursor.getString(cursor.getColumnIndexOrThrow("path"))

                val music = Music(id, title, artist, album, image, duration, path)
                musicFavouriteList.add(music)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        musicFavouriteList
    }


    fun getAllPlaylist(): ArrayList<Playlist>{
        val playlistList = ArrayList<Playlist>()
        val db =  readableDatabase
        val query = "SELECT * FROM playlists";
        val cursor = db.rawQuery(query,null)

        while(cursor.moveToNext()){
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
            val image_playlist = cursor.getString(cursor.getColumnIndexOrThrow("image_playlist"))
            val create_playlist = cursor.getString(cursor.getColumnIndexOrThrow("create_playlist"))
            val music = Playlist(id,name, image_playlist,create_playlist)
            playlistList.add(music)
        }
        cursor.close()
        db.close()
        return playlistList
    }
    fun getSongsInPlaylist(playlistId: Int): ArrayList<Music> {
        val musicList = ArrayList<Music>()
        val db = readableDatabase
        val query = """
        SELECT songs.id AS song_id, songs.title AS song_title, artists.name AS artist_name, albums.name AS album_name, songs.image, songs.duration, songs.path
        FROM playlist_songs
        JOIN songs ON playlist_songs.song_id = songs.id
        JOIN artists ON songs.artist_id = artists.id
        JOIN albums ON songs.album_id = albums.id
        WHERE playlist_songs.playlist_id = ?
    """
        val cursor = db.rawQuery(query, arrayOf(playlistId.toString()))

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("song_id"))
            val title = cursor.getString(cursor.getColumnIndexOrThrow("song_title"))
            val artist = cursor.getString(cursor.getColumnIndexOrThrow("artist_name"))
            val album = cursor.getString(cursor.getColumnIndexOrThrow("album_name"))
            val image = cursor.getString(cursor.getColumnIndexOrThrow("image"))
            val duration = cursor.getInt(cursor.getColumnIndexOrThrow("duration"))
            val path = cursor.getString(cursor.getColumnIndexOrThrow("path"))

            val music = Music(id, title, artist, album, image, duration, path)
            musicList.add(music)
        }
        cursor.close()
        db.close()
        return musicList
    }

    fun saveFavoriteSong(songId: Int) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_SONG_ID, songId)
        db.insert(TABLE_FAVORITES, null, values)
        db.close()
    }
    fun addSongToPlaylist(songId: Int, playlistId: Int) {
        val db = writableDatabase
        val query = """
                INSERT INTO playlist_songs (song_id, playlist_id) VALUES (?, ?)
        """
        val statement = db.compileStatement(query)
        statement.bindLong(1, songId.toLong())
        statement.bindLong(2, playlistId.toLong())
        statement.executeInsert()
        db.close()
    }
    fun checkSongInPlaylist(songId: Int , playlistId: Int): Boolean {
        val db = readableDatabase
        val query = """
        SELECT COUNT(*) FROM $TABLE_PLAYLIST_SONGS 
        WHERE $COLUMN_PLAYLIST_SONG_ID = ? AND $COLUMN_PLAYLIST_ID = ?
    """
        val cursor = db.rawQuery(query, arrayOf(songId.toString(),playlistId.toString()))

        var exists = false
        if (cursor.moveToFirst()) {
            val count = cursor.getInt(0)
            exists = count > 0
        }

        cursor.close()
        db.close()
        return exists
    }
    fun deleteAllSongsFromPlaylist(playlistId: Int) {
        val db = writableDatabase
        db.delete("playlist_songs", "playlist_id=?", arrayOf(playlistId.toString()))
        db.close()
    }
    fun deleteFavoriteSong(songId: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_FAVORITES, "$COLUMN_SONG_ID=?", arrayOf(songId.toString()))
        db.close()
    }
    fun isFavoriteSong(songId: Int): Boolean {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_FAVORITES,
            arrayOf(COLUMN_SONG_ID),
            "$COLUMN_SONG_ID=?",
            arrayOf(songId.toString()),
            null, null, null
        )
        val isFavorite = cursor.count > 0
        cursor.close()
        db.close()
        return isFavorite
    }
}