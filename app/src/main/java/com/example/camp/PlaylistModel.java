package com.example.camp;

import androidx.annotation.NonNull;

public class PlaylistModel {
    String playlistName;
    String artistName; //TODO make array
    int image;

    public PlaylistModel(String playlistName, String artistName, int image) {
        this.playlistName = playlistName;
        this.artistName = artistName;
        this.image = image;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public String getArtistName() {
        return artistName;
    }

    public int getImage() {
//        return image;
        return R.drawable.baseline_favorite_24;
    }

    @NonNull
    @Override
    public String toString() {
        return getPlaylistName() + " " +
                getArtistName() + " " +
                getImage();
    }
}
