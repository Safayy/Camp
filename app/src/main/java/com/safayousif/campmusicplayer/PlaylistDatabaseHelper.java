package com.safayousif.campmusicplayer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
public class PlaylistDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "playlist_db";
    private static final int DATABASE_VERSION = 1;

    // Table names
    private static final String TABLE_PLAYLISTS = "playlists";
    private static final String TABLE_SONGS = "songs";

    // Common column names
    private static final String KEY_ID = "id";

    // Playlists table column names
    private static final String KEY_PLAYLIST_NAME = "playlist_name";
    private static final String KEY_PLAYLIST_SONGS = "playlist_songs";

    // Songs table column names
    private static final String KEY_SONG_PATH = "song_path";
    private static final String KEY_SONG_TITLE = "song_title";
    private static final String KEY_SONG_ARTIST = "song_artist";
    private static final String KEY_SONG_ALBUM = "song_album";
    private static final String KEY_SONG_DURATION = "song_duration";
    private static final String KEY_SONG_FAVORITE = "song_favorite";
    private static final String KEY_SONG_ALBUM_ID = "song_album_id";
    private static final String KEY_PLAYLIST_ID = "playlist_id"; //TODO remove

    private static final String CREATE_TABLE_SONGS = "CREATE TABLE " + TABLE_SONGS + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_SONG_PATH + " TEXT,"
            + KEY_SONG_TITLE + " TEXT,"
            + KEY_SONG_ARTIST + " TEXT,"
            + KEY_SONG_ALBUM + " TEXT,"
            + KEY_SONG_DURATION + " TEXT,"
            + KEY_SONG_FAVORITE + " INTEGER,"
            + KEY_SONG_ALBUM_ID + " LONG,"
            + KEY_PLAYLIST_ID + " INTEGER,"
            + "FOREIGN KEY (" + KEY_PLAYLIST_ID + ") REFERENCES " + TABLE_PLAYLISTS + "(" + KEY_ID + ")"
            + ")";


//    private static final String CREATE_TABLE_SONGS = "CREATE TABLE " + TABLE_SONGS + "("
//            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
//            + KEY_SONG_PATH + " TEXT,"
//            + KEY_SONG_TITLE + " TEXT,"
//            + KEY_SONG_ARTIST + " TEXT,"
//            + KEY_SONG_ALBUM + " TEXT,"
//            + KEY_SONG_DURATION + " TEXT,"
//            + KEY_SONG_FAVORITE + " INTEGER,"
//            + KEY_SONG_ALBUM_ID + " INTEGER"
//            + ")";

    // Create table statements
    private static final String CREATE_TABLE_PLAYLISTS = "CREATE TABLE " + TABLE_PLAYLISTS + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_PLAYLIST_NAME + " TEXT,"
            + KEY_PLAYLIST_SONGS + " TEXT"
            + ")";

    private static final String TAG = "PlaylistDatabaseHelper";

    public PlaylistDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_PLAYLISTS);
        db.execSQL(CREATE_TABLE_SONGS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if they exist
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYLISTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SONGS);

        // Create new tables
        onCreate(db);
    }

    // Playlist operations

    public long addPlaylist(PlaylistModel playlist) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_PLAYLIST_NAME, playlist.getPlaylistName());

        String songsJson = convertSongListToJson(playlist.getSongs());
        values.put(KEY_PLAYLIST_SONGS, songsJson);

        return db.insert(TABLE_PLAYLISTS, null, values);
    }

    public List<PlaylistModel> getAllPlaylists(Context context) {
        List<PlaylistModel> playlists = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_PLAYLISTS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            cursor.moveToFirst();
            if (cursor != null && !cursor.isClosed()) {
                do {
                    String jsonPlaylists = cursor.getString(2);
                    ArrayList<SongModel> songModels = new ArrayList<>();
                    try {
                        JSONArray jsonArray = new JSONArray(jsonPlaylists);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonSong = jsonArray.getJSONObject(i);

                            String path = jsonSong.getString("path");
                            String title = jsonSong.getString("title");
                            String artist = jsonSong.getString("artist");
                            String album = jsonSong.getString("album");
                            String duration = jsonSong.getString("duration");
                            String isFavorite = jsonSong.getString("isFavorite");
                            int albumId = Integer.parseInt(jsonSong.getString("albumId"));

                            SongModel songModel = new SongModel(path, title, artist, album,
                                    duration, Boolean.parseBoolean(isFavorite), albumId);

//                            Log.d(TAG, "Song Details: " + songModel.toString());
                            songModels.add(songModel);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //
                    PlaylistModel playlist = new PlaylistModel(
                            cursor.getString(1),
                            songModels,
                            context);
                    playlists.add(playlist);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error while retrieving playlists: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return playlists;
    }

    private String convertSongListToJson(List<SongModel> songs) {
        return new Gson().toJson(songs);
    }

    private List<SongModel> convertJsonToSongList(String json) {
        Log.d(TAG, "convertSongListToJson: " + json);
        Gson gson = new Gson();
        SongModel[] songArray = gson.fromJson(json, SongModel[].class);
        List<SongModel> songs = Arrays.asList(songArray);


//        try {
//            JSONArray jsonArray = new JSONArray(json);
//            ArrayList<String> songTitles = new ArrayList<>();
//
//            for (int i = 0; i < jsonArray.length(); i++) {
//                JSONObject jsonObject = jsonArray.getJSONObject(i);
//
//                String album = jsonObject.getString("album");
//                String artist = jsonObject.getString("artist");
//                String duration = jsonObject.getString("duration");
//                boolean isFavorite = jsonObject.getBoolean("isFavorite");
//                String path = jsonObject.getString("path");
//                String title = jsonObject.getString("title");
//                songTitles.add(title);
//                Log.d(TAG, "convertJsonToSongList: " + title);
//            }
//            return songTitles;
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Log.e("JSON Parsing Error", "Error parsing JSON array: " + e.getMessage());
//        }
        return songs;
    }


    public long addSong(SongModel song, long playlistId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_SONG_PATH, song.getPath());
        values.put(KEY_SONG_TITLE, song.getTitle());
        values.put(KEY_SONG_ARTIST, song.getArtist());
        values.put(KEY_SONG_ALBUM, song.getAlbum());
        values.put(KEY_SONG_DURATION, song.getDuration());
        values.put(KEY_SONG_FAVORITE, song.isFavorite() ? 1 : 0);
        values.put(KEY_SONG_ALBUM_ID, song.getAlbumId());
        values.put(KEY_PLAYLIST_ID, playlistId);  // TODO remove
        return db.insert(TABLE_SONGS, null, values);
    }

    public List<SongModel> getSongsForPlaylist(long playlistId) {
        List<SongModel> songs = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_SONGS + " WHERE " + KEY_PLAYLIST_ID + " = " + playlistId;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                SongModel song = new SongModel(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getInt(5) == 1,
                        Long.parseLong(cursor.getString(6)));

                songs.add(song);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return songs;
    }
}