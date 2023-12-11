package com.safayousif.campmusicplayer;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class PlaylistModel implements Parcelable {
    private static final String TAG = "PlaylistModel";
    private String playlistName;
    private String artistName;
    private Uri imageUri;
    private ArrayList<SongModel> songs;

    public PlaylistModel(String playlistName, ArrayList<SongModel> songs, Context context) {
        this.playlistName = playlistName;
        this.songs = songs;
        if (songs.size() > 0 && songs.get(0).getImageUri() != null) {
            this.artistName = songs.get(0).getArtist();
            String albumId = songs.get(0).albumId;
            Uri albumArtUri = Uri.parse("content://media/external/audio/albumart/" + albumId);
            this.imageUri = albumArtUri;
        }
    }

    // Other methods remain unchanged

    // Parcelable implementation
    protected PlaylistModel(Parcel in) {
        playlistName = in.readString();
        artistName = in.readString();
        imageUri = in.readParcelable(Uri.class.getClassLoader());
        songs = in.createTypedArrayList(SongModel.CREATOR);
    }

    public static final Creator<PlaylistModel> CREATOR = new Creator<PlaylistModel>() {
        @Override
        public PlaylistModel createFromParcel(Parcel in) {
            return new PlaylistModel(in);
        }

        @Override
        public PlaylistModel[] newArray(int size) {
            return new PlaylistModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(playlistName);
        dest.writeString(artistName);
        dest.writeParcelable(imageUri, flags);
        dest.writeTypedList(songs);
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public String getArtistName() {
        return artistName;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public Drawable getImage(Context context) {
//        Bitmap albumArtBitmap = getAlbumArt(context);
//        Bitmap albumArt = songs.get(0).albumArt;
//        Log.d(TAG, "SM getAlbumArt: " + albumArt.getWidth());
//        if (albumArt != null) {
//            return new BitmapDrawable(context.getResources(), albumArt);
//        } else {
        return context.getResources().getDrawable(
                R.drawable.ic_launcher_background);
//        }
    }

    public ArrayList<SongModel> getSongs() {
        return songs;
    }
}