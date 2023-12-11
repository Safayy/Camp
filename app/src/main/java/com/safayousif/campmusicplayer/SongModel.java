package com.safayousif.campmusicplayer;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import java.io.InputStream;
import android.graphics.drawable.BitmapDrawable;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.IOException;

public class SongModel implements Parcelable {
    private static final String TAG = "SongModel";
    private String path;
    private String title;
    private String artist;
    private String album;
    private String duration;
    private String playlists[];
    private boolean isFavorite;
    public Bitmap albumArt;
    public long albumId;

    public SongModel(String path, String title, String artist, String album, String duration, boolean isFavorite, long albumId) {
        this.path = path;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
        this.isFavorite = isFavorite; // Default False
        this.albumId = albumId;
    }

    protected SongModel(Parcel in) {
        path = in.readString();
        title = in.readString();
        artist = in.readString();
        album = in.readString();
        duration = in.readString();
        isFavorite = in.readByte() != 0;
        albumId = in.readLong();
    }

    public static final Creator<SongModel> CREATOR = new Creator<SongModel>() {
        @Override
        public SongModel createFromParcel(Parcel in) {
            return new SongModel(in);
        }

        @Override
        public SongModel[] newArray(int size) {
            return new SongModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeString(title);
        dest.writeString(artist);
        dest.writeString(album);
        dest.writeString(duration);
        dest.writeByte((byte) (isFavorite ? 1 : 0));
        dest.writeLong(albumId);
//        dest.writeParcelable(imageUri, flags);
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

    public Bitmap getAlbumArt(Context context) {
        Bitmap albumArt = null;
        try {
            // Use the content resolver to open the input stream
            InputStream is = context.getContentResolver().openInputStream(this.getImageUri());
            if (is != null) {
                // Decode the stream into a bitmap
                albumArt = BitmapFactory.decodeStream(is);
                // Close the stream
                is.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.albumArt = albumArt;
        return albumArt;
    }

    public Drawable getImage(Context context) {
        Bitmap albumArtBitmap = getAlbumArt(context);
        if (albumArtBitmap != null) {
            return new BitmapDrawable(context.getResources(), albumArtBitmap);
        } else {
            return context.getResources().getDrawable(R.drawable.fe_random);
        }
    }

    public String getPath() {
        return path;
    }

    public String getAlbum() {
        return album;
    }

    public String[] getPlaylists() {
        return playlists;
    }

    public String getDuration() {
        return duration;
    }

    public Uri getImageUri() {
        return Uri.parse("content://media/external/audio/albumart/" + albumId);
    }

    @NonNull
    @Override
    public String toString() {
        return "SongModel{" +
                "path='" + path + "'\n" +
                "title='" + title + "'\n" +
                "artist='" + artist + "'\n" +
                "album='" + album + "'\n" +
                "duration='" + duration + "'\n" +
                "isFavorite=" + isFavorite + "'\n" +
                "albumId=" + albumId + "'\n" +
                '}';
    }

    public long getAlbumId() {
        return albumId;
    }
}