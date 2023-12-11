package com.safayousif.campmusicplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.safayousif.campmusicplayer.MediaService;
import com.safayousif.campmusicplayer.RecyclerViewInterface;

public abstract class MediaPlaybackActivity extends AppCompatActivity implements RecyclerViewInterface {
    private static final String TAG = "MediaPlaybackActivity";
    private MediaService mediaService;
    private boolean isBound = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Bind Media Service
        Intent intent = new Intent(this, MediaService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    public boolean getIsBound() {
        return isBound;
    }

    public void onItemClick(int position, SongModel songModel) {
        // Start song
        if (mediaService == null)
            mediaService = new MediaService();

        if (MediaStateManager.getInstance().getCurrentSong() == null ||
                !songModel.toString().equals(MediaStateManager.getInstance().getCurrentSong().toString())) {
            mediaService.playSong(songModel.getPath());
        }

        MediaStateManager.getInstance().setCurrentPosition(position);
        MediaStateManager.getInstance().setCurrentSong(songModel);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBound) {
            unbindService(connection);
            isBound = false;
        }
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            MediaService.MediaBinder binder = (MediaService.MediaBinder) service;
            mediaService = binder.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            isBound = false;
        }
    };
}