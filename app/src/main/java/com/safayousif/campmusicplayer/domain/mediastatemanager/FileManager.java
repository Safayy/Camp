package com.safayousif.campmusicplayer.domain.mediastatemanager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.safayousif.campmusicplayer.domain.mediastatemanager.PlaylistDatabaseHelper;
import com.safayousif.campmusicplayer.domain.model.PlaylistModel;
import com.safayousif.campmusicplayer.domain.model.SongModel;

import java.util.ArrayList;
import java.util.Arrays;

public class FileManager {
    String TAG = "FileManager";
    ArrayList<SongModel> songModels;
    public FileManager(){
    }

    public ArrayList<PlaylistModel> getPlaylists(Context context){
        PlaylistDatabaseHelper playlistDatabaseHelper = new PlaylistDatabaseHelper(context);

        PlaylistModel playlist = new PlaylistModel("Renaissance",
                new ArrayList<>(Arrays.asList(songModels.get(0), songModels.get(8))), context);
        playlistDatabaseHelper.addPlaylist(playlist);

        ArrayList<PlaylistModel> playlistModels = (ArrayList<PlaylistModel>) playlistDatabaseHelper.getAllPlaylists(context);
//        Log.d(TAG, playlistModels.get(0).getPlaylistName());

//        return playlistModels;
        ArrayList<PlaylistModel> mModels = new ArrayList<>();
        mModels.add(playlist);
        return mModels;
    }

    // Prints all file names in the music directory
    public ArrayList<SongModel> getSongFiles(Context context) {
        ArrayList<SongModel> tempSongs = new ArrayList<>();
        String musicFolderPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection  = {
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
        if(cursor != null){
            while(cursor.moveToNext()){
                String albumId = cursor.getString(5);

                SongModel song = new SongModel(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        false, //TODO read from db
                        Long.parseLong(cursor.getString(5))
                );
                tempSongs.add(song);
            }
            cursor.close();
        }
        this.songModels = tempSongs;
        return tempSongs;
    }
}