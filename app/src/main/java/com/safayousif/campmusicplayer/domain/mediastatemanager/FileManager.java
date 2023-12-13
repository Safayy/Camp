package com.safayousif.campmusicplayer.domain.mediastatemanager;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.safayousif.campmusicplayer.domain.model.SongModel;

import java.util.ArrayList;

public class FileManager {
    private static FileManager instance;
    private String TAG = "FileManager";
    private ArrayList<SongModel> songModels;

    private FileManager() {}
    public static FileManager getInstance() {
        if (instance == null) {
            instance = new FileManager();
        }
        return instance;
    }

    public SongModel getSongByIdentifier(String songIdentifier) {
        Log.d(TAG, "getSongByIdentifier: " + songIdentifier);
        if (songModels != null) {
            for (SongModel song : songModels) {
                String currentSongIdentifier = song.getTitle() + song.getDuration();
                if (currentSongIdentifier.equals(songIdentifier)) {
                    Log.d(TAG, "getSongByIdentifier: FOUND:");
                    return song;
                }
            }
        }
        return null;
    }

    // Archive Song
//    public void archiveSong(SongModel songModel){}

    // Get all songs from filesystem
    public ArrayList<SongModel> getSongs(Context context) {
        ArrayList<SongModel> tempSongs = new ArrayList<>();
        String musicFolderPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.AudioColumns.DATA,
                MediaStore.Audio.AudioColumns.TITLE,
                MediaStore.Audio.ArtistColumns.ARTIST,
                MediaStore.Audio.AudioColumns.ALBUM,
                MediaStore.Audio.AudioColumns.DURATION,
                MediaStore.Audio.AudioColumns.ALBUM_ID
        };
        String selection = MediaStore.Audio.Media.DATA + " like ?";
        String[] selectionArgs = new String[]{"%" + musicFolderPath + "%"};

        Cursor cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
        if (cursor != null) {
            DatabaseHelper dbHelper = new DatabaseHelper(context);
            while (cursor.moveToNext()) {
                SongModel song = new SongModel(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        false, // Default value for new songs
                        Long.parseLong(cursor.getString(5))
                );
                dbHelper.addSong(song);
                tempSongs.add(song);
            }
            cursor.close();
        }
        this.songModels = tempSongs;
        printSongModel();
        return tempSongs; //Get Unarchived Songs
    }

    public void printSongModel() {
        Log.d(TAG, "printSongModel: " + songModels.get(0).getTitle()
                + " " + songModels.size());
    }
}