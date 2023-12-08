package com.example.camp;

public class SongModel {
    String title;
    String artist;
    boolean isFavorite;
    int image;

    public SongModel(String title, String artist, boolean isFavorite, int image) {
        this.title = title;
        this.artist = artist;
        this.isFavorite = isFavorite;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public int getImage() {
//        return image;
        return R.drawable.baseline_favorite_24;
    }
}
