package com.safayousif.campmusicplayer.domain.model;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import com.safayousif.campmusicplayer.R;

import java.util.ArrayList;

public class PlaylistModel implements Parcelable {
    private static final String TAG = "PlaylistModel";
    private String playlistName;
    private String artistName;
    private ArrayList<SongModel> songs;
    long albumId;

    public PlaylistModel(String playlistName, ArrayList<SongModel> songs, Context context) {
        this.playlistName = playlistName;
        this.songs = songs;
        if (songs.size() > 0 && songs.get(0).getImageUri() != null) {
            this.artistName = songs.get(0).getArtist();
            this.albumId = songs.get(0).getAlbumId();
        }
    }
    public PlaylistModel(Parcel in) {
        playlistName = in.readString();
        artistName = in.readString();
        albumId = in.readLong();
        songs = in.createTypedArrayList(SongModel.CREATOR);
        Log.d(TAG, playlistName + " " + artistName + " " + albumId);
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
        dest.writeLong(albumId);
        dest.writeTypedList(songs);
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public String getArtistName() {
        return artistName;
    }

    public Drawable getImage(Context context) {
        Drawable image = getSongs().get(0).getImage(context);
        if (image != null)
            return image;
        else
            return context.getResources().getDrawable(
            R.drawable.ic_launcher_background);
    }

    public ArrayList<SongModel> getSongs() {
        return songs;
    }

    @NonNull
    @Override
    public String toString() {
        return getPlaylistName() + getArtistName() + getSongs().toString();
    }
}